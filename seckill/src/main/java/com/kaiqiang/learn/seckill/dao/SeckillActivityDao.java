package com.kaiqiang.learn.seckill.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author kaiqiang
 * @Date 2020/09/09
 */
@Repository
public interface SeckillActivityDao {

    @Insert("insert into seckill_activity(activity_id, activity_name) value (#{activityId}, #{activityName})")
    int insert(@Param("activityId") String activityId,
               @Param("activityName") String activityName);
}
