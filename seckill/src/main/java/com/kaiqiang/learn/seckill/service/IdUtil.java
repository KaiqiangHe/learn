package com.kaiqiang.learn.seckill.service;

import com.kaiqiang.learn.seckill.service.impi.HutooIdGenerator;

/**
 * @Author kaiqiang
 * @Date 2020/09/12
 */
public class IdUtil {

    private static final IdGenerator idGenerator = new HutooIdGenerator();

    public static String getOrderNo() {
        return "S_" + idGenerator.getNextId();
    }

    public static String getBillNo() {
        return "B_" + idGenerator.getNextId();
    }

    public static String getNextId() {
        return  String.valueOf(idGenerator.getNextId());
    }

}
