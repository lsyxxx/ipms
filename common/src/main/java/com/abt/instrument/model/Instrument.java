package com.abt.instrument.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 实验仪器设备
 */
@Getter
@Setter
@Entity
@Table(name = "T_shebeiguanli")
public class Instrument {
    @Id
    @Size(max = 50)
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    /**
     * 设备分类
     */
    @Size(max = 2)
    @NotNull
    @Column(name = "shebeifenlei", nullable = false, length = 2)
    private String type;

    /**
     * 设备状态
     */
    @Size(max = 2)
    @NotNull
    @Column(name = "shebeistatus", nullable = false, length = 2)
    private String status;

    /**
     * 设备编号
     */
    @Size(max = 50)
    @Column(name = "shebeibianhao", length = 50)
    private String code;

    /**
     * 设备名称
     */
    @Size(max = 50)
    @NotNull
    @Column(name = "shebeiname", nullable = false, length = 50)
    private String name;

    /**
     * 设备规格
     */
    @Size(max = 50)
    @NotNull
    @Column(name = "shebeiguige", nullable = false, length = 50)
    private String specification;

    /**
     * 设备数量
     */
    @Column(name = "shebeinum")
    private Integer num;

    /**
     * 制造厂家
     */
    @Size(max = 200)
    @Column(name = "zhizaochangjia", length = 200)
    private String manufacturer;

    /**
     * 出厂编号
     */
    @Size(max = 200)
    @Column(name = "chuchangbianhao", length = 200)
    private String factoryCode;

    /**
     * 测量范围
     */
    @Size(max = 100)
    @Column(name = "celiangfanwei", length = 100)
    private String measureRange;

    /**
     * 精确等级
     */
    @Size(max = 100)
    @Column(name = "jingquedengji", length = 100)
    private String accuracy;

    /**
     * 资产来源
     */
    @Size(max = 2)
    @Column(name = "zichansource", length = 2)
    private String source;

    /**
     * 购入时间
     */
    @Column(name = "gourudata")
    private LocalDateTime buyTime;

    /**
     * 使用部门
     */
    @Size(max = 200)
    @Column(name = "sydept", length = 200)
    private String useDept;

    /**
     * 负责人
     */
    @Size(max = 50)
    @Column(name = "zremp", length = 50)
    private String responsibleUserid;

    /**
     * 安装地址
     */
    @Size(max = 200)
    @Column(name = "anzhuangAddress", length = 200)
    private String setupAddress;

    /**
     * 供应商
     */
    @Size(max = 200)
    @Column(name = "gongyingshang", length = 200)
    private String supplier;

    /**
     * 供应商联系人
     */
    @Size(max = 100)
    @Column(name = "lianxiren", length = 100)
    private String supplierUser;

    /**
     * 供应商联系人电话
     */
    @Size(max = 100)
    @Column(name = "lianxitel", length = 100)
    private String supplierTel;

    /**
     * 备注
     */
    @Size(max = 200)
    @Column(name = "note", length = 200)
    private String note;

    /**
     * 附件地址
     */
    @Lob
    @Column(name = "filePath")
    private String filePath;


    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private LocalDateTime operateDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @Column(name = "smac", length = 50)
    private String smac;

    /**
     * 价格
     */
    @Column(name = "price", columnDefinition = "decimal(11,2)")
    private Double price;

    /**
     * 档案盒号
     */
    @Size(max = 32)
    @Column(name="boxNo", length = 32)
    private String boxNo;


}