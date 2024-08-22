package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 野外考勤设置
 * 每次修改都是添加一条，同时新增版本号
 */
@Table(name = "fw_atd_setting")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({CommonJpaAuditListener.class})
public class FieldWorkAttendanceSetting extends AuditInfo implements CommonJpaAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 同一个项目的标识
     */
    @NotBlank(groups = {ValidateGroup.Insert.class}, message = "标识id不能为空!")
    @Column(name="vid", length = 128)
    private String vid;

    @Column(name="sort", columnDefinition="TINYINT")
    private int sort;

    @NotNull(groups = {ValidateGroup.Insert.class}, message = "补贴名称不能为空")
    @Column(name="name", columnDefinition="VARCHAR(128)", nullable = false)
    private String name;

    /**
     * 生产奖金
     */
    @Column(name="prod_allowance", columnDefinition="DECIMAL(10,2)")
    private double productionAllowance;
    /**
     * 餐补
     */
    @Column(name="meal_allowance", columnDefinition="DECIMAL(10,2)")
    private double mealAllowance;
    /**
     * 奖金合计
     */
    @Column(name="sum_allowance", columnDefinition="DECIMAL(10,2)")
    private double sumAllowance;

    @Column(name="unit_", columnDefinition="VARCHAR(32)")
    private String unit;

    @Column(name="desc_", columnDefinition="VARCHAR(1000)")
    private String description;

    @Column(name="group_", columnDefinition="VARCHAR(128)")
    private String group;

    /**
     * 是否启用
     */
    @Column(name="enabled_", columnDefinition="BIT")
    private boolean enabled = true;

    @Column(name="bg_color")
    private String backgroundColor;

    /**
     * 配置样式(css)
     */
    @Column(name="style_", columnDefinition="VARCHAR(1000)")
    private String style;

    /**
     * 组件类型
     * 1. 单选
     * 2. 多选
     */
    @Column(name="com_type", columnDefinition="TINYINT")
    private int componentType = COMPONENT_TYPE_RADIO;

    /**
     * 是否算作出勤
     * 用于计算出勤天数
     */
    @Column(name="is_work", columnDefinition = "BIT")
    private boolean isWork = true;

    /**
     * 简写
     */
    @Column(name="short_name", columnDefinition = "VARCHAR(16)")
    private String shortName;

    /**
     * 表示符号
     */
    @Column(name="symbol_", columnDefinition = "VARCHAR(64)")
    private String symbol;

    /**
     * 版本号
     * 正式版本从1开始
     */
    @Column(name="version_", columnDefinition = "INT")
    private int version = 0;

    /**
     * 版本数量
     */
    @Transient
    private long versionCount = 0;

    public static final int COMPONENT_TYPE_RADIO = 1;
    public static final int COMPONENT_TYPE_CHECKBOX = 2;



    public FieldWorkAttendanceSetting newVersion(FieldWorkAttendanceSetting setting) {
        setting.setId(null);
        setting.setVersion(setting.increaseVersion());
        return setting;
    }


    private int increaseVersion() {
        return ++version;
    }



}
