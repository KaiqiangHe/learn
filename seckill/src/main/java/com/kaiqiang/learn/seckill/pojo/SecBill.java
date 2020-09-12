package com.kaiqiang.learn.seckill.pojo;

import lombok.Data;

/**
 * 账单表
 *
 * @Author kaiqiang
 * @Date 2020/09/11
 */
@Data
public class SecBill {

    private Long id;
    private String billNo;
    private String price;
    private String account;

}
