package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Combo {
    private Long id;
    private String name;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 套餐包含的商品列表（不是数据库字段）
    private List<ComboItem> items;
}
