package com.abt.safety.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.safety.converter.SafetyItemConverter;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;


/**
 * 安全检查表单配置项目
 */
@Table(name = "safety_form_item", indexes = {
    @Index(name = "idx_form_id", columnList = "form_id"),
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SafetyFormItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 排序
     */
    @Column(name="sort_no")
    private int sortNo = 0;

    /**
     * 关联SafetyForm id
     */
    @Column(name="form_id")
    private Long formId;

    /**
     * 关联safety_item:id
     * 方便查询
     */
    @Column(name="item_id")
    @NotBlank(message = "检查项目ID不能为空")
    private String itemId;

    /**
     * 关联safety_item:name
     * 方便查询
     */
    @Column(name="item_name")
    @NotBlank(message = "检查项目名称不能为空")
    @Size(max = 200, message = "检查项目名称不能超过200个字符")
    private String itemName;

    /**
     * 关联SafetyItem json，用于保存完整safetyItem信息
     */
    @Column(name="safety_item", columnDefinition = "text")
    @Convert(converter = SafetyItemConverter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SafetyItem safetyItem;

    /**
     * 是否是检查出的问题项目，默认false
     * 若最终检查人确认整改完成，那么认为这些问题已结局
     */
    @Transient
    private boolean isProblem = false;

    /**
     * 检查结果
     */
    @Transient
    private String result;

    /**
     * 结果是附件类型
     */
    @Convert(converter = SystemFileListConverter.class)
    private List<SystemFile> fileList;

}
