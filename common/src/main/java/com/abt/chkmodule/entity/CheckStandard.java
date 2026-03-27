package com.abt.chkmodule.entity;

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
public class CheckStandard {
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
    @Column(name = "code_", nullable = false)
    private String code;

    @NotNull
    @Column(name = "name_", nullable = false)
    private String name;

    /**
     * 备注
     */
    @Column(name = "note_", length = 512)
    private String note;

    /**
     * 标准状态，现行/废弃
     */
    @Column(name = "status_", length = 16, nullable = false)
    private String status;

    /**
     * 标准等级：国家/行业/地方/企业/团体/国外
     * TODO: 使用category保存字典
     */
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
    @Convert(converter = SystemFileListConverter.class)
    @Column(name="file_path", length = 1024)
    private List<SystemFile> filePath;

}