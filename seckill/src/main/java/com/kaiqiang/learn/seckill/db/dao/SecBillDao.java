package com.kaiqiang.learn.seckill.db.dao;

import com.kaiqiang.learn.seckill.db.pojo.SecBill;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @Author kaiqiang
 * @Date 2020/09/12
 */
@Repository
public interface SecBillDao {

    @Insert("insert into sec_bill_#{tableIndex}(user_id, bill_no, price, ext) " +
            "values(#{bill.userId}, #{bill.billNo}, #{bill.price}, #{bill.ext})")
    int insert(@Param("bill") SecBill bill,
               @Param("tableIndex") int tableIndex);

    @Update("update sec_bill_#{tableIndex} set ext = #{ext} where bill_no = #{billNo}")
    int updateExt(@Param("billNo") String billNo,
                  @Param("ext")String ext,
                  @Param("tableIndex") int tableIndex);
}
