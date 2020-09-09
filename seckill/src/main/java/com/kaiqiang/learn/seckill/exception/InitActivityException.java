package com.kaiqiang.learn.seckill.exception;

/**
 * 初始化活动异常
 *
 * @Author kaiqiang
 * @Date 2020/09/09
 */
public class InitActivityException extends RuntimeException {

    public InitActivityException(String message) {
        super(message);
    }

    public InitActivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
