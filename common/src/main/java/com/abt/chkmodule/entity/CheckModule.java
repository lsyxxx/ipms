package com.abt.chkmodule.entity;

import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.model.SimpleCheckModule;
import com.abt.common.AuditInfo;
import com.abt.chkmodule.converter.ListStringConverter;
import com.abt.common.config.ValidateGroup;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * 检测项目
 */
@Getter
@Setter
@Entity
@Table(name = "check_module")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CheckModule extends AuditInfo implements UseChannel {

    @Id
    @Size(max = 128)
    @Column(name = "id", nullable = false, length = 128)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 代码编号
     */
    @Size(max = 32)
    @NotNull(message = "请输入检测项目编码", groups = {ValidateGroup.Save.class})
    @Column(name = "code_", nullable = false, length = 32)
    private String code;

    /**
     * 名称
     */
    @Size(max = 128)
    @NotNull(message = "请输入检测项目名称", groups =  {ValidateGroup.Save.class, ValidateGroup.Temp.class})
    @Column(name = "name_", nullable = false, length = 128)
    private String name;

    /**
     * 分类id
     */
    @Size(max = 128)
    @Column(name = "cu_id", length = 128, nullable = false)
    private String checkUnitId;

    /**
     * 备注
     */
    @Size(max = 500)
    @Column(name = "note_", length = 500)
    private String note;

    /**
     * 是否启用。默认启用
     */
    @Column(name="enabled_", columnDefinition = "BIT")
    private boolean enabled = true;

    /**
     * 其他常用名称，可以多个，用逗号分隔
     */
    @Size(max = 512)
    @Column(name = "alias_name", length = 512)
    private String aliasNames;

    /**
     * 预约须知
     * TODO: 富文本
     */
    @Size(max = 1000)
    @Column(name="intro_", length = 1024)
    private String notice;

    /**
     * 使用渠道
     */
    @NotNull(message = "请输入检测项目使用渠道", groups = {ValidateGroup.Save.class})
    @Enumerated(EnumType.STRING)
    @Column(name="use_chn", length = 16, nullable = false)
    private ChannelEnum useChannel;

    /**
     * 封面图片url
     */
    @Column(name="cover_img")
    private String coverImage;

    /**
     * 一般工作时间
     */
    @Column(name="duration_")
    private String duration;

    /**
     * 结果说明
     * TODO: 富文本
     */
    @Column(name="result_desc", length = 1024)
    private String resultDescription;

    /**
     * 结果展示附图url列表
     * 可多图，可无
     */
    @Column(name="result_img", length = 1024)
    @Convert(converter = SystemFileListConverter.class)
    private List<SystemFile> resultImages;

    /**
     * 资质，可多个。用逗号分隔
     * TODO: 资质一般对应的是子参数(checkItem)，目前暂定：如果子参数中有CMA/CNAS认证的，则这里表示有资质
     */
    @Transient
    private List<String> certificateList;

    /**
     * 单价，可以非数字，如面议
     */
    @Column(name="price",  length = 32)
    private String price;

    /**
     * 状态：草稿0，已发布1
     */
    @Column(name="status_", columnDefinition = "TINYINT")
    private int status = STATUS_TEMP;

    public static final int STATUS_TEMP = 0;
    public static final int STATUS_PUBLISHED = 1;

    public static final String CERTIFICATE_CMA = "CMA";

    public static final String CERTIFICATE_CNAS = "CNAS";

    /**
     * 设置为草稿，不启用
     */
    public void setStatusTemp() {
        this.status = STATUS_TEMP;
        this.enabled = false;
    }

    /**
     * 设置为正式发布状态
     */
    public void setPublished() {
        this.status = STATUS_PUBLISHED;
        this.enabled = true;
    }

    public void addCma() {
        if (this.certificateList == null) {
            this.certificateList = new ArrayList<>();
        }
        this.certificateList.add(CERTIFICATE_CMA);
    }

    public void addCnas() {
        if (this.certificateList == null) {
            this.certificateList = new ArrayList<>();
        }
        this.certificateList.add(CERTIFICATE_CNAS);
    }

    /**
     * 是否有CMA资质
     */
    public boolean isCma() {
        for (String c : this.certificateList) {
            if (CERTIFICATE_CMA.equals(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有CNAS资质
     */
    public boolean isCnas() {
        for (String c : this.certificateList) {
            if (CERTIFICATE_CNAS.equals(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 相关的检测项目，仅id
     */
    @Column(name="rel_cm", columnDefinition = "VARCHAR(MAX)")
    @Convert(converter = ListStringConverter.class)
    private List<String> relatedCheckModuleIds;


    /**
     * 关联检测项目对象，仅返回简单数据
     */
    @Transient
    private List<SimpleCheckModule> relatedCheckModuleList;

    /**
     * 检测关联仪器
     */
    @Transient
    private List<Instrument> instruments;

    /**
     * 关联检测仪器
     */
    @Transient
    private List<CheckModuleInstrumentRel> instrumentRels;

    /**
     * 关联子参数
     */
    @Transient
    private List<CheckItem> checkItems;





    @Override
    public ChannelEnum getChannel() {
        return this.useChannel;
    }
}