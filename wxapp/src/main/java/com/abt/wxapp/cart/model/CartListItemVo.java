package com.abt.wxapp.cart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 小程序购物车列表项
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartListItemVo {

    private String id;

    private String checkModuleId;

    private String checkModuleName;

    private Integer sampleCount;

    private String formDataId;

    private String totalPrice;

    private String image;

    private boolean active;
}
