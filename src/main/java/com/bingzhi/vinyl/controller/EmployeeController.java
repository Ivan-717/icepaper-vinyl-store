package com.bingzhi.vinyl.controller;

import com.bingzhi.vinyl.entity.Employee;
import com.bingzhi.vinyl.mapper.EmployeeMapper;
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










}
