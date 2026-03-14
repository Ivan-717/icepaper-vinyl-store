package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    //查询所有员工（分页）
    @Select("SELECT * FROM employee ORDER BY id ASC ")
    List<Employee> findAll();

    //根据id查询员工
    @Select("SELECT * FROM employee where id=#{id}")
    Employee findById(@Param("id") Long id);

    //根据用户名查询员工
    @Select("SELECT * FROM employee WHERE usrname=#(username)")
    Employee findByUsername(@Param("username") String username);



    //新增员工
    /**
     * useGeneratedKeys = true：告诉Mybatis，使用数据库自动生成的组件
     * keyProperty = "id":把生成的主键值赋值给id属性
     **/
    @Insert("INSERT INTO employee(username,password,name,phone,role,status,create_time,update_time)"+
            "VALUES (#{username}, #{password}, #{name}, #{phone}, #{role}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(Employee employee);

    //更新员工信息
    @Update("UPDATE employee SET name=#{name},phone=#{phone},role=#{role},"+
            "status=#{status},update_time = NOW() WHERE id = #{id}")
    int update(Employee employee);


    //更新状态
    @Update("UPDATE employee SET status =#{status},update_time=NOW()  where id=#{id}")
    int updateStatus(@Param("id") Long id,@Param("status") Integer status);

    //重置密码
    @Update("UPDATE employee SET password=#{password},update_time=NOW() where id=#{id}")
    int resetPassword(@Param("id") Long id,@Param("password")String password);

    //删除员工
    @Delete("DELETE FROM employee where id=#{id}")
    int deleteById(@Param("id") Long id);











}
