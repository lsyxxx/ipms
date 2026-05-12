package com.abt.wxapp.order.entity;

import com.abt.wxapp.db.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 小程序检测预约订单
 */
@Getter
@Setter
@Entity
@Table(name = "wx_order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WxOrder extends AuditInfo {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @NotNull
    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    @NotNull
    @Column(name = "check_module_id", nullable = false, length = 128)
    private String checkModuleId;

    @Column(name = "check_module_name", length = 200)
    private String checkModuleName;

    @NotNull
    @Column(name = "scheme_id", nullable = false)
    private Long schemeId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_", nullable = false, length = 24)
    private WxOrderStatus status = WxOrderStatus.PENDING_PAY;

    @Column(name = "sample_count")
    private Integer sampleCount;

    @Lob
    @Column(name = "option_json", columnDefinition = "NVARCHAR(MAX)")
    private String optionJson;

    @Column(name = "amount", length = 50)
    private String amount;
}
