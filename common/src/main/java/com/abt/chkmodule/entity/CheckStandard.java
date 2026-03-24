package com.abt.chkmodule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;


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

    @Size(max = 64)
    @Column(name = "type", length = 64)
    private String type;

    /**
     * 标准号
     */
    @Column(name = "code", length = 512, nullable = false)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 备注
     */
    @Column(name = "note", length = 512)
    private String note;

    /**
     * 标准状态，现行/废弃
     * TODO: 使用category保存字典
     */
    @Column(name = "status", length = 16, nullable = false)
    private String status;

    /**
     * 标准等级：国家/行业/地方/企业/团体/国外
     * TODO: 使用category保存字典
     */
    @Column(name = "level", length = 16, nullable = false)
    private String level;

    /**
     * 限制范围说明
     */
    @Column(name="restrict")
    private String restrict;

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


}