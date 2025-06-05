package com.abt.safety.entity;

import com.abt.common.listener.JpaUsernameListener;
import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 安全检查项目配置
 * 支持多种检查结果类型：
 * - 布尔型（是/否）：使用"1"/"0"表示
 * - 文本型：自由文本描述
 */
@Table(name = "safety_item", indexes = {
    @Index(name = "idx_del", columnList = "is_deleted"),
    @Index(name = "idx_enabled", columnList = "enabled_")
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(JpaUsernameListener.class)
public class SafetyItem extends AuditInfo {
    // 结果类型常量
    public static final String INPUT_TYPE_BOOLEAN = "BOOLEAN"; // 布尔型（是/否）
    public static final String INPUT_TYPE_TEXT = "TEXT";       // 文本型
    public static final String INPUT_TYPE_NUMBER = "NUMBER";   // 数值型
    public static final String INPUT_TYPE_SELECT = "SELECT";   // 选择型
    public static final String INPUT_TYPE_UPLOAD_PIC = "UPLOAD_PIC";   // 上传图片

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 检查项目名称
     */
    @Column(name = "name_")
    private String name;

    /**
     * 输入类型
     * 可选值：BOOLEAN, TEXT,
     */
    @Column(name = "input_type")
    private String inputType;

    /**
     * 是否必填
     */
    @Column(name="required_")
    private Boolean required = true;

    /**
     * 是否删除，逻辑删除
     */
    @Column(name="is_deleted")
    private boolean isDeleted = false;

    /**
     * 是否启用
     */
    @Column(name="enabled_")
    private boolean enabled = true;

    /**
     * 排序
     */
    @Column(name="sort_no", columnDefinition = "INT DEFAULT 0") 
    private int sortNo;

    /**
     * 自定义分类
     */
    @Column(name="type_")
    private String type;

}
