package com.kaiqiang.learn.seckill.service;

import com.kaiqiang.learn.seckill.service.impi.HutooIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class IdGeneratorTest {

    private static final IdGenerator idGenerator = new HutooIdGenerator();

    @Test
    void testPerformance() {
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            idGenerator.getNextId();
        }
        long time = System.currentTimeMillis() - start;
        log.info("count = {}, time = {}ms, {}/ms", count, time, count / time);
    }

    @Test
    void testCorrect() {



    }
}