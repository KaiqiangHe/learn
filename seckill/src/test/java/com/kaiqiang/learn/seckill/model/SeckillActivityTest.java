package com.kaiqiang.learn.seckill.model;

import com.kaiqiang.learn.seckill.SpringTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeckillActivityTest extends SpringTestSupport {

    private static final Logger log = LoggerFactory.getLogger(SeckillActivityTest.class);

    @Test
    public void testCreateActivity() {
        SeckillActivity.createSecActivity(
                "apple", "apple秒杀",
                Arrays.asList(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000));
    }

    @Test
    public void testAddUseCount() {
        String activityId = SeckillActivity.createSecActivity(
                "apple", "apple秒杀",
                Arrays.asList(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000));
        SeckillActivity activity = SeckillActivity.initActivity(activityId);
        long start = System.currentTimeMillis();
        /*for (int i = 0; i < 6000; i++) {
            log.info("add use count {}", activity.addUseStock(1));
        }*/

        for (int i = 0; i < 2000; i++) {
            activity.createOrder("sfjslfjljlwejfewofoew" + i);
        }
        log.info("end----------------------------------");
        log.info("time = {}", System.currentTimeMillis() - start);
    }

    /**
     * nThread  nStock      stockCount      time    t per second
     * 100      5           5000            48068   520
     * 100      10          5000            50727
     */
    @Test
    public void test() throws InterruptedException {
        int nThread = 80;
        int nStock = 200;
        int stockCount = 5000;

        List<Integer> stocks = new ArrayList<>();
        for (int i = 0; i < nStock; i++) {
            stocks.add(stockCount);
        }
        String activityId = SeckillActivity.createSecActivity("banana", "测试", stocks);
        SeckillActivity activity = SeckillActivity.initActivity(activityId);
        List<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i < nThread; i++) {
            runnables.add(() -> {
                try {
                    for (int j = 0; j < stockCount * nStock / nThread + nThread; j++) {
//                        log.info("add use count {}", activity.addUseStock(1));
//                        activity.addUseStock(1);
                        activity.createOrder("sfjslfjljlwejfewofoew" + j);
                    }
                } catch (Exception e) {
                    log.error("exception ", e);
                }
            });
        }

        threadTestSupport(runnables);
    }

    private void threadTestSupport(List<Runnable> runnables) throws InterruptedException {
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
}