package com.kaiqiang.learn.distributed.lock.mysql;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author kaiqiang
 * @Date 2020/08/30
 */
@Repository
public interface MysqlLockDao {

    /**
     * @return nullable
     */
    SimpleLock selectByLockKey(@Param("lockKey") String lockKey);

    /**
     * @return nullable
     */
    SimpleLock selectByLockKeyThreadId(@Param("lockKey") String lockKey,
                                        @Param("threadId")String threadId);


    int insert(@Param("lock") SimpleLock lock);

    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * @param startId where id > startId
     * @param time 过期时间
     * @param limit 查询的最大个数
     */
    List<Long> selectExpireLock(@Param("startId") long startId,
                                @Param("time")LocalDateTime time,
                                @Param("limit") int limit);


    /**
     * select from xxx where id = dbSerializeId for update;
     */
    Integer lockUntilGetDBXLock(@Param("dbSerializeId") int dbSerializeId);
}
