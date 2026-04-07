package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComboItem {
    private  Long id;
    private Long comboId;
    private Long productId;
    private Integer quantity;
    private String productImage;

    //关联的商品信息（方便前端显示）
    private String productName;
    private String productArtist;
    private BigDecimal productPrice;
}
