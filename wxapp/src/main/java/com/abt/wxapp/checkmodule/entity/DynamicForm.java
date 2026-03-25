package com.abt.wxapp.checkmodule.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.wxapp.checkmodule.CheckComponentListConverter;
import com.abt.wxapp.db.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * 动态表单定义（Schema）。组件列表以 JSON 存于 {@code components_json}，接口返回时可与库中结构一致。
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "check_dynamic_form")
@EntityListeners(AuditingEntityListener.class)
public class DynamicForm extends AuditInfo {

    /**
     * 主键：数据库自增（如 SQL Server {@code BIGINT IDENTITY}），单调递增、可比较大小。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 关联检测项目 id
     */
    @Column(name = "cm_id", nullable = false)
    @NotNull(message = "请选择检测项目", groups = {ValidateGroup.Save.class})
    private String checkModuleId;

    /**
     * 检测项目名称
     */
    @Column(name = "cm_name", nullable = false)
    private String checkModuleName;

    /**
     * 表单标题（可选）
     */
    @Column(name = "title_")
    private String title;

    /**
     * 表单说明
     */
    @Column(name = "desc_", length = 500)
    @Size(max = 500, message = "表单说明最多500字")
    private String description;

    /**
     * 持久化为 JSON
     */
    @Convert(converter = CheckComponentListConverter.class)
    @Column(name = "comp_json", columnDefinition = "NVARCHAR(MAX)")
    private List<CheckComponent> components;

    /** 再插入一条新版本时清空 id，由库生成新自增值 */
    public void resetId() {
        this.id = null;
    }
}
