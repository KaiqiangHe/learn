package com.kaiqiang.learn.seckill.db.pojo;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 *
 * 疑问：何时扣库存？生单还是支付前
 *
 * 状态流转
 * 后端生单 -> 前端调起支付 -> 用户发起支付 -> 支付前校验(扣库存) -> 支付成功 -> 支付回调后端 -> 成功
 *
 * (订单初始状态)待付款 -> 库存扣减成功 -> 支付成功(失败)
 *
 * @Author kaiqiang
 * @Date 2020/09/11
 */
@Alias("SecOrder")
@Data
public class SecOrder {

    private Long id;
    private String userId;
    private String activityId;
    private String orderNo;
    private String billNo;
    private String productId;

    // 商品数
    private Integer secCount;

    /**
     * 待支付，已扣减库存，支付成功，支付失败，待支付超时，已扣减库存后超时
     * PRE_PAY      ->      STOCK_DEDUCTED      ->      PAY_SUCCESS or PAY_FAILED
     *    |                     |
     * PRE_PAY_TIMEOUT      AFTER_DEDUCTED_TIMEOUT
     */
    public static final int PRE_PAY = 1, PRE_PAY_TIMEOUT = 3, STOCK_DEDUCTED = 2, AFTER_DEDUCTED_TIMEOUT = 3, PAY_SUCCESS = 4, PAY_FAILED = 5;
    private int orderStatus;

    // 生单时间
    private LocalDateTime initOrderTime;
    // 待支付过期时间
    private LocalDateTime prePayTimeout;
    // 库存扣减时间
    private LocalDateTime deductStockTime;
    // 库存扣减后订单支付超时时间
    private LocalDateTime afterDeductTimeout;
    // 支付回调时间
    private LocalDateTime payCallbackTime;

    private String ext;

    // --------------------------------------------------
    // 当订单处于PAY_SUCCESS时，回调业务线接口
    private int callbackCount;
    private LocalDateTime lastCallbackTime;
    private boolean callbackSuccess;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
