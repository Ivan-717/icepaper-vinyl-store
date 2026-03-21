package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetail {
    private long id;
    private long orderId;
    private long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;

}
