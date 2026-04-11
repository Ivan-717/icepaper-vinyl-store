package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    //查询所有订单
    @Select("SELECT * from orders ORDER BY id ASC ")
    List<Orders> findAll();

    //根据状态查询订单
    @Select("SELECT * FROM orders WHERE status=#{status} order by id ASC")
    List<Orders> findByStatus(@Param("status") Integer status);

    //根据id查询订单
    @Select("SELECT * from orders WHERE id=#{id}")
    Orders findById(@Param("id") Long id);

    //查询今日订单(按时间范围）
    @Select("SELECT * FROM orders WHERE create_time BETWEEN #{start} and #{end}")
    List<Orders> findByCreateTimeBetween(@Param("start")LocalDateTime start,@Param("end") LocalDateTime end);

    //更新订单状态
    @Update("UPDATE orders SET status = #{status},update_time=NOW() Where id=#{id}")
    void updateStatus(@Param("id") Long id,@Param("status") Integer status);

    //插入订单
    @Insert("INSERT INTO orders (order_no, user_id, total_amount, status, receiver, receiver_phone, address, remark, create_time, update_time) " +
            "VALUES (#{orderNo}, #{userId}, #{totalAmount}, #{status}, #{receiver}, #{receiverPhone}, #{address}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(Orders order);

    //根据用户ID查询订单列表
    @Select("SELECT * FROM orders WHERE user_id=#{userId} order by id desc")
    List<Orders> findByUserId(@Param("userId") Long userID);

    //根据用户ID和订单状态订单列表
    @Select("SELECT * FROM orders WHERE user_id=#{userId} And status=#{status} order by id desc")
    List<Orders> findByUserIdAndStatus(@Param("userId") Long userId,@Param("status") Integer status);
}
