package com.abt.flow.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报销流程申请表单, 报销流程业务数据
 * client流程申请数据表单
 */
@Data
@Accessors(chain = true)
public class ReimburseApplyForm extends FlowForm{

    /**
     * 报销费用
     */
    private double cost;

    /**
     * 票据数量
     * 最大上传数量99
     */
    @Max(value = 99, message = "${flow.service.ReimburseApplyForm.voucherNum.max}")
    @Min(value = 0, message = "${flow.service.ReimburseApplyForm.voucherNum.min}")
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
     * 当前节点决策
     */
    private String decision;

    /**
     * 当前节点评论
     */
    private String comment;

    /**
     * 相关项目
     */
    private String project;

    /**
     * 报销时间
     */
    private Date rbsDate;

    /**
     * 报销事由
     */
    private String reason;

    public ReimburseApplyForm(double cost, int voucherNum, Date rbsDate, String reason) {
        this.cost = cost;
        this.voucherNum = voucherNum;
        this.rbsDate = rbsDate;
        this.reason = getReason();
    }

}
