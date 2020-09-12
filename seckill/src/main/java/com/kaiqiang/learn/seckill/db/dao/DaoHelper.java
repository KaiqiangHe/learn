package com.kaiqiang.learn.seckill.db.dao;

/**
 * @Author kaiqiang
 * @Date 2020/09/12
 */
public class DaoHelper {

    public static final int TABLE_COUNT = 2;

    public static int getTableIndex(String userId) {
        return Math.abs(userId.hashCode()) % TABLE_COUNT;
    }

}
