package com.kaiqiang.learn.distributed.lock.mysql;

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void executeWithReadCommittedTx(Consumer<Void> consumer) {
        consumer.accept(null);
    }
}
