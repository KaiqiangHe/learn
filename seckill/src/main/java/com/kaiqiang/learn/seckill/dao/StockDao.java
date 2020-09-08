package com.kaiqiang.learn.seckill.dao;

import com.kaiqiang.learn.seckill.model.Stock;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author kaiqiang
 * @Date 2020/09/07
 */
@Repository
public interface StockDao {

    //Stock selectByStockId(@Param("stockId") String stockId);

    int initStock(@Param("stockId") String stockId,
                  @Param("totalStock") String totalStock);

    /**
     * 返回1表示更新库存成功，否则库存已用光
     * @param addCount > 0
     */
    int addUseStock(@Param("stockId") String stockId,
                    @Param("addCount") int addCount);


}
