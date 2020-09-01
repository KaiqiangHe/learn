package com.kaiqiang.learn.distributed.lock.mysql;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;

/**
 * @Author kaiqiang
 * @Date 2020/08/30
 */
@Component
public class SpringBeanSupport implements InitializingBean {

    @Resource
    private MysqlLockDao mysqlLockDao;

    @Resource
    private PlatformTransactionManager platformTransactionManager;

    @Resource
    private TxSupport txSupport;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(mysqlLockDao == null) {
            throw new RuntimeException("mysqlLockDao 为null");
        }
        if(platformTransactionManager == null) {
            throw new RuntimeException("platformTransactionManager为null");
        }
        if(txSupport == null) {
            throw new RuntimeException("txSupport为null");
        }

        MysqlLock.setMysqlLockDao(mysqlLockDao);
        SpringTxUtil.setTransactionManager(platformTransactionManager);
        SpringTxUtil.setTxSupport(txSupport);
    }
}
