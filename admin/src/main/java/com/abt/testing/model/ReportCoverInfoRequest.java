package com.abt.testing.model;

import com.abt.common.config.ValidateGroup;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** 报告封面新增、更新请求，不接收客户端审计信息。 */
@Getter
@Setter
public class ReportCoverInfoRequest {
    @Null(message = "新增时不能指定ID", groups = ValidateGroup.Insert.class)
    @NotBlank(message = "封面配置ID不能为空", groups = ValidateGroup.Update.class)
    @Size(max = 36, message = "封面配置ID不能超过36个字符")
    private String id;

    @NotBlank(message = "封面检测项目名称不能为空")
    @Size(max = 256, message = "封面检测项目名称不能超过256个字符")
    private String coverName;

    private List<@NotBlank(message = "关联检测项目ID不能为空")
            @Size(max = 50, message = "关联检测项目ID不能超过50个字符") String> checkModuleIds;

    @Size(max = 64, message = "项目代码不能超过64个字符")
    private String itemCode;
    @Size(max = 500, message = "样品规格不能超过500个字符")
    private String sampleSpec;
    @Size(max = 200, message = "样品状态不能超过200个字符")
    private String sampleStatus;

    private List<@NotBlank(message = "检测设备内容不能为空") String> instruments;
    private List<@NotBlank(message = "检测依据内容不能为空") String> standards;

    @Size(max = 64, message = "业务分类不能超过64个字符")
    private String category;
    @Size(max = 32, message = "版本不能超过32个字符")
    private String version;
    @Size(max = 128, message = "归属不能超过128个字符")
    private String ownership;
    @Size(max = 1000, message = "备注不能超过1000个字符")
    private String remark;
    @Min(value = 0, message = "状态只能为0禁用或1启用")
    @Max(value = 1, message = "状态只能为0禁用或1启用")
    private Integer status = ReportCoverStatus.ENABLED.getCode();
}
