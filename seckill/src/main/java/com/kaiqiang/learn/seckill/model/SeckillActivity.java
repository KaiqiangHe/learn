package com.kaiqiang.learn.seckill.model;

import com.kaiqiang.learn.seckill.CheckUtil;
import com.kaiqiang.learn.seckill.db.dao.*;
import com.kaiqiang.learn.seckill.db.pojo.SecBill;
import com.kaiqiang.learn.seckill.db.pojo.SecOrder;
import com.kaiqiang.learn.seckill.exception.InitActivityException;
import com.kaiqiang.learn.seckill.exception.StockNotEnough;
import com.kaiqiang.learn.seckill.exception.UpdateOrderStatusException;
import com.kaiqiang.learn.seckill.service.IdUtil;
import com.kaiqiang.learn.seckill.spring.TxSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

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
@Slf4j
public class SeckillActivity {

    private static StockDao stockDao;
    private static SeckillActivityDao seckillActivityDao;
    private static TxSupport txSupport;
    private static SecBillDao secBillDao;
    private static SecOrderDao secOrderDao;

    /**
     * 活动id
     */
    private String activityId;
    private String productId = "banana-202009";
    private String price = "69.00";
    private int secCount = 1;
    private int payPreTimeoutMinutes = 5;

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
            String nextId = IdUtil.getNextId();
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
     * 创建订单
     *
     * @return orderNo, nullable
     */
    public String createOrder(String userId) {
        if(!hasRemain) {
            return null;
        }

        int tableIndex = DaoHelper.getTableIndex(userId);
        String orderNo = IdUtil.getOrderNo();
        String billNo = IdUtil.getBillNo();

        SecBill bill = new SecBill();
        bill.setUserId(userId);
        bill.setBillNo(billNo);
        bill.setExt("");
        bill.setPrice(price);

        LocalDateTime now = LocalDateTime.now();
        SecOrder order = new SecOrder();
        order.setUserId(userId);
        order.setActivityId(activityId);
        order.setOrderNo(orderNo);
        order.setBillNo(billNo);
        order.setProductId(productId);
        order.setSecCount(secCount);
        order.setOrderStatus(SecOrder.PRE_PAY);
        order.setInitOrderTime(now);

        txSupport.executeWithDefaultTx(v -> {
            secBillDao.insert(bill, tableIndex);
            secOrderDao.initOrder(order, tableIndex);
        });

        return orderNo;
    }

    /**
     * 支付前扣除库存
     */
    public boolean deductStock(String orderNo, String userId) {
        if(!hasRemain) {
            return false;
        }

        try {
            txSupport.executeWithDefaultTx(v -> {
                // 更新订单状态为已扣减
                int rowEffect = secOrderDao.deductOrder(orderNo, LocalDateTime.now(), DaoHelper.getTableIndex(userId));
                if(rowEffect != 1) {
                    throw new UpdateOrderStatusException(orderNo, "更新订单状态为STOCK_DEDUCTED失败");
                }
                // 扣减库存
                boolean deducted = addUseStock(secCount);
                if(!deducted) {
                    throw new StockNotEnough("扣减库存失败");
                }
            });
        } catch (StockNotEnough e) {
            log.info("库存不足, orderNo = {}", orderNo, e);
            return false;
        } catch (Exception e) {
            log.error("deductStock失败, orderNo = {}", orderNo, e);
            return false;
        }

        return true;
    }

    /**
     * 支付成功
     */
    public void payCallback(String orderNo, String userId) {
        secOrderDao.payCallback(orderNo, SecOrder.PAY_SUCCESS, LocalDateTime.now(), DaoHelper.getTableIndex(userId));
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

    public static void setSecBillDao(SecBillDao secBillDao) {
        SeckillActivity.secBillDao = secBillDao;
    }

    public static void setSecOrderDao(SecOrderDao secOrderDao) {
        SeckillActivity.secOrderDao = secOrderDao;
    }
}
