package com.abt.wf.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 采购单据导出条件；分页参数不影响导出范围。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseDocumentExportRequest extends PurchaseApplyRequestForm {
    /** MY：我创建的，TODO：待我处理，DONE：我已处理，ALL：全部采购。 */
    @NotBlank(message = "请选择采购单据导出范围")
    @Pattern(regexp = "MY|TODO|DONE|ALL", message = "采购单据导出范围不正确")
    private String scope;
}
