package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.Category;
import com.bingzhi.vinyl.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping
    public List<Category> getAllCategories(){

        return categoryMapper.findAll();
    }
}
