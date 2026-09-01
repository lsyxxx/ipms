package com.abt.finance.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 费用分类
 */
@Getter
@Setter
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Table(name = "fi_expense_category", indexes = {
        @Index(name = "idx_fi_exp_cat_code", columnList = "code_"),
        @Index(name = "idx_fi_exp_cat_enabled", columnList = "enabled_"),
        @Index(name = "idx_fi_exp_cat_sort", columnList = "sort_no")
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EntityListeners(CommonJpaAuditListener.class)
public class ExpenseCategory extends AuditInfo implements CommonJpaAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 分类编号，用户手动录入
     */
    @NotBlank(message = "分类编号不能为空", groups = {ValidateGroup.Save.class})
    @Size(max = 64, message = "分类编号不能超过64个字符", groups = {ValidateGroup.Save.class})
    @Column(name = "code_", length = 64, nullable = false)
    private String code;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空", groups = {ValidateGroup.Save.class})
    @Size(max = 128, message = "分类名称不能超过128个字符", groups = {ValidateGroup.Save.class})
    @Column(name = "name_", length = 128, nullable = false)
    private String name;

    /**
     * 排序号，按录入顺序递增，可手动调整
     */
    @Column(name = "sort_no", columnDefinition = "INT DEFAULT 0")
    private int sortNo;

    /**
     * 是否启用
     */
    @Column(name = "enabled_", columnDefinition = "BIT")
    private boolean enabled = true;

    /**
     * 备注
     */
    @Size(max = 256, message = "备注不能超过256个字符", groups = {ValidateGroup.Save.class})
    @Column(name = "remark_", length = 256)
    private String remark;
}
