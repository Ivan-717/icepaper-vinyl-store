package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE category_id=#{categoryId}")
    List<Product> findByCategoryId(Long categoryId);

    @Select("SELECT * FROM product WHERE id=#{id}")
    Product findById(Long id);
}
