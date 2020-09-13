package com.kaiqiang.learn.seckill.exception;

/**
 * 扣减库存失败
 *
 * @Author kaiqiang
 * @Date 2020/09/13
 */
public class StockNotEnough extends RuntimeException {
    public StockNotEnough(String message) {
        super(message);
    }

    public StockNotEnough(String message, Throwable cause) {
        super(message, cause);
    }
}
