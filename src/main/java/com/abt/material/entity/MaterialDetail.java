package com.abt.material.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "T_stockcataDetail")
public class MaterialDetail {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 物品名称
     */
    @Size(max = 50)
    @NotNull
    @Column(name = "fname", nullable = false, length = 50)
    private String name;

    /**
     * 型号规格
     */
    @Size(max = 50)
    @Column(name = "Xhgz", length = 50)
    private String specification;

    /**
     * 单位
     */
    @Size(max = 50)
    @Column(name = "Unit", length = 50)
    private String unit;

    /**
     * 单价
     */
    @Column(name = "price", precision = 18, scale = 2)
    private BigDecimal price;

    /**
     * 备注
     */
    @Size(max = 300)
    @Column(name = "Remark", length = 300)
    private String remark;

    /**
     * 物品分类id
     */
    @Size(max = 50)
    @NotNull
    @Column(name = "stockcatalogId", nullable = false, length = 50)
    private String stockcatalogId;

    /**
     * 是否启用
     */
    @Size(max = 50)
    @Column(name = "IsActice", length = 50)
    private String isActive;

    /**
     * 序号
     */
    @Column(name = "XH")
    private Integer index;

    /**
     * 初始库存
     */
    @Column(name = "CshNum")
    private Integer beginningInventory;

    /**
     * 当前库存
     */
    @Column(name = "KcNum")
    private Integer currentInventory;

    /**
     * 用途
     */
    @Column(name = "usage")
    private String usage;

    /**
     * 使用部门id
     */
    @Column(name = "deptId", length = 128)
    private String deptId;


    /**
     * 使用部门名称
     */
    @Column(name = "deptName", length = 128)
    private String deptName;



}