package com.kaiqiang.learn.seckill.model;

import java.time.LocalDateTime;

/**
 * @Author kaiqiang
 * @Date 2020/09/07
 */
public class Stock {

    private String stockId;
    private int totalStock;
    private LocalDateTime createTime;

    /**
     * 是否有剩余库存
     */
    private volatile boolean hasRemain = true;

    public Stock() {
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public boolean isHasRemain() {
        return hasRemain;
    }

    public void setHasRemain(boolean hasRemain) {
        this.hasRemain = hasRemain;
    }
}
