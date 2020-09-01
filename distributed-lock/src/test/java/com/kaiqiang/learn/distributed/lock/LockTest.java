package com.kaiqiang.learn.distributed.lock;

import com.kaiqiang.learn.distributed.lock.mysql.MysqlLock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LockTest extends SpringTestSupport {

    private static final Logger log = LoggerFactory.getLogger(LockTest.class);

    @Test
    public void test2() throws InterruptedException {
        int nThreadPerKey = 10;
        int keyCount = 5;

        List<LockTestThread> runnables = new ArrayList<>();
        long time = System.currentTimeMillis();
        for (int i = 0; i < keyCount; i++) {
            List<String> lockKeys = Collections.singletonList("lockKey-" + time + "-" + i);
            for (int j = 0; j < nThreadPerKey; j++) {
                runnables.add(new LockTestThread(lockKeys, 2000));
            }
        }

        lockTestSupport(runnables);
    }

    @Test
    public void test() throws InterruptedException {
        int nThread = 1;
        int lockCount = 2000;

        List<LockTestThread> runnables = new ArrayList<>();
        long time = System.currentTimeMillis();
        for (int i = 0; i < nThread; i++) {
            List<String> lockKeys = new ArrayList<>();
            for (int j = 0; j < 1000; j++) {
                lockKeys.add("lockKey" + "-" + time + "-" + i + "-" + j);
            }
            runnables.add(new LockTestThread(lockKeys, lockCount));
        }

        lockTestSupport(runnables);
    }

    private void lockTestSupport(List<LockTestThread> runnables) throws InterruptedException {
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
        private final int totalTestCount;
        private long alreadyLockCount = 0;

        public LockTestThread(List<String> lockKeys, int totalTestCount) {
            if(lockKeys == null || lockKeys.isEmpty()) {
                throw new IllegalArgumentException("lockKeys 不能为空");
            }
            this.lockKeys = lockKeys;
            this.totalTestCount = totalTestCount;
        }

        @Override
        public void run() {
            int index = 0;
            String tName = Thread.currentThread().getName();
            while(alreadyLockCount < totalTestCount) {
                String lockKey = lockKeys.get(index);
                Lock lock = MysqlLock.create(lockKey, 30);
                boolean locked = lock.tryLock();
                if(locked) {
                    //log.info("获得锁成功, lock = {}, tName = {}", lock, tName);
                    alreadyLockCount++;
                    try {
                        sleep(10, 100);
                    } finally {
//                        log.info("解锁成功, lock = {}, tName = {}", lock, tName);
                        lock.unlock();
                    }
                } else {
                    sleep(10, 20);
//                    log.info("获得锁失败, lock = {}, tName = {}", lock, tName);
                }

                index++;
                if(index >= lockKeys.size()) {
                    index = 0;
                }
            }
        }

        private void sleep(int basic, int random) {
//            log.info("mock execute...");
            try {
                Thread.sleep(basic + ThreadLocalRandom.current().nextInt(random));
            } catch (InterruptedException e) {
                log.error("InterruptedException ", e);
            }
        }
    }

}