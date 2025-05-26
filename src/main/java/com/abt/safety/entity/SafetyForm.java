package com.abt.safety.entity;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Table(name = "safety_form", indexes = {
    @Index(name = "idx_form_enabled", columnList = "enabled_")
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SafetyForm extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 表单名称
     */
    @Column(name="name_")
    private String name;

    /**
     * 检查地点
     */
    @Size(max = 400, message = "检查地点输入最多100字")
    @Column(name = "location_", length = 400, unique = true)
    private String location;

    /**
     * 负责人jobNumber
     */
    @Column(name = "responsible_jno", length = 128)
    private String responsibleJno;
    /**
     * 负责人name
     */
    @Column(name="responsible_name", length = 128)
    private String responsibleName;

    /**
     * 是否启用
     */
    @Column(name = "enabled_")
    private boolean enabled;

    /**
     * 是否删除，逻辑删除
     */
    @Column(name = "is_deleted")
    private boolean isDeleted;

    /**
     * 检查项目列表
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    private List<SafetyFormItem> items = new ArrayList<>();
}
