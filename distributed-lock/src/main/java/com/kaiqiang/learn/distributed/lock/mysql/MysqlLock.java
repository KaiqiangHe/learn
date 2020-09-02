package com.kaiqiang.learn.distributed.lock.mysql;

import com.kaiqiang.learn.distributed.lock.Lock;
import com.kaiqiang.learn.distributed.lock.util.HostUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * // TODO: 2020/8/30  1. 删除过期锁的线程; 2. 测试(事务、正确性、压测); 3. 整理文档;
 * // TODO: 2020/9/1
 * 4. 死锁问题仍然未解决, 参考mysql官网死锁举例：https://dev.mysql.com/doc/refman/5.7/en/innodb-locks-set.html
 * 方案可以参考：让对同一lockKey的写操作串行化
 * 仍未解决死锁，问题可能出现在gap锁，尝试设置事务隔离级别为RC后，仍有gap锁
 *
 * @Author kaiqiang
 * @Date 2020/08/30
 */
public class MysqlLock implements Lock {

    private static final Logger log = LoggerFactory.getLogger(MysqlLock.class);

    private static volatile MysqlLockDao dao = null;
    public static void setMysqlLockDao(MysqlLockDao dao) {
        if(dao == null) {
            throw new IllegalArgumentException("Parameter 'dao' should not be null.");
        }
        MysqlLock.dao = dao;
    }

    private static volatile TxSupport txSupport;

    public static void setTxSupport(TxSupport txSupport) {
        if(txSupport == null) {
            throw new IllegalArgumentException("Parameter 'txSupport' should not be null.");
        }
        MysqlLock.txSupport = txSupport;
    }

    /**
     * 锁最小过期时间 10s
     */
    private static final int MIN_EXPIRE_SECONDS = 10;

    private String key;

    /**
     * 失效时间 防止死锁 单位s
     */
    private int expireSeconds;

    private int dbSerializeId;

    private MysqlLock() { }

    /**
     *
     * @param key not empty
     * @param expireSeconds >= {@link MysqlLock#MIN_EXPIRE_SECONDS}
     * @return
     */
    public static MysqlLock create(String key, int expireSeconds) {
        if(StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Parameter 'key' should not empty.");
        }
        if(expireSeconds < MIN_EXPIRE_SECONDS) {
            throw new IllegalArgumentException("Parameter 'expireSeconds' should >= " + MIN_EXPIRE_SECONDS);
        }
        MysqlLock lock = new MysqlLock();
        lock.expireSeconds = expireSeconds;
        lock.key = key;
        lock.dbSerializeId = getDBSerializeId(lock.key);
        return lock;
    }

    @Override
    public boolean tryLock() {
        String threadId = currentThreadId();
        try {
            SimpleLock dbLock = dao.selectByLockKey(key);
            // 可重入
            if(dbLock != null) {
                return key.equals(dbLock.getLockKey());
            } else {
                txSupport.executeWithReadCommittedTx((v) -> {
                    SimpleLock newLock = new SimpleLock();
                    newLock.setLockKey(key);
                    newLock.setThreadId(threadId);
                    newLock.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
                    serializeStart();
                    dao.insert(newLock);
                });
                return true;
            }
        } catch (DuplicateKeyException e) {
            log.info("DuplicateException {} lock = {}", threadId, this);
            // empty body
        } catch (Exception e) {
            log.error("tryLock失败[{}], currentThread = {}", key, threadId, e);
        }
        return false;
    }

    private static final int RETRY_TIMES = 3;
    @Override
    public void unlock() {
        String threadId = currentThreadId();
        for (int i = 0; i < RETRY_TIMES; i++) {
            if(unlock0(threadId)) {
                return ;
            }
        }
        log.error("unlock失败[{}], 重试{}次后放弃, currentThread = {}", key, RETRY_TIMES, threadId);
    }
    private boolean unlock0(String threadId) {
        try {
            SimpleLock dbLock = dao.selectByLockKeyThreadId(key, threadId);
            if(dbLock != null) {
                txSupport.executeWithReadCommittedTx((v) -> {
                    serializeStart();
                    dao.batchDelete(Collections.singletonList(dbLock.getId()));
                });
            }
            return true;
        } catch (Exception e) {
            log.warn("unlock失败[{}], currentThread = {}", key, threadId, e);
            return false;
        }
    }

    /**
     *
     */
    public void serializeStart() {
        dao.selectForUpdate(dbSerializeId);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * 当前线程id, 格式为: ip#pid@thread-name
     */
    private String currentThreadId() {
        return HostUtils.CURRENT_MACHINE_IP_STR + '#' + HostUtils.PID + '@' + Thread.currentThread().getName();
    }

    // --------------------------------------------------------------------------------
    // 移除过期锁的线程
    private static final class RemoveExpireLock implements Runnable {

        private static final int BATCH_REMOVE_SIZE = 5;

        @Override
        public void run() {
            while(true) {
                LocalDateTime time = LocalDateTime.now();
                try {
                    log.info("移除过期锁execute...");
                    List<Long> ids = dao.selectExpireLock(-1, time, BATCH_REMOVE_SIZE);
                    while(CollectionUtils.isNotEmpty(ids)) {
                        dao.batchDelete(ids);
                        long startId = ids.get(ids.size() - 1);
                        log.info("移除过期锁{}个", ids.size());
                        ids = dao.selectExpireLock(startId, time, BATCH_REMOVE_SIZE);
                    }
                } catch (Exception e) {
                    log.error("移除过期锁异常 ", e);
                }

                try {
                    Thread.sleep(1000); // 每1s扫描一次
                } catch (InterruptedException e) {
                    log.error("InterruptedException ", e);
                }
            }
        }
    }

    static {
        try {
            Thread t = new Thread(new RemoveExpireLock());
            t.setName("remove-expire-lock-thread");
            t.setDaemon(true);
            //t.start();
        } catch (Exception e) {
            throw new RuntimeException("启动移除过期锁的线程失败", e);
        }
    }
    // -----------------------------------------------------------------------------------

    private static final int DB_SERIALIZE_COUNT = 1000;
    private static int getDBSerializeId(String lockKey) {
        return Math.abs(lockKey.hashCode()) % DB_SERIALIZE_COUNT;
    }

    @Override
    public String toString() {
        return "MysqlLock{" +
                "key='" + key + '\'' +
                ", expireSeconds=" + expireSeconds +
                '}';
    }
}
