package com.abt.wxapp.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 订单详情
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private String id;
    private String orderNo;
    private String checkModuleName;
    private Integer sampleCount;
    private Integer status;
    private String statusText;
    private String progressText;
    private String createTime;
    private String optionJson;
}
