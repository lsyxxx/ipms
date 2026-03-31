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
 * 动态表单定义结构
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "check_dynamic_scheme")
@EntityListeners(AuditingEntityListener.class)
public class DynamicScheme extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 关联检测项目 id。是单独为小程序设计的检测项目。比如薄片鉴定，有2个，一个是用于小程序，一个用于WEB
     * TODO:
     */
    @Column(name = "cm_id", nullable = false)
    @NotNull(message = "请选择检测项目", groups = {ValidateGroup.Save.class})
    private String checkModuleId;

    /**
     * 检测项目名称
     * TODO:
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
     * component json，保存组件列表的json
     */
    @Convert(converter = CheckComponentListConverter.class)
    @Column(name = "comp_json", columnDefinition = "NVARCHAR(MAX)")
    private List<CheckComponent> components;

    /**
     * 重置id为空
     */
    public void resetId() {
        this.id = null;
    }
}
