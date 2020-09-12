package com.kaiqiang.learn.seckill.service.impi;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.kaiqiang.learn.seckill.service.IdGenerator;

/**
 * hutool id生成封装
 * https://hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/%E5%94%AF%E4%B8%80ID%E5%B7%A5%E5%85%B7-IdUtil
 *
 * @Author kaiqiang
 * @Date 2020/09/12
 */
public class HutooIdGenerator implements IdGenerator {

    private static final Snowflake SNOWFLAKE;
    static {
        // TODO: 2020/9/12  程序初始化时生成 workId datacenterId
        SNOWFLAKE = IdUtil.getSnowflake(1, 1);
    }

    @Override
    public long getNextId() {
        return SNOWFLAKE.nextId();
    }
}
