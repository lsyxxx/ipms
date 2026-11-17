package com.abt.testing.model;

import com.abt.common.model.RequestForm;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 报告封面查询条件，page 从 1 开始，limit 为 0 时分页接口默认每页 20 条。 */
@Getter
@Setter
public class ReportCoverInfoRequestForm extends RequestForm {
    @Size(max = 256, message = "封面名称查询条件不能超过256个字符")
    private String coverName;
    @Size(max = 64, message = "项目代码查询条件不能超过64个字符")
    private String itemCode;
    @Size(max = 64, message = "业务分类查询条件不能超过64个字符")
    private String category;
    @Size(max = 32, message = "版本查询条件不能超过32个字符")
    private String version;
    @Size(max = 128, message = "归属查询条件不能超过128个字符")
    private String ownership;
    @Min(value = 0, message = "状态只能为0禁用或1启用")
    @Max(value = 1, message = "状态只能为0禁用或1启用")
    private Integer status;

    @Override
    @Min(value = 1, message = "页码必须大于等于1")
    public int getPage() {
        return super.getPage();
    }

    @Override
    @Min(value = 0, message = "每页数量不能小于0")
    @Max(value = PAGE_LIMIT, message = "每页数量不能超过999")
    public int getLimit() {
        return super.getLimit();
    }
}
