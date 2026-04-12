package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.*;
import com.bingzhi.vinyl.mapper.*;
import com.bingzhi.vinyl.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("userOrderController")
@RequestMapping("/api/user/order")
public class OrderController {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ComboMapper comboMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    //创建订单
    @PostMapping("/create")
    @Transactional //加上事务
    public Map<String, Object> createOrder(@RequestHeader("Authorization") String token, @RequestBody Map<String,Object> request){

        Long userId= JwtUtil.getUserId(token);
        Long addressId=Long.valueOf(request.get("addressId").toString());
        String remark=(String) request.getOrDefault("remark","");

        //获取用户购物车
        List<Cart> cartList=cartMapper.findByUserId(userId);
        if(cartList.isEmpty()){
            throw new RuntimeException("购物车为空");
        }

        //计算金额，检查库存
        BigDecimal totalAmount=BigDecimal.ZERO;
        for (Cart cart : cartList){
            if(cart.getType()==0){
                Product product=productMapper.findById(cart.getProductId());
                if(product.getStock()<cart.getQuantity()){
                    throw new RuntimeException("商品"+product.getName()+"库存不足");
                }
                totalAmount=totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }else {
                Combo combo=comboMapper.findById(cart.getComboId());
                if(combo==null){
                    throw new RuntimeException("套餐不存在");
                }
                //检查套餐内每个商品的库存
                List<ComboItem> comboItems=comboMapper.findItemsByComboId(combo.getId());
                for (ComboItem item : comboItems){
                    Product product=productMapper.findById(item.getProductId());
                    if(product==null){
                        throw new RuntimeException("套餐内商品不存在");
                    }
                    int needStock= item.getQuantity()*cart.getQuantity();
                    if(product.getStock()<needStock){
                        throw new RuntimeException("套餐 " + combo.getName() + " 中的商品 " + product.getName() + " 库存不足");
                    }
                }
                totalAmount=totalAmount.add(combo.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }
        }

        //生成订单号
        String orderNo="ORD"+System.currentTimeMillis()+userId;

        //获取信息地址
        Address address=addressMapper.findById(addressId);
        if(address==null){
            throw new RuntimeException("地址不存在");
        }

        //插入订单
        Orders order=new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(1);//待付款
        order.setReceiver(address.getReceiver());
        order.setReceiverPhone(address.getPhone());
        String fullAddress=address.getProvince()+address.getCity()+address.getDistrict()+address.getDetail();
        order.setAddress(fullAddress);
        order.setRemark(remark);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        //插入订单明细+扣减库存
        for (Cart cart:cartList){
            OrderDetail detail=new OrderDetail();
            detail.setOrderId(order.getId());

            if (cart.getType()==0){
                //商品
                Product product=productMapper.findById(cart.getProductId());
                detail.setProductId(product.getId());
                detail.setProductName(product.getName());
                detail.setPrice(product.getPrice());
                detail.setQuantity(cart.getQuantity());
                //扣减商品库存
                productMapper.decreaseStock(product.getId(),cart.getQuantity());
            }else {
                //套餐
                Combo combo =comboMapper.findById(cart.getComboId());
                detail.setProductId(combo.getId());
                detail.setProductName(combo.getName());
                detail.setPrice(combo.getPrice());
                detail.setQuantity(cart.getQuantity());
                //扣减套餐内每个商品的库存
                List<ComboItem> comboItems=comboMapper.findItemsByComboId(combo.getId());
                for (ComboItem item:comboItems){
                    int needStock=item.getQuantity()*cart.getQuantity();
                    productMapper.decreaseStock(item.getProductId(),needStock);
                }
            }
            orderDetailMapper.insert(detail);
        }
        //清空购物车
        cartMapper.deleteByUserId(userId);

        Map<String,Object> result=new HashMap<>();
        result.put("success",true);
        result.put("orderId",order.getId());
        result.put("orderNo",orderNo);
        result.put("totalAmount",totalAmount);
        return result;
    }


    //获取我的订单状态
    @GetMapping("/my")
    public List<Orders> getMyOrders(@RequestHeader("Authorization") String token,@RequestParam(required = false) Integer status){
        Long userId=JwtUtil.getUserId(token);
        if(status!=null&&status!=0){
            return orderMapper.findByUserIdAndStatus(userId,status);
        }
        return orderMapper.findByUserId(userId);
    }

    //获取订单详情
    @GetMapping("/{id}")
    public Map<String,Object> getOrderDetail(@RequestHeader("Authorization") String token,@PathVariable Long id){
        Long userId=JwtUtil.getUserId(token);
        Orders order=orderMapper.findById(id);
        if(order==null||order.getUserId()!=userId){
            throw new RuntimeException("订单不存在");
        }

        List<OrderDetail> details=orderDetailMapper.findByOrderId(id);

        //补充商品图片
        for (OrderDetail detail:details){
            Product product=productMapper.findById(detail.getProductId());
            if(product!=null){
                detail.setProductImage(product.getImage());
            }else {
                Combo combo=comboMapper.findById(detail.getProductId());
                if(combo!=null){
                    detail.setProductImage(combo.getImage());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("details", details);
        return result;
    }

    //模拟支付
    @PutMapping("/pay/{id}")
    public Map<String,String> payOrder(@PathVariable Long id,@RequestHeader("Authorization") String token){
        Long userId=JwtUtil.getUserId(token);
        Orders order=orderMapper.findById(id);
        if (order==null || order.getUserId()!=userId){
            throw new RuntimeException("订单不存在");
        }
        if(order.getStatus()!=1){
            throw  new RuntimeException("订单状态不正确，无法支付");
        }

        //更新订单状态2（待发货）
        orderMapper.updateStatus(id,2);

        Map<String, String> result = new HashMap<>();
        result.put("message", "支付成功");
        return result;
    }

    //取消订单
    @PutMapping("/cancel/{id}")
    public Map<String,String> cancelOrder(@PathVariable Long id,@RequestHeader("Authorization") String token)
    {
        Long userId=JwtUtil.getUserId(token);
        Orders order=orderMapper.findById(id);
        if (order==null || order.getUserId()!=userId){
            throw new RuntimeException("订单不存在");
        }
        if(order.getStatus()!=1){
            throw new RuntimeException("只有待付款订单可取消");
        }

        //获取订单明细
        List<OrderDetail> details=orderDetailMapper.findByOrderId(id);

        //恢复库存
        for (OrderDetail detail:details){
            //先尝试恢复商品库存
            int updated=productMapper.increaseStock(detail.getProductId(),detail.getQuantity());
            if(updated==0){
                //如果不是商品
                Combo combo=comboMapper.findById(detail.getProductId());
                if(combo!=null){
                    List<ComboItem> comboItems=comboMapper.findItemsByComboId(combo.getId());
                    for (ComboItem item : comboItems){
                        productMapper.increaseStock(item.getProductId(),item.getQuantity()*detail.getQuantity());
                    }
                }
            }
        }

        //更新订单状态为5
        orderMapper.updateStatus(id,5);

        Map<String, String> result = new HashMap<>();
        result.put("message", "订单已取消");
        return result;

    }



}
