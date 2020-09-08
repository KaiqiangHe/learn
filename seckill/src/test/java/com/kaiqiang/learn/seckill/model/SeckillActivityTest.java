package com.kaiqiang.learn.seckill.model;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeckillActivityTest extends SpringTestSupport {

    private static final Logger log = LoggerFactory.getLogger(SeckillActivityTest.class);

    @Test
    public void testAddUseCount() {
        SeckillActivity activity = SeckillActivity.initProductActivity("apple");
        for (int i = 0; i < 6000; i++) {
            log.info("add use count {}", activity.addUseStock(1));
        }

        log.info("end----------------------------------");
    }
}