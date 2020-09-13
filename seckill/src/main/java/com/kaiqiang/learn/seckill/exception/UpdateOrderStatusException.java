package com.kaiqiang.learn.seckill.exception;

/**
 * 更新订单状态异常
 *
 * @Author kaiqiang
 * @Date 2020/09/13
 */
public class UpdateOrderStatusException extends RuntimeException {

    private String orderNo;

    public UpdateOrderStatusException(String orderNo, String message) {
        super(message);
        this.orderNo = orderNo;
    }

    public UpdateOrderStatusException(String orderNo, String message, Throwable cause) {
        super(message, cause);
        this.orderNo = orderNo;
    }
}
