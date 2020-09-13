package com.kaiqiang.learn.seckill.db.dao;

import com.kaiqiang.learn.seckill.db.pojo.SecOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @Author kaiqiang
 * @Date 2020/09/12
 */
@Repository
public interface SecOrderDao {

    int initOrder(@Param("order") SecOrder order,
                  @Param("tableIndex") int tableIndex);

    /**
     * 更新订单状态为已扣减库存
     */
    int deductOrder(@Param("orderNo") String orderNo,
                    @Param("deductStockTime")LocalDateTime deductStockTime,
                    @Param("tableIndex") int tableIndex);

    /**
     * @param status PAY_SUCCESS PAY_FAILED
     */
    int payCallback(@Param("orderNo") String orderNo,
                    @Param("orderStatus") int orderStatus,
                    @Param("payCallbackTime")LocalDateTime payCallbackTime,
                    @Param("tableIndex") int tableIndex);

}
