package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 部门费用详细
 */

@Table(name = "wf_cost_dept")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CostDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联单号
     */
    @NotNull(groups = ValidateGroup.Save.class, message = "关联单号(refCode)不能为空")
    @Column(name="ref_code", nullable = false)
    private String refCode;

    private Double cost;

    /**
     * 部门Id
     */
    @Column(name="dept_id", nullable = false)
    private String deptId;

    @Column(name="dept_name")
    private String deptName;

    @Transient
    private String error;
}
