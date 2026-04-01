package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.Address;
import com.bingzhi.vinyl.mapper.AddressMapper;
import com.bingzhi.vinyl.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/address")
public class AddressController {
    @Autowired
    private AddressMapper addressMapper;

    //获取用户地址列表
    @GetMapping
    public List<Address> list(@RequestHeader("Authorization") String token){
        Long userId= JwtUtil.getUserId(token);
        return addressMapper.findByUserId(userId);
    }

    //新增地址
    @PostMapping
    public Map<String,Object> add(@RequestHeader("Authorization") String token,@RequestBody Address address){
        Long userId=JwtUtil.getUserId(token);
        address.setUserId(userId);

        //如果是默认地址，先把其他地址的默认去掉
        if(address.getIsDefault()==1){
            addressMapper.resetDefault(userId);
        }

        addressMapper.insert(address);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "添加成功");
        result.put("data", address);
        return result;
    }

    //更新地址
    @PutMapping
    public Map<String,String> update(@RequestHeader("Authorization") String token,@RequestBody Address address){
        Long userId=JwtUtil.getUserId(token);
        //防止传错id
        address.setUserId(userId);

        if(address.getIsDefault()==1){
            addressMapper.resetDefault(userId);
        }

        addressMapper.update(address);

        Map<String, String> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }

    //删除地址
    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable Long id){
        addressMapper.deleteById(id);

        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }

    //设为默认地址
    @PutMapping("/default/{id}")
    public Map<String,String> setDefault(@PathVariable Long id,@RequestHeader("Authorization") String token){
        Long userId=JwtUtil.getUserId(token);

        addressMapper.resetDefault(userId);

        addressMapper.updateDefault(id,1);

        Map<String, String> result = new HashMap<>();
        result.put("message", "设置成功");
        return result;
    }

}
