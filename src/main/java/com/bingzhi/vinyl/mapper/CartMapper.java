package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    //获取用户购物车
    /*左连接product表
     * on：连接条件
     * where：只查当前用户的购物车
     ***/
    @Select("SELECT c.id, c.user_id as userId, c.product_id as productId, c.quantity, " +
            "c.create_time as createTime, c.update_time as updateTime, " +
            "p.name as productName, p.artist as productArtist, p.price as productPrice, p.image as productImage " +
            "FROM cart c LEFT JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId} AND c.type = 0 ORDER BY c.id DESC")
    List<Cart> findByUserId(@Param("userId") Long userId);

    //根据用户ID和商品ID查询
    @Select("SELECT * FROM cart WHERE user_id=#{userId} and product_id=#{productId}")
    Cart findByUserAndProduct(@Param("userId") Long userId,@Param("productId")Long productId);

    //添加购物车
    @Insert("INSERT INTO cart (user_id, product_id, quantity, type, create_time, update_time) " +
            "VALUES (#{userId}, #{productId}, #{quantity}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Cart cart);

    //更新数量
    @Update("UPDATE cart SET quantity=#{quantity},update_time=NOW() Where id=#{id}")
    void updateQuantity(@Param("id") Long id,@Param("quantity") Integer quantity);

    //删除购物车项
    @Delete("DELETE FROM cart WHERE id=#{id}")
    void deleteById(@Param("id") Long id);

    //清空购物车
    @Delete("DELETE FROM cart WHERE user_id=#{userId}")
    void deleteByUserId(@Param("userId") Long userId);


    // 1. 查询商品购物车（只查 type=0）
    @Select("SELECT c.id, c.user_id as userId, c.product_id as productId, c.quantity, " +
            "c.create_time as createTime, c.update_time as updateTime, " +
            "p.name as productName, p.artist as productArtist, p.price as productPrice, p.image as productImage " +
            "FROM cart c LEFT JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId} AND c.type = 0 ORDER BY c.id DESC")
    List<Cart> findProductsByUserId(@Param("userId") Long userId);

    // 2. 查询套餐购物车（查 combo 表）
    @Select("SELECT c.id, c.user_id as userId, c.combo_id as comboId, c.quantity, " +
            "c.create_time as createTime, c.update_time as updateTime, " +
            "cm.name as productName, cm.price as productPrice, cm.image as productImage " +
            "FROM cart c LEFT JOIN combo cm ON c.combo_id = cm.id " +
            "WHERE c.user_id = #{userId} AND c.type = 1 ORDER BY c.id DESC")
    List<Cart> findCombosByUserId(@Param("userId") Long userId);


    // 3. 根据用户ID和套餐ID查询
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND combo_id = #{comboId} AND type = 1")
    Cart findByUserAndCombo(@Param("userId") Long userId, @Param("comboId") Long comboId);

    // 4. 添加套餐到购物车
    @Insert("INSERT INTO cart (user_id, combo_id, quantity, type, create_time, update_time) " +
            "VALUES (#{userId}, #{comboId}, #{quantity}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCombo(Cart cart);

}
