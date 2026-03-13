package com.bingzhi.vinyl.controller;

import com.bingzhi.vinyl.entity.SystemConfig;
import com.bingzhi.vinyl.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/shop")
public class ShopController {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    //获取店铺状态
    @GetMapping("/status")
    public Map<String,Integer> getShopStatus(){
        SystemConfig config=systemConfigMapper.getConfig();
        Map<String,Integer> result=new HashMap<>();
        result.put("status",config!=null?config.getShopStatus():1);
        return result;
    }

    //更新店铺状态
    @PutMapping("/status")
    //@RequestBody:告诉spring:把前端传来的json数据转成后面的Map对象
    public Map<String,String> updateShopStatus(@RequestBody Map<String,Integer> request){
        //取前端发来的信息
        Integer status=request.get("status");
        if (status==null || (status!=0 && status != 1)){
            throw new RuntimeException("状态值必须是0或1");
        }

        systemConfigMapper.updateShopStatus(status);

        Map<String,String> result=new HashMap<>();
        result.put("message","更新成功");
        return result;
    }
}
