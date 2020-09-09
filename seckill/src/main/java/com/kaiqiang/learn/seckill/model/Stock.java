package com.kaiqiang.learn.seckill.model;

import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * @Author kaiqiang
 * @Date 2020/09/07
 */
@Alias("Stock")
public class Stock {

    private String stockId;
    private int totalStock;

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

    public boolean isHasRemain() {
        return hasRemain;
    }

    public void setHasRemain(boolean hasRemain) {
        this.hasRemain = hasRemain;
    }
}
