package com.kaiqiang.learn.distributed.lock.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Consumer;

/**
 * @Author kaiqiang
 * @Date 2020/08/30
 */
public class SpringTxUtil {

    private static final Logger log = LoggerFactory.getLogger(SpringTxUtil.class);

    private static PlatformTransactionManager transactionManager;

    public static void setTransactionManager(PlatformTransactionManager transactionManager) {
        if(transactionManager == null) {
            throw new IllegalArgumentException("Parameter 'transactionManager' should not be null");
        }
        SpringTxUtil.transactionManager = transactionManager;
    }

    public static void executeWithTx(Consumer<Void> consumer) throws Exception {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            consumer.accept(null);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
