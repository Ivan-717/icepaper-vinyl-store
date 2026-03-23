package com.bingzhi.vinyl.controller.user;

import com.bingzhi.vinyl.entity.User;
import com.bingzhi.vinyl.mapper.UserMapper;
import com.bingzhi.vinyl.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    //用户注册
    @PostMapping("/register")
    public Map<String,Object> register(@RequestBody Map<String,String> request){
        String username=request.get("username");
        String password=request.get("password");
        String phone=request.get("phone");

        //检查用户名是否存在
        User existUser=userMapper.findByUsername(username);
        if(existUser!=null){
            throw new RuntimeException("用户名已存在");
        }

        //创建用户
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);

        userMapper.insert(user);

        Map<String,Object> result=new HashMap<>();
        result.put("success",true);
        result.put("message","注册成功");
        result.put("userId",user.getId());
        return result;
    }

    //用户登录
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,String> request){
        String username=request.get("username");
        String password=request.get("password");

        //查询用户
        User user=userMapper.findByUsername(username);
        if(user==null){
            throw new RuntimeException("用户不存在");
        }
        if(!password.equals(user.getPassword())){
            throw new RuntimeException("密码错误");
        }

        //生成jwt token
        String token= JwtUtil.generateToken(user.getId(),user.getUsername(),"user");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }
}
