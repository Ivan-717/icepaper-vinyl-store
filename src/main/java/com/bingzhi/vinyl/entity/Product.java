package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String name;
    private String artist;
    // BigDecimal专门用来处理高精度小数的引用数据类型
    private BigDecimal price;
    private  Integer stock;
    private  String image;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
