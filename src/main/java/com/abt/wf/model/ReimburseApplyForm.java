package com.abt.wf.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报销业务申请表单
 */
@Data
public class ReimburseApplyForm {

    /**
     * 流程定义id
     */
    private String processDefId;

    /**
     * 报销金额
     */
    private double cost;

    /**
     * 报销时间
     */
    private LocalDateTime applyDate;

    /**
     * 报销事由
     */
    private String reason;

    /**
     * 票据数量
     */
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
