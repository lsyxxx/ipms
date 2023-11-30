package com.abt.wf.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报销业务申请表单
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ReimburseApplyForm extends FlowForm{


    /**
     * 报销金额
     */
    @DecimalMin(value = "0.00", message = "Parameter: cost must be greater than 0.00")
    private double cost;

    /**
     * 报销时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime applyDate;

    /**
     * 报销事由
     */
    @NotBlank(message = "Parameter: reason is not blank")
    private String reason;

    /**
     * 票据数量
     */
    @Size(min = 0, max = 99,  message = "Parameter: voucherNum between 0 and 99")
    private int voucherNum;

    /**
     * 报销凭证列表
     * 名称
     */
    private List<String> vouchers;

    /**
     * 附件
     * 名称
     */
    private List<String> attachments;

}
