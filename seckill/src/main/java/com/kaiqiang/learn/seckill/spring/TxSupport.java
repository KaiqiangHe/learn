package com.kaiqiang.learn.seckill.spring;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * @Author kaiqiang
 * @Date 2020/09/01
 */
@Service
public class TxSupport {

    @Transactional(rollbackFor = {Exception.class})
    public void executeWithDefaultTx(Consumer<Void> consumer) {
        consumer.accept(null);
    }
}
