package com.abt.testing.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.testing.converter.ReportCoverStringListConverter;
import com.abt.testing.model.ReportCoverStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** 报告封面基础信息，一条记录对应一个封面检测项目名称。 */
@Getter
@Setter
@Entity
@Table(name = "report_cover_info", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_report_cover_info_cover_name", columnNames = "cover_name")
})
@EntityListeners(CommonJpaAuditListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReportCoverInfo extends AuditInfo implements CommonJpaAudit {
    /** 封面配置主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    /** 封面中的检测项目名称，全表唯一。 */
    @Column(name = "cover_name", columnDefinition = "NVARCHAR(256)", nullable = false)
    private String coverName;

    /** 关联 T_checkModule.Id 的列表，一个系统项目只归属一条配置。 */
    @Convert(converter = ReportCoverStringListConverter.class)
    @Column(name = "check_module_ids", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private List<String> checkModuleIds = new ArrayList<>();

    /** 项目代码，允许重复。 */
    @Column(name = "item_code", columnDefinition = "NVARCHAR(64)")
    private String itemCode;

    /** 样品规格，包含原始文字和单位。 */
    @Column(name = "sample_spec", columnDefinition = "NVARCHAR(500)")
    private String sampleSpec;

    /** 样品状态，如粉末、块状。 */
    @Column(name = "sample_status", columnDefinition = "NVARCHAR(200)")
    private String sampleStatus;

    /** 检测设备完整文本列表，包含型号和说明。 */
    @Convert(converter = ReportCoverStringListConverter.class)
    @Column(name = "instruments", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private List<String> instruments = new ArrayList<>();

    /** 检测依据完整文本列表，包含标准号和说明。 */
    @Convert(converter = ReportCoverStringListConverter.class)
    @Column(name = "standards", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private List<String> standards = new ArrayList<>();

    /** 业务分类，如化学、岩心。 */
    @Column(name = "category", columnDefinition = "NVARCHAR(64)")
    private String category;

    /** 业务版本，如 2026，不是乐观锁版本。 */
    @Column(name = "version", columnDefinition = "NVARCHAR(32)")
    private String version;

    /** 配置归属，如 ABT。 */
    @Column(name = "ownership", columnDefinition = "NVARCHAR(128)")
    private String ownership;

    /** 备注。 */
    @Column(name = "remark", columnDefinition = "NVARCHAR(1000)")
    private String remark;

    /** 启用状态：0 禁用，1 启用。 */
    @Column(name = "status", nullable = false)
    private Integer status = ReportCoverStatus.ENABLED.getCode();
}
