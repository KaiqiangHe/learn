package com.kaiqiang.learn.seckill.model;

import com.kaiqiang.learn.seckill.dao.StockDao;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 商品秒杀活动model类
 *
 * // TODO: 2020/9/8  完善活动、库存、商品、库存抽象
 *
 * @Author kaiqiang
 * @Date 2020/09/07
 */
public class SeckillActivity {

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
    private volatile boolean hasRemain;

    private SeckillActivity() {
    }

    public static SeckillActivity initProductActivity(String activityId) {
        SeckillActivity result = new SeckillActivity();
        Stock stock = new Stock();
        stock.setHasRemain(true);
        stock.setStockId("apple_20200907-01");

        Stock stock2 = new Stock();
        stock2.setHasRemain(true);
        stock2.setStockId("apple_20200907-02");

        Stock stock3 = new Stock();
        stock3.setHasRemain(true);
        stock3.setStockId("apple_20200907-03");

        List<Stock> stocks = Arrays.asList(stock, stock2, stock3);

        result.stockMap = new HashMap<>();
        stocks.forEach(s -> {
            result.stockMap.put(s.getStockId(), s);
        });
        result.hasRemainStockIdList = new ArrayList<>(result.stockMap.keySet());
        result.activityId = activityId;
        result.hasRemain = true;
        return result;
    }

    /**
     * 添加使用库存
     *
     * @param addCount > 0
     */
    public boolean addUseStock(int addCount) {
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
        SeckillActivity.stockDao = stockDao;
    }
}
