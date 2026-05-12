package com.abt.wxapp.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 订单列表项（含展示文案，前端勿自行推断状态）
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderListItemVo {
    private String id;
    private String orderNo;
    private String checkModuleName;
    private Integer sampleCount;
    private Integer status;
    private String statusText;
    private String progressText;
    private String createTime;
}
