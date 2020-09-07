package com.kaiqiang.learn.seckill.model;

import com.kaiqiang.learn.seckill.dao.StockDao;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 商品秒杀活动model类
 *
 * @Author kaiqiang
 * @Date 2020/09/07
 */
public class ProductActivity {

    private static StockDao stockDao;

    /**
     * 活动id
     */
    private String activityId;

    /**
     * key stockId
     * value Stock
     */
    private Map<String, Stock> stockMap;

    /**
     * 当前有剩余库存的stockId
     */
    private List<String> hasRemainStockIdList;

    /**
     * 当前活动是否有剩余库存
     */
    private boolean hasRemain;

    private ProductActivity() {
    }

    public static ProductActivity initProductActivity(String acitvityId) {
        ProductActivity result = new ProductActivity();
        result.hasRemain = true;
        // TODO: 2020/9/7

        return result;
    }

    /**
     * 添加使用库存
     *
     * @param addCount > 0
     */
    private boolean addUseStock(int addCount) {
        if(addCount <= 0) {
            throw new IllegalArgumentException("Parameter addCount should > 0, but current addCount is " + addCount);
        }

        if(!hasRemain) {
            return false;
        }

        Stock stock = getRemainStock();
        if(stock == null || !stock.isHasRemain()) {
            return false;
        }
        String stockId = stock.getStockId();
        int rowEffect = stockDao.addUseStock(stockId, addCount);
        if(rowEffect != 1) {
            removeUsedStock(stockId);
            return false;
        }

        return true;
    }

    private final Object obj = new Object();
    private int current = 0;
    /**
     * @return nullable
     */
    private Stock getRemainStock() {
        synchronized (obj) {
            if(hasRemainStockIdList.isEmpty()) {
                return null;
            }

            current = (current + 1) % hasRemainStockIdList.size();
            return stockMap.get(hasRemainStockIdList.get(current));
        }
    }
    /**
     * 移除已使用完的库存
     *
     * @param stockId
     */
    private void removeUsedStock(String stockId) {
        synchronized (obj) {
            if(hasRemainStockIdList.isEmpty()) {
                return ;
            }

            boolean removed = hasRemainStockIdList.remove(stockId);
            if(removed) {
                stockMap.get(stockId).setHasRemain(false);
            }

            if(hasRemainStockIdList.isEmpty()) {
                this.hasRemain = true;
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    public static void setStockDao(StockDao stockDao) {
        Objects.requireNonNull(stockDao, "Parameter 'stockDao' should not be null");
        ProductActivity.stockDao = stockDao;
    }
}
