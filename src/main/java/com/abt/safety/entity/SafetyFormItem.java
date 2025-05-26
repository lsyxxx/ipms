package com.abt.safety.entity;

import com.abt.safety.converter.SafetyItemConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
    private String formId;

    /**
     * 关联safety_item:id
     */
    @Column(name="item_id")
    private String itemId;

    @Column(name="item_name")
    private String itemName;

    /**
     * 关联SafetyItem json
     */
    @Column(name="safety_item", columnDefinition = "text")
    @Convert(converter = SafetyItemConverter.class)
    private SafetyItem safetyItem;
}
