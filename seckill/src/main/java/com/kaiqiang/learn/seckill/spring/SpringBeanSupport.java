package com.kaiqiang.learn.seckill.spring;

import com.kaiqiang.learn.seckill.db.dao.SecBillDao;
import com.kaiqiang.learn.seckill.db.dao.SecOrderDao;
import com.kaiqiang.learn.seckill.db.dao.SeckillActivityDao;
import com.kaiqiang.learn.seckill.db.dao.StockDao;
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

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private SecOrderDao secOrderDao;

    @Resource
    private SecBillDao secBillDao;

    @Resource
    private TxSupport txSupport;

    @Override
    public void afterPropertiesSet() throws Exception {
        SeckillActivity.setStockDao(stockDao);
        SeckillActivity.setSeckillActivityDao(seckillActivityDao);
        SeckillActivity.setTxSupport(txSupport);
        SeckillActivity.setSecOrderDao(secOrderDao);
        SeckillActivity.setSecBillDao(secBillDao);
    }
}
