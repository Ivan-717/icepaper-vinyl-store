package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemConfig {
    private Long id;

    private Integer shopStatus;

    private LocalDateTime updateTime;
}
