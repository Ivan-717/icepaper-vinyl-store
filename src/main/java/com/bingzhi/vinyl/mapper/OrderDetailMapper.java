package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    //根据订单id查询明细
    @Select("SELECT * FROM order_detail WHERE order_id=#{orderId}")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);

    @Insert("INSERT INTO order_detail (order_id, product_id, product_name, price, quantity) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{price}, #{quantity})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderDetail orderDetail);
}
