package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Combo;
import com.bingzhi.vinyl.entity.ComboItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface ComboMapper {
    @Select("SELECT * from combo ORDER BY id ASC ")
    List<Combo> findAll();

    @Select("SELECT * from combo where id=#{id}")
    Combo findById(@Param("id") Long id);

    //查询套餐包含的商品ID列表
    @Select("SELECT * FROM combo_item where combo_id=#{comboId}")
    List<ComboItem> findItemsByComboId(@Param("comboId") Long comboId);

    //根据状态查询套餐
    @Select("SELECT * FROM combo WHERE status=#{status} Order by id ASC")
    List<Combo> findByStatus(@Param("status") Integer status);

    @Insert("INSERT INTO combo (name, price, image, description, status, create_time, update_time) " +
            "VALUES (#{name}, #{price}, #{image}, #{description}, #{status}, NOW(), NOW())")
    //自增id
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(Combo combo);

    @Insert("INSERT INTO combo_item(combo_id,product_id,quantity)VALUES (#{comboId},#{productId},#{quantity})")
    void insertItem(ComboItem item);

    //更新套餐
    @Update("UPDATE combo SET name = #{name}, price = #{price}, image = #{image}, " +
            "description = #{description}, status = #{status}, update_time = NOW() WHERE id = #{id}")
    void update(Combo combo);

    //更新状态
    @Update("UPDATE combo SET status=#{status},update_time=NOW() where id=#{id}")
    void updateStatus(@Param("id") Long id,@Param("status") Integer status);

    //删除套餐
    @Delete("DELETE FROM combo where id=#{id}")
    void deleteById(@Param("id") Long id);

    //删除套餐的所有商品关联
    @Delete("DELETE FROM combo_item where combo_id=#{comboId}")
    void deleteItemsByComboId(@Param("comboId") Long comboId);




}
