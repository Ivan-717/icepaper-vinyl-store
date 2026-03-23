package com.bingzhi.vinyl.controller.admin;

import com.bingzhi.vinyl.entity.Product;
import com.bingzhi.vinyl.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public List<Product> list(){
        return productMapper.findAll();
    }

    @PostMapping
    public Map<String,Object> add(@RequestBody Product product){
        productMapper.insert(product);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "新增成功");
        result.put("data", product);
        return result;
    }

    //更新商品
    @PutMapping
    public Map<String,String> update(@RequestBody Product product){
        productMapper.update(product);

        Map<String, String> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable Long id){
        productMapper.deleteById(id);

        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }

    @PutMapping("/{id}/status")
    public Map<String,String> updateStatus(@PathVariable Long id,@RequestBody Map<String,Integer> request){
        Integer status=request.get("status");
        productMapper.updateStatus(id,status);

        Map<String, String> result = new HashMap<>();
        result.put("message", "状态更新成功");
        return result;
    }


}
