package com.bingzhi.vinyl.mapper;

import com.bingzhi.vinyl.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    //根据用户名查询用户
    @Select("SELECT * from user WHERE username=#{username}")
    User findByUsername(@Param("username") String username);

    //插入用户
    @Select("INSERT INTO user (username, password, phone, avatar, create_time, update_time) " +
            "VALUES (#{username}, #{password}, #{phone}, #{avatar}, NOW(), NOW())")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(User user);




}
