package com.abt.chemicals.entity;

import com.abt.chemicals.model.StandardType;
import com.abt.common.config.ValidateGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 标准
 */
@Data
@Table(name = "chm_standard")
@Accessors(chain = true)
@DynamicInsert
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Standard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "标准号不能为空", groups = {ValidateGroup.Save.class})
    @Column(name="code_", columnDefinition="VARCHAR(128)")
    private String code;

    @NotBlank(message = "关联化学品不能为空", groups = {ValidateGroup.Update.class})
    @Column(name="chm_id", columnDefinition="VARCHAR(128)")
    private String chemicalId;

    @Column(name="name_", columnDefinition="VARCHAR(128)")
    private String name;

    @Column(name="desc_", columnDefinition="VARCHAR(512)")
    private String description;
    /**
     * 国标/行标/企业标准/地方/...
     * @see StandardType
     */
    @Column(name="type_", columnDefinition="VARCHAR(64)")
    private String type;
    /**
     * 是否使用
     */
    private boolean enable = true;
    @Column(name="file_id", columnDefinition="VARCHAR(128)")
    private String fileId;
    private String fileUrl;
    /**
     * 发行时间
     */
    private LocalDateTime publishTime;

    public static Standard of(String code, String chemicalId, String id) {
        Standard standard = new Standard().setCode(code).setChemicalId(chemicalId);
        if (StringUtils.isNotBlank(id)) {
            standard.setId(id);
        }
        return standard;
    }
}
