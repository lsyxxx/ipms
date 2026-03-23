package com.abt.wxapp.cart.entity;

import com.abt.wxapp.db.AuditInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 微信小程序购物车实体类
 */
@Getter
@Setter
@Entity
@ToString
@Table(name = "wx_cart")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({AuditingEntityListener.class})
public class Cart extends AuditInfo {

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