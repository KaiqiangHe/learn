package com.kaiqiang.learn.distributed.lock.mysql;

import com.kaiqiang.learn.distributed.lock.Lock;
import com.kaiqiang.learn.distributed.lock.util.HostUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * // TODO: 2020/8/30  测试
 *
 * @Author kaiqiang
 * @Date 2020/08/30
 */
public class MysqlLock implements Lock {

    private static final Logger log = LoggerFactory.getLogger(MysqlLock.class);

    private static volatile MysqlLockDao dao = null;
    public synchronized static void setMysqlLockDao(MysqlLockDao dao) {
        if(dao == null) {
            throw new IllegalArgumentException("Parameter 'dao' should not be null.");
        }
        MysqlLock.dao = dao;
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

    private MysqlLock() { }

    /**
     *
     * @param key not empty
     * @param dao not null
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
        return lock;
    }

    @Override
    public synchronized boolean tryLock() {
        String threadId = currentThreadId();
        try {
            SimpleLock dbLock = dao.selectByLockKey(key);
            // 可重入
            if(dbLock != null) {
                return key.equals(dbLock.getLockKey());
            } else {
                dbLock = new SimpleLock();
                dbLock.setLockKey(key);
                dbLock.setThreadId(threadId);
                dbLock.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
                dao.insert(dbLock);
                return true;
            }
        } catch (DuplicateKeyException e) {
            log.error("DuplicateException {} lock = {}", threadId, this);
            // empty body
        } catch (Exception e) {
            log.error("tryLock失败[{}], currentThread = {}", key, threadId, e);
        }
        return false;
    }

    private static final int RETRY_TIMES = 3;
    @Override
    public synchronized void unlock() {
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
            SpringTxUtil.executeWithTx((v) -> {
                SimpleLock dbLock = dao.selectByLockKeyForUpdate(key, threadId);
                if(dbLock != null) {
                    dao.batchDelete(Collections.singletonList(dbLock.getId()));
                }
            });
            return true;
        } catch (Exception e) {
            log.warn("unlock失败[{}], currentThread = {}", key, threadId, e);
            return false;
        }
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

    @Override
    public String toString() {
        return "MysqlLock{" +
                "key='" + key + '\'' +
                ", expireSeconds=" + expireSeconds +
                '}';
    }
}
