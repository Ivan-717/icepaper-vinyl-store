package com.bingzhi.vinyl.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Orders {
    private long id;
    private String orderNo;
    private  long userId;
    private BigDecimal totalAmount;
    private Integer status;

    private String receiver;        // 收货人
    private String receiverPhone;   // 收货人电话
    private String address;         // 收货地址
    private String remark;          // 备注

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
