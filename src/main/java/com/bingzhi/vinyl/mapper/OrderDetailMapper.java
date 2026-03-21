package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    //根据订单id查询明细
    @Select("SELECT * FROM order_detail WHERE order_id=#{orderId}")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);
}
