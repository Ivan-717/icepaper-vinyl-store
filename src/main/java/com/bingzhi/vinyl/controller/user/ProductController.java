package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.Product;
import com.bingzhi.vinyl.mapper.ProductMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/all")
    public List<Product> getAllProducts(){
        return productMapper.findAll();
    }

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

    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable Long id){
        productMapper.deleteById(id);

        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }

    //更新状态
    @PutMapping("/{id}/status")
    public Map<String,String> updateStatus(@PathVariable Long id,@RequestBody Map<String,Integer> request){
        Integer status=request.get("status");
        productMapper.updateStatus(id,status);

        Map<String, String> result = new HashMap<>();
        result.put("message", "状态更新成功");
        return result;
    }



}
