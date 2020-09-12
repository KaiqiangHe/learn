package com.kaiqiang.learn.seckill.db.dao;

import com.kaiqiang.learn.seckill.db.pojo.SecBill;
import com.kaiqiang.learn.seckill.db.pojo.SecOrder;
import com.kaiqiang.learn.seckill.model.SpringTestSupport;
import com.kaiqiang.learn.seckill.service.IdGenerator;
import com.kaiqiang.learn.seckill.service.impi.HutooIdGenerator;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SecBillDaoTest extends SpringTestSupport {

    private static final IdGenerator ID_GENERATOR = new HutooIdGenerator();

    @Resource
    private SecBillDao secBillDao;

    @Resource
    private SecOrderDao secOrderDao;

    @Test
    void insert() {
        String userId = "apple";
        SecBill bill = new SecBill();
        bill.setUserId(userId);
        bill.setBillNo("B_" + ID_GENERATOR.getNextId());
        bill.setExt("");
        bill.setPrice("69.00");
        secBillDao.insert(bill, DaoHelper.getTableIndex(userId));

        /*LocalDateTime now = LocalDateTime.now();
        SecOrder order = new SecOrder();
        order.setUserId(userId);
        order.setActivityId("apple-test-01");
        order.setOrderNo("S_" + ID_GENERATOR.getNextId());
        order.setBillNo("B_" + ID_GENERATOR.getNextId());
        order.setProductId("apple001");
        order.setSecCount(1);
        order.setOrderStatus(SecOrder.PRE_PAY);
        order.setInitOrderTime(now);
        order.setPrePayTimeout(now.plusMinutes(5));
        secOrderDao.initOrder(order, DaoHelper.getTableIndex(userId));*/

    }

    @Test
    void updateExt() {
        secBillDao.updateExt("hello", "{test}", DaoHelper.getTableIndex("apple"));
    }
}