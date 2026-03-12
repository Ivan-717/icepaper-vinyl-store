package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
