package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.impl.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 野外考勤设置
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

    @Column(name="sort", columnDefinition="TINYINT")
    private int sort;

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

    public static final int COMPONENT_TYPE_RADIO = 1;
    public static final int COMPONENT_TYPE_CHECKBOX = 2;


}
