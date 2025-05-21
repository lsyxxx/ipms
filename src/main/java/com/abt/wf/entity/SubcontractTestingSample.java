package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 外送检测清单
 */
@Table(name = "wf_sbct_sample", indexes = {
        @Index(name = "idx_mid", columnList = "mid"),
        @Index(name = "idx_entrustid", columnList = "entrust_id")
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubcontractTestingSample {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联主表id
     */
    @Column(name="mid")
    private String mid;

    /**
     * 关联主表
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mid", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    @NotFound(action= NotFoundAction.IGNORE)
    private SubcontractTesting main;

    /**
     * 委托单号
     */
    @Column(name="entrust_id", nullable = false)
    private String entrustId;


    /**
     * 井号
     */
    @Column(name="well_no")
    private String wellNo;

    /**
     * 样品检测编号
     */
    @Column(name="new_sample_no", nullable = false)
    private String newSampleNo;

    /**
     * 原始样号
     */
    @Column(name="old_sample_no")
    private String oldSampleNo;

    /**
     * 检测项目id
     */
    @Column(name="check_module_id", nullable = false)
    private String checkModuleId;

    /**
     * 检测项目名称
     */
    @NotNull(message = "检测项目名称不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="check_module_name", nullable = false)
    private String checkModuleName;


    /**
     * 深度
     */
    @Column(name="depth_")
    private String depth;

    /**
     * 岩性
     */
    @Column(name="name_desc")
    private String nameDesc;

    /**
     * 层位
     */
    @Column(name="ceng_wei")
    private String cengWei;

    //---- 气样有其他的属性----

    /**
     * 压力
     */
    @Column(name="pressure_")
    private String pressure;

    /**
     * 取样位置
     */
    @Column(name="sample_addr")
    private String sampleAddress;
    
}
