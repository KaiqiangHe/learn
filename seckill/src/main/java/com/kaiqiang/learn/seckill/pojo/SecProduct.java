package com.kaiqiang.learn.seckill.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 秒杀商品信息
 *
 * @Author kaiqiang
 * @Date 2020/09/11
 */
@Data
public class SecProduct {

    private String id;
    private String detail;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
