package com.abt.chkmodule.entity;

import com.abt.chkmodule.CheckStandardListConverter;
import com.abt.common.AuditInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * 检测子参数
 * 直接关联checkModule，不再使用关系表。
 */
@Getter
@Setter
@Entity
@Table(name = "check_item")
public class CheckItem extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    /**
     * 检测子参数代码
     */
    @Column(name="code_",  nullable = false, length = 32)
    private String code;

    /**
     * 检测子参数名称
     */
    @Size(max = 50, message = "检测子参数名称，最多输入50字")
    @Column(name="name_", nullable = false, length = 50)
    private String name;

    /**
     * 简要描述
     */
    @Column(name="desc_", length = 128)
    private String description;

    /**
     * 其他常用名称，可以多个
     */
    @Column(name="alias_name", length = 512)
    private String aliasName;

    /**
     * 是否有CMA资质
     */
    @Column(name="is_cma", columnDefinition = "BIT")
    private boolean isCma =  false;

    /**
     * 是否有CNAS资质
     */
    @Column(name="is_cnas", columnDefinition = "BIT")
    private boolean isCnas = false;

    /**
     * 其他资质证书，多个用逗号分隔
     */
    @Column(name="oth_cert", length = 512)
    private String otherCertificate;

    /**
     * 是否启用，默认启用
     */
    @Column(name="is_enabled", columnDefinition = "BIT")
    private boolean enabled = true;


    @Column(name="cm_id", nullable = false, length = 128)
    private String checkModuleId;

    /**
     * 限制范围说明
     */
    @Size(max = 500, message = "最多输入250字")
    @Column(name="restrict_", length = 500)
    private String restrict;

    /**
     * 依据标准，不做jpa关联了
     */
    @Transient
    private List<CheckStandard> standards;

    /**
     * 前端传入
     */
    @Transient
    private List<CheckItemStandardRel> stdRels;

}