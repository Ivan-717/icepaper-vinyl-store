package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Category;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper {

    @Select("SELECT * FROM category ORDER BY sort")
    List<Category> findAll();
}
