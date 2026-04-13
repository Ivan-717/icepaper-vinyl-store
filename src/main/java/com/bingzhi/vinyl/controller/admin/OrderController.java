package com.bingzhi.vinyl.controller.admin;

import com.bingzhi.vinyl.entity.OrderDetail;
import com.bingzhi.vinyl.entity.Orders;
import com.bingzhi.vinyl.mapper.OrderDetailMapper;
import com.bingzhi.vinyl.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController("adminOrderController")
@RequestMapping("/api/admin/orders")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    //查询所有订单
    @GetMapping
    public List<Orders> list(@RequestParam(required = false) Integer status){
        if(status!=null&&status!=0){
            return orderMapper.findByStatus(status);
        }
        return orderMapper.findAll();
    }

    //查看订单详情
    @GetMapping("/{id}")
    public Map<String,Object> findById(@PathVariable Long id){
        Orders order=orderMapper.findById(id);
        if(order == null){
            return null;
        }

        List<OrderDetail> details=orderDetailMapper.findByOrderId(id);

        Map<String,Object> result=new HashMap<>();
        result.put("order", order);
        result.put("details", details);
        return result;

    }

    //更新订单状态
    @PutMapping("/{id}/status")
    public Map<String,String> updateStatus(@PathVariable Long id,@RequestBody Map<String,Integer> request){
        Integer status=request.get("status");
        orderMapper.updateStatus(id,status);

        Map<String, String> result = new HashMap<>();
        result.put("message", "状态更新成功");
        return result;
    }

    //今日订单统计
    @GetMapping("/today-stats")
    public Map<String,Object> getTodayStats(){
        LocalDateTime start=LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end=start.plusDays(1);

        List<Orders> orders=orderMapper.findByCreateTimeBetween(start,end);

        int count = orders.size();
        BigDecimal total=orders.stream()
                //把每个订单转换成一个金额
                .map(Orders::getTotalAmount)
                //把所有的金额加起来，从0开始累加
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("total", total);
        return result;
    }

    //员工发货
    @PutMapping("/deliver/{id}")
    public Map<String,String> deliver(@PathVariable Long id){
        Orders order=orderMapper.findById(id);
        if(order==null || order.getStatus()!=2){
            throw  new RuntimeException("只有待发货订单可以发货");
        }
        orderMapper.updateStatus(id,3);

        Map<String, String> result = new HashMap<>();
        result.put("message", "发货成功");
        return result;
    }

}
