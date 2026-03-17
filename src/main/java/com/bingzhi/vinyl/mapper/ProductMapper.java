package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE category_id=#{categoryId}")
    List<Product> findByCategoryId(Long categoryId);

    @Select("SELECT * FROM product WHERE id=#{id}")
    Product findById(Long id);

    //根据关键词搜索
    //LIKE "%love%" 的意思是：包含love的结果全都匹配

    @Select("SELECT * FROM  product WHERE name LIKE CONCAT('%',#{keyword},'%') or artist LIKE CONCAT('%',#{keyword},'%')")
    //@param("keyword")是把参数和sql里的#{。。。}联系起来
    List<Product> search(@Param("keyword") String keyword);

    @Delete("DELETE FROM product WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    //更新状态
    @Update("UPDATE  product SET status=#{status} where id=#{id}")
    int updateStatus(@Param("id") Long id,@Param("status") int status);

    //新增商品
    @Insert("INSERT INTO product (category_id, name, artist, price, stock, image, description, status, create_time, update_time) " +
            "VALUES (#{categoryId}, #{name}, #{artist}, #{price}, #{stock}, #{image}, #{description}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(Product product);

    //编辑信息
    @Update("UPDATE product SET name = #{name}, artist = #{artist}, price = #{price}, " +
            "stock = #{stock}, image = #{image}, description = #{description}, category_id = #{categoryId} " +
            "WHERE id = #{id}")
    int update(Product product);

    @Select("SELECT * FROM product ORDER BY id ASC ")
    List<Product> findAll();


}
