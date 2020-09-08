package com.kaiqiang.learn.seckill.spring;

import com.kaiqiang.learn.seckill.dao.StockDao;
import com.kaiqiang.learn.seckill.model.SeckillActivity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author kaiqiang
 * @Date 2020/09/08
 */
@Component
public class SpringBeanSupport implements InitializingBean {

    @Resource
    private StockDao stockDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        SeckillActivity.setStockDao(stockDao);
    }
}
