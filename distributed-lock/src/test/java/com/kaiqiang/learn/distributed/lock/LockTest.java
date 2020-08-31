package com.kaiqiang.learn.distributed.lock;

import com.kaiqiang.learn.distributed.lock.mysql.MysqlLock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LockTest extends SpringTestSupport {

    private static final Logger log = LoggerFactory.getLogger(LockTest.class);

    @Test
    public void test() throws InterruptedException {
        int nThread = 1;
        int lockCount = 2000;

        List<LockTestThread> runnables = new ArrayList<>();
        for (int i = 0; i < nThread; i++) {
            List<String> lockKeys = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                lockKeys.add("lockKey" + i + "-" + j);
            }
            runnables.add(new LockTestThread(lockKeys, lockCount));
        }

        long start = System.currentTimeMillis();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < runnables.size(); i++) {
            Thread t = new Thread(runnables.get(i), "test-thread-" + i);
            Thread.sleep(10);
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            t.join();
        }
        log.info("------------------------------------------------");
        log.info("time = {}", System.currentTimeMillis() - start);
    }



    static class LockTestThread implements Runnable {

        private final List<String> lockKeys;
        private final int lockTestCount;
        private long acquireLockCount = 0;

        public LockTestThread(List<String> lockKeys, int lockTestCount) {
            if(lockKeys == null || lockKeys.isEmpty()) {
                throw new IllegalArgumentException("lockKeys 不能为空");
            }
            this.lockKeys = lockKeys;
            this.lockTestCount = lockTestCount;
        }

        @Override
        public void run() {
            int index = 0;
            while(acquireLockCount < lockTestCount) {
                String lockKey = lockKeys.get(index);
                Lock lock = MysqlLock.create(lockKey, 20);
                boolean locked = lock.tryLock();
                if(locked) {
                    log.info("获得锁成功, lock = {}", lock);
                    acquireLockCount ++;
                    String tName = Thread.currentThread().getName();
                    try {
                        mockExecute();
                    } finally {
                        log.info("解锁成功, lock = {}", lock);
                        lock.unlock();
                    }
                } else {
                    log.info("获得锁失败, lock = {}", lock);
                }

                index++;
                if(index >= lockKeys.size()) {
                    index = 0;
                }
            }
        }

        private void mockExecute() {
//            log.info("mock execute...");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}