package com.kaiqiang.learn.seckill.db.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账单表
 *
 * @Author kaiqiang
 * @Date 2020/09/11
 */
@Data
public class SecBill {

    private Long id;
    private String userId;
    private String billNo;
    private String price;
    private String ext;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
