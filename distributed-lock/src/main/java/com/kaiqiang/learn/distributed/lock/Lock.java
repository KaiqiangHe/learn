package com.kaiqiang.learn.distributed.lock;

/**
 * 使用参考：
 * if (lock.tryLock()) {    // 1
 *  try {
 *      // 获得锁后的处理  2
 *
 *  } finally {
 *      // 保证锁一定释放  3
 *      lock.unlock();
 *    }
 *  } else {
 *      // 未获得锁时的处理
 *  }
 *
 *  分布式环境下可能由于各种原因(网络抖动、重试等等)导致锁未能释放，
 *  建议为每个锁设置过期时间。
 *
 * @Author kaiqiang
 * @Date 2020/08/24
 */
public interface Lock {

    /**
     * 尝试获得锁, 返回是否成功
     *
     * 注意：锁必须是可重入的
     */
    boolean tryLock();

    /**
     * 释放锁
     */
    void unlock();

    String getKey();
}
