package com.kaiqiang.learn.distributed.lock.mysql;

import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author kaiqiang
 * @Date 2020/08/30
 */
@Alias("SimpleLock")
public class SimpleLock {

    private long id;
    private String lockKey;
    private String threadId;
    private LocalDateTime expireTime;

    public SimpleLock() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}
