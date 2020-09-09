package com.kaiqiang.learn.seckill.service;

/**
 * @Author kaiqiang
 * @Date 2020/09/09
 */
public class SimpleIdGenerator implements IdGenerator {

    @Override
    public long getNextId() {
        return System.nanoTime();
    }
}
