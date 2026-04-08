package com.abt.chkmodule.entity;

import com.abt.chkmodule.CheckComponentListConverter;
import com.abt.common.AuditInfo;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.SaveMode;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 动态表单定义结构
 *
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "check_dynamic_scheme")
public class DynamicScheme extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 关联检测项目 id。可为小程序单独配置的检测项目。
     */
    @Column(name = "cm_id", nullable = false)
    @NotNull(message = "请选择检测项目", groups = {ValidateGroup.Save.class, ValidateGroup.Temp.class})
    private String checkModuleId;

    @Column(name = "cm_name", nullable = false)
    private String checkModuleName;

    /** 表单标题（可选） */
    @NotNull(message = "表单标题不能为空", groups = {ValidateGroup.Save.class, ValidateGroup.Temp.class})
    @Column(name = "title_", nullable = false)
    private String title;

    /** 表单说明 */
    @Column(name = "desc_", length = 500)
    @Size(max = 500, message = "表单说明最多500字")
    private String description;

    /**
     * 组件列表 JSON
     * 不再单独表保存了
     */
    @Valid
    @NotNull(message = "动态表单必须添加组件", groups = {ValidateGroup.Save.class})
    @Convert(converter = CheckComponentListConverter.class)
    @Column(name = "comp_json", columnDefinition = "NVARCHAR(MAX)")
    private List<CheckComponent> components;

    /**
     * 状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name="status_", columnDefinition = "tinyint")
    private SaveMode status = SaveMode.TEMP;


    public void setTemp() {
        this.status = SaveMode.TEMP;
    }

    public void setPublish() {
        this.status = SaveMode.SAVE;
    }

    /** 持久化前清空 id（用于复制新建等场景） */
    public void resetId() {
        this.id = null;
    }
}
