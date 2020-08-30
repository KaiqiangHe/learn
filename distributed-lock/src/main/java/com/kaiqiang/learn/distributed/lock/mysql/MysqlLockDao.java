package com.kaiqiang.learn.distributed.lock.mysql;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
     * select for update
     * @return nullable
     */
    SimpleLock selectByLockKeyForUpdate(@Param("lockKey") String lockKey,
                                        @Param("threadId")String threadId);

    int insert(@Param("lock") SimpleLock lock);

    int batchDelete(@Param("ids") List<Long> ids);
}
