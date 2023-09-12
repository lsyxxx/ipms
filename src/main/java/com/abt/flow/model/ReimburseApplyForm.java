package com.abt.flow.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 报销流程申请表单
 * 同时用来前后传递数据
 */
@Data
@Accessors(chain = true)
public class ReimburseApplyForm extends Form{

    /**
     * 报销费用
     */
    private Double cost = 0.00;

    /**
     * 票据数量
     * 最大上传数量99
     */
    @Max(value = 99, message = "${flow.service.ReimburseApplyForm.voucherNum.max}")
    @Min(value = 0, message = "${flow.service.ReimburseApplyForm.voucherNum.min}")
    private int voucherNum = 0;

    /**
     * 附件列表，保存文件路径
     */
    private List<String> attachments = new ArrayList<>();

    /**
     * 报销凭证文件列表，保存文件路径
     */
    private List<String> vouchers = new ArrayList<>();

    /**
     * 审批意见
     */
    private List<Approval> approvals = new ArrayList<>();

    /**
     * 报销日期
     */
    private LocalDateTime dateTime;

    /**
     * 当前节点决策
     */
    private String decision;

    /**
     * 当前节点评论
     */
    private String comment;


}
