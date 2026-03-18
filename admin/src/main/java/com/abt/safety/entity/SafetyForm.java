package com.abt.safety.entity;

import com.abt.common.model.AuditInfo;
import com.abt.safety.model.CheckType;
import com.abt.safety.model.LocationType;
import com.abt.sys.model.WithQuery;
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
public class SafetyForm extends AuditInfo implements WithQuery<SafetyForm> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 表单名称
     */
    @Column(name="name_")
    private String name;

    /**
     * 检查地点
     */
    @Size(max = 400, message = "检查地点输入最多100字")
    @Column(name = "location_", length = 400)
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
     * 排序
     */
    @Column(name="sort_no")
    private int sortNo = 0;


    /**
     * 表单分类:环境，安全
     */
    @Enumerated(EnumType.STRING)
    @Column(name="check_type", length = 32)
    private CheckType checkType;

    @Transient
    private String checkTypeDesc;
    @Transient
    private String locationTypeDesc;


    @Enumerated(EnumType.STRING)
    @Column(name="loc_type", length = 16)
    private LocationType locationType;

    public String getLocationTypeDesc() {
        this.locationTypeDesc = this.locationType == null ? "" : this.locationType.getDescription();
        return this.locationTypeDesc;
    }

    public String getCheckTypeDesc() {
        this.checkTypeDesc =  this.checkType == null ? "" : this.checkType.getDescription();
        return this.checkTypeDesc;
    }


    /**
     * 检查项目列表
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    @OrderBy("sortNo ASC")
    private List<SafetyFormItem> items = new ArrayList<>();

    @Override
    public SafetyForm afterQuery() {
        this.getLocationTypeDesc();
        this.getCheckTypeDesc();
        return this;
    }
}
