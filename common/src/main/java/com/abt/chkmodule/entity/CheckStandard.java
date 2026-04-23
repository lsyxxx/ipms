package com.abt.chkmodule.entity;

import com.abt.chkmodule.model.StandardStatus;
import com.abt.common.AuditInfo;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.SaveMode;
import com.abt.sys.SystemFileConverter;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;


/**
 * 检测标准资料库
 */
@Getter
@Setter
@Entity
@Table(name = "check_std")
public class CheckStandard extends AuditInfo {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 行业分类
     */
    @Size(max = 64)
    @Column(name = "type_", length = 64)
    private String type;

    /**
     * 标准号
     */
    @NotNull(message = "标准号不能为空", groups = {ValidateGroup.Save.class, ValidateGroup.Temp.class})
    @Column(name = "code_", nullable = false)
    private String code;

    /**
     * 标准名称
     */
    @NotNull(message = "标准名称不能为空", groups = {ValidateGroup.Save.class, ValidateGroup.Temp.class})
    @Column(name = "name_", nullable = false)
    private String name;

    /**
     * 备注
     */
    @Column(name = "note_", length = 512)
    private String note;

    /**
     * 标准状态
     */
    @NotNull(message = "标准状态不能为空", groups = {ValidateGroup.Save.class, ValidateGroup.Temp.class})
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status_", nullable = false, columnDefinition = "tinyint")
    private StandardStatus status;

    /**
     * 标准等级：国家/行业/地方/企业/团体/国外
     * TODO: 使用category保存字典
     */
    @NotNull(message = "标准等级不能为空", groups = {ValidateGroup.Save.class})
    @Column(name = "level_", length = 16)
    private String level;

    /**
     * 发布时间
     */
    @Column(name="publish_date")
    private LocalDate publishDate;

    /**
     * 实施时间
     */
    @Column(name="effective_date")
    private LocalDate effectiveDate;

    /**
     * 标准文件地址
     */
    @Convert(converter = SystemFileConverter.class)
    @Column(name="file_path", length = 1024)
    private SystemFile filePath;

    /**
     * 0：暂存, 1: 保存
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "save_mode")
    private SaveMode saveMode = SaveMode.TEMP;


}