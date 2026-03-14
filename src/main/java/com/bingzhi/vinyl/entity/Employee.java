package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Employee {
    private  Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
