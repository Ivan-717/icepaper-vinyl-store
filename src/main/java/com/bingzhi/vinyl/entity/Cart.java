package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    //关键的商品信息
    private String productName;
    private String productArtist;
    private Double productPrice;
    private String productImage;

    private Long comboId;   // 套餐ID（如果是套餐就有值）
    private Integer type;   // 0商品 1套餐
}
