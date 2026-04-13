package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.SystemConfig;
import com.bingzhi.vinyl.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController("userShopController")
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    //获取店铺状态
    @GetMapping("/status")
    public Map<String,Integer> getShopStatus(){
        SystemConfig config=systemConfigMapper.getConfig();
        Map<String,Integer> result=new HashMap<>();
        result.put("status", config != null ? config.getShopStatus() : 1);
        return result;
    }
}
