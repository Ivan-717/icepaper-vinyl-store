package com.bingzhi.vinyl.controller;

import com.bingzhi.vinyl.entity.Product;
import com.bingzhi.vinyl.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/category/{categoryId}")
    public List<Product> getByCategory(@PathVariable Long categoryId){
        return productMapper.findByCategoryId(categoryId);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id){
        return productMapper.findById(id);
    }

    //搜索商品
    @GetMapping("/search")
    //@RequestParam会从URL?后面找keyword=周杰伦，把周杰伦赋值给keyword参数
    public List<Product> search(@RequestParam String keyword){
        return productMapper.search(keyword);
    }





}
