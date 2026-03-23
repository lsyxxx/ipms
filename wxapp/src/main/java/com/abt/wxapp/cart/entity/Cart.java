package com.abt.wxapp.cart.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 微信小程序购物车实体类
 */
@Data
@Entity
@Table(name = "wx_cart")
public class Cart {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "check_module_id", length = 50)
    private String checkModuleId;

    @Column(name = "check_module_name", length = 100)
    private String checkModuleName;

    @Column(name = "sample_count")
    private Integer sampleCount;

    @Column(name = "check_item_field_data", length = 2000)
    private String checkItemFieldData;

    @Column(name = "total_price", length = 50)
    private String totalPrice;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "check_item_params_brief", length = 500)
    private String checkItemParamsBrief;

}