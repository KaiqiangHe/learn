package com.kaiqiang.learn.seckill.model;

import com.kaiqiang.learn.seckill.CheckUtil;
import com.kaiqiang.learn.seckill.dao.SeckillActivityDao;
import com.kaiqiang.learn.seckill.dao.StockDao;
import com.kaiqiang.learn.seckill.exception.InitActivityException;
import com.kaiqiang.learn.seckill.service.IdGenerator;
import com.kaiqiang.learn.seckill.service.SimpleIdGenerator;
import com.kaiqiang.learn.seckill.spring.TxSupport;
import org.apache.commons.lang.StringUtils;

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
    private static SeckillActivityDao seckillActivityDao;
    private static TxSupport txSupport;

    private static final IdGenerator idGenerator = new SimpleIdGenerator();

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

    public static SeckillActivity initActivity(String activityId) {
        CheckUtil.checkParam(activityId, String::isEmpty, "Parameter 'activityId' should not be empty.");

        try {
            SeckillActivity activity = new SeckillActivity();
            List<Stock> stocks = stockDao.selectByActivityId(activityId);
            if(stocks == null || stocks.isEmpty()) {
                throw new InitActivityException("未查到活动对应的库存数据");
            }
            HashMap<String, Stock> map = new HashMap<>();
            stocks.forEach(s -> {
                map.put(s.getStockId(), s);
            });
            activity.stockMap = map;
            activity.hasRemainStockIdList = new ArrayList<>(map.keySet());
            activity.activityId = activityId;
            activity.hasRemain = true;
            return activity;
        } catch (Exception e) {
            throw new InitActivityException("从db中载入活动失败", e);
        }
    }

    /**
     * 创建一个活动
     *
     * @param activityPrefix 活动前缀
     * @param stocks 总库存 > 0
     *
     * @return 创建活动id
     */
    public static String createSecActivity(String activityPrefix,
                                         String activityName,
                                         List<Integer> stocks) {

        CheckUtil.checkParam(activityPrefix, StringUtils::isEmpty, "Parameter 'activityPrefix' should not be empty.");
        CheckUtil.checkParam(activityName, StringUtils::isEmpty, "Parameter 'activityName' should not be empty.");
        CheckUtil.checkParam(stocks, p -> p == null || p.isEmpty(), "Parameter 'stocks' should not be empty.");
        CheckUtil.checkParam(stocks, p -> p.stream().anyMatch(v -> v <= 0), "Parameter 'stocks' elem should > 0.");

        try {
            long nextId = idGenerator.getNextId();
            String activityId = activityPrefix + "_" + nextId;
            txSupport.executeWithDefaultTx(v -> {
                seckillActivityDao.insert(activityId, activityName);
                for (int i = 0; i < stocks.size(); i++) {
                    stockDao.initStock(activityId + "_" + i, activityId, stocks.get(i));
                }
            });
            return activityId;
        } catch (Exception e) {
            throw new RuntimeException("创建活动失败", e);
        }
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
    public static void setSeckillActivityDao(SeckillActivityDao seckillActivityDao) {
        Objects.requireNonNull(seckillActivityDao, "Parameter 'seckillActivityDao' should not be null");
        SeckillActivity.seckillActivityDao = seckillActivityDao;
    }

    public static void setTxSupport(TxSupport txSupport) {
        Objects.requireNonNull(txSupport, "Parameter 'txSupport' should not be null");
        SeckillActivity.txSupport = txSupport;
    }
}
