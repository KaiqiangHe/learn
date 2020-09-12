package com.kaiqiang.learn.seckill.db.dao;

import com.kaiqiang.learn.seckill.db.pojo.SecOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author kaiqiang
 * @Date 2020/09/12
 */
@Repository
public interface SecOrderDao {

    int initOrder(@Param("order") SecOrder order,
                  @Param("tableIndex") int tableIndex);

    /**
     * 扣减库存
     */

}
