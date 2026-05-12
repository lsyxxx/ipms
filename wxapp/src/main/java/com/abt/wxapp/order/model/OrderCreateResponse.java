package com.abt.wxapp.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 创建订单响应
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResponse {
    private String orderId;
    private String orderNo;
}
