package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.Cart;
import com.bingzhi.vinyl.mapper.CartMapper;
import com.bingzhi.vinyl.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/cart")
public class CartController {
    @Autowired
    private CartMapper cartMapper;

    public CartController() {
        System.out.println("CartController 已加载");
    }

    //获取购物车列表
    @GetMapping
    public List<Cart> list(@RequestHeader("Authorization") String token){
        System.out.println("获取购物车，token: " + token);
        Long userId= JwtUtil.getUserId(token);
        System.out.println("userId: " + userId);
        return cartMapper.findByUserId(userId);
    }

    //添加商品到购物车
    @PostMapping
    public Map<String,Object> add(@RequestHeader("Authorization") String token,@RequestBody Map<String,Object> request){
        System.out.println("=== 添加购物车 ===");
        System.out.println("收到的token: " + token);
        System.out.println("productId: " + request.get("productId"));
        System.out.println("quantity: " + request.get("quantity"));
        Long userId=JwtUtil.getUserId(token);
        System.out.println("解析出的userId: " + userId);

        //获取前端传来的商品Id，并转为Long类型
        Long productId=Long.valueOf(request.get("productId").toString());
        //有值就用，没值就是1
        Integer quantity=(Integer) request.getOrDefault("quantity",1);

        //检查是否已存在
        Cart exist=cartMapper.findByUserAndProduct(userId,productId);
        if(exist!=null) {
            cartMapper.updateQuantity(exist.getId(),exist.getQuantity()+quantity);
        }else {
            Cart cart=new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "添加成功");
        return result;
    }

    //删除购物车商品
    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable Long id){
        cartMapper.deleteById(id);

        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }

    //清空购物车
    @DeleteMapping("/all")
    public Map<String,String> clear(@RequestHeader("Authorization") String token){
        Long userId=JwtUtil.getUserId(token);
        cartMapper.deleteByUserId(userId);

        Map<String, String> result = new HashMap<>();
        result.put("message", "清空成功");
        return result;
    }

    //同步本地购物车（登录后调用）
    //synchronize同步
    @PostMapping("/sync")
    public Map<String,Object> sync(@RequestHeader("Authorization") String token,@RequestBody List<Map<String,Object>> localCart){
        Long userId=JwtUtil.getUserId(token);

        for (Map<String,Object> item:localCart){
            Long productId=Long.valueOf(item.get("productId").toString());
            Integer quantity=(Integer) item.get("quantity");

            Cart exist=cartMapper.findByUserAndProduct(userId,productId);
            if (exist!=null){
                cartMapper.updateQuantity(exist.getId(),exist.getQuantity()+quantity);
            }else {
                Cart cart=new Cart();
                cart.setUserId(userId);
                cart.setProductId(productId);
                cart.setQuantity(quantity);
                cartMapper.insert(cart);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "同步成功");
        return result;
    }

    //更新购物车商品数量
    @PutMapping("/{id}")
    public Map<String,String> updateQuantity(@PathVariable Long id,@RequestBody Map<String,Integer> request){
        Integer quantity=request.get("quantity");
        cartMapper.updateQuantity(id,quantity);

        Map<String, String> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }
}
