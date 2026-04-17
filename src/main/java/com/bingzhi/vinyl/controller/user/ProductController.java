package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.Product;
import com.bingzhi.vinyl.mapper.ProductMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/all")
    public List<Product> getAllProducts(){
        String key="products:all";

        List<Product> products=(List<Product>) redisTemplate.opsForValue().get(key);
        if(products!=null){
            System.out.println("从redis读取了商品列表");
            return products;
        }

        System.out.println("从数据库查到商品列表");
        products=productMapper.findAll();
        //存入redis 24小时过期
        redisTemplate.opsForValue().set(key,products,24, TimeUnit.HOURS);

        return products;
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getByCategory(@PathVariable Long categoryId){
        String key="products:category:"+categoryId;

        List<Product> products=(List<Product>) redisTemplate.opsForValue().get(key);
        if(products!=null){
            System.out.println("从redis读取了商品列表");
            return products;
        }

        System.out.println("从数据库查到商品列表");
        products=productMapper.findByCategoryId(categoryId);
        //存入redis 24小时过期
        redisTemplate.opsForValue().set(key,products,24, TimeUnit.HOURS);

        return products;
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id){
        String key="products:detail:"+id;

        Product product=(Product) redisTemplate.opsForValue().get(key);
        if(product!=null){
            System.out.println("从redis读取了商品列表");
            return product;
        }

        System.out.println("从数据库查到商品列表");
        product=productMapper.findById(id);
        //存入redis 24小时过期
        redisTemplate.opsForValue().set(key,product,24, TimeUnit.HOURS);

        return product;
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
