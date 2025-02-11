package com.abt.material.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 库房信息
 */
@Table(name = "stock_warehouse")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(CommonJpaAudit.class)
public class Warehouse extends AuditInfo implements CommonJpaAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 序号
     */
    @Column(name="sort_no")
    private int sortNo;

    @NotNull(message = "请输入库房名称", groups = {ValidateGroup.Save.class})
    @Size(max = 200)
    @Column(name="name_")
    private String name;

    /**
     * 地点
     */
    @Size(max = 500)
    @Column(name="address")
    private String address;

    /**
     * 负责人工号
     */
    @Column(name="owner_jn")
    private String ownerJobNumber;

    /**
     * 负责人名称
     */
    @Column(name="owner_name")
    private String ownerName;

    /**
     * 是否启用
     */
    @Column(name="enabled_", columnDefinition = "BIT")
    private boolean enabled = true;
    
    
}
