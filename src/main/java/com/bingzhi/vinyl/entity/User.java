package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private long id;
    private String username;
    private String password;
    private String phone;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
