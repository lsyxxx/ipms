package com.abt.flow.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 报销流程申请表单
 */
@Data
@Accessors(chain = true)
public class ReimburseApplyForm extends Form{

    /**
     * 报销费用
     */
    private Double cost;

    /**
     * 报销事由
     */
    private String reason;

    /**
     * 票据数量
     */
    private int voucherNum;

    /**
     * 附件列表，保存文件路径
     */
    private List<String> attachments = new ArrayList<>();

    /**
     * 报销凭证文件列表，保存文件路径
     */
    private List<String> vouchers = new ArrayList<>();

    /**
     * 流程信息
     */
    private ProcessVo process;

    /**
     * 审批意见
     */
    private List<Approval> approvals = new ArrayList<>();

}
