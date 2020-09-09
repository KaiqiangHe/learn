package com.kaiqiang.learn.seckill.dao;

import com.kaiqiang.learn.seckill.model.Stock;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author kaiqiang
 * @Date 2020/09/07
 */
@Repository
public interface StockDao {

    @Select("select stock_id, total_stock from stock where activity_id = #{activityId}")
    List<Stock> selectByActivityId(@Param("activityId") String activityId);

    @Insert("insert into stock(stock_id, activity_id, use_stock, total_stock) values (#{stockId}, #{activityId}, 0, #{totalStock});")
    int initStock(@Param("stockId") String stockId,
                  @Param("activityId") String activityId,
                  @Param("totalStock") int totalStock);

    /**
     * 返回1表示更新库存成功，否则库存已用光
     * @param addCount > 0
     */
    @Update("update stock set use_stock = use_stock + #{addCount} where use_stock + #{addCount} <= total_stock and stock_id = #{stockId}")
    int addUseStock(@Param("stockId") String stockId,
                    @Param("addCount") int addCount);

}
