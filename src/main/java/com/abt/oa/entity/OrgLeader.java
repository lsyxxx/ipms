package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 副总负责部门配置
 */
@Getter
@Setter
@Table(name = "org_leader")
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({CommonJpaAuditListener.class})
public class OrgLeader extends AuditInfo implements CommonJpaAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 负责人名称
     */
    @NotNull(message = "负责人姓名(name)不能为空", groups = {ValidateGroup.All.class})
    @Column(name="name_")
    private String name;

    /**
     * 负责人工号
     */
    @NotNull(message = "负责人工号(jobNumber)不能为空", groups = {ValidateGroup.All.class})
    @Column(name="emp_num")
    private String jobNumber;

    /**
     * 负责部门id
     */
    @NotNull(message = "负责部门id(deptId)不能为空", groups = {ValidateGroup.All.class})
    @Column(name="dept_id")
    private String deptId;

    @NotNull(message = "负责部门名称(deptName)不能为空", groups = {ValidateGroup.All.class})
    @Column(name="dept_name")
    private String deptName;

    /**
     * 角色：副总/部门经理
     */
    @Column(name="role_")
    private String role;

}
