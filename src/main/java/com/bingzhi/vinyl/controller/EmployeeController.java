package com.bingzhi.vinyl.controller;

import com.bingzhi.vinyl.entity.Employee;
import com.bingzhi.vinyl.mapper.EmployeeMapper;
import com.bingzhi.vinyl.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeMapper employeeMapper;

    //查询所有员工
    @GetMapping("/list")
    public List<Employee> list(){
        return employeeMapper.findAll();
    }

    //根据id查询
    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id){
        return employeeMapper.findById(id);
    }

    //新增员工
    @PostMapping
    public Map<String, Object> add(@RequestBody Employee employee){
        //默认密码
        employee.setPassword("123456");
        //默认角色
        if(employee.getRole()==null){
            employee.setRole("employee");
        }
        //默认状态启用
        if(employee.getStatus()==null){
            employee.setStatus(1);
        }

        employeeMapper.insert(employee);

        Map<String,Object> result=new HashMap<>();
        result.put("success",true);
        result.put("message","新增成功");
        result.put("data",employee);
        return result;

    }

    //更新员工
    @PutMapping
    public Map<String,String>update(@RequestBody Employee employee){
        employeeMapper.update(employee);

        Map<String, String> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }

    //更新状态
    @PutMapping("/status")
    public Map<String,String> updateStatus(@RequestBody Map<String,Object> request){
        //request.get("id"):从对象中请求到key为“id”的参数值
        Long id=Long.valueOf(request.get("id").toString());
        Integer status=(Integer) request.get("status");

        employeeMapper.updateStatus(id,status);

        Map<String,String> result=new HashMap<>();
        result.put("message","状态更新成功");
        return result;
    }

    //重置密码
    @PutMapping("/reset-password")
    public Map<String,String> resetPassword(@RequestBody Map<String,Object> request){
        Long id=Long.valueOf(request.get("id").toString());
        employeeMapper.resetPassword(id,"123456");
        Map<String,String> result=new HashMap<>();
        result.put("message","密码已重置为 123456");
        return result;
    }

    //删除员工
    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable Long id){
        employeeMapper.deleteById(id);

        Map<String,String> result=new HashMap<>();
        result.put("message","删除成功");
        return result;
    }

    //登录方法
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,String> request){
        String username=request.get("username");
        String password=request.get("password");

        Employee employee=employeeMapper.findByUsername(username);

        if(employee==null){
            throw new RuntimeException("用户名或密码错误");
        }

        if(!password.equals(employee.getPassword())){
            throw new RuntimeException("用户名或密码错误");
        }

        //验证账号状态
        if (employee.getStatus()==0){
            throw new RuntimeException("账号已被禁用");
        }

        //验证通过，生成JWT token
        String token= JwtUtil.generateToken(
                employee.getId(),
                employee.getUsername(),
                employee.getRole()
        );

        //返回结果
        Map<String,Object> result=new HashMap<>();
        result.put("token",token);
        result.put("userId",employee.getId());
        result.put("username", employee.getUsername());
        result.put("name", employee.getName());
        result.put("role", employee.getRole());

        return result;
    }


}
