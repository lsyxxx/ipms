package com.abt.flow.model;

import com.abt.flow.model.entity.Reimburse;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class ReimburseApplyForm extends FlowForm{
    public static final int SHOW_COMMENT = 1;

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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rbsDate;

    /**
     * 报销事由
     */
    private String reason;

    /**
     * 是否显示审批意见表单，页面控制
     * 1:显示
     */
    private int showComment = 0;

    public ReimburseApplyForm(double cost, int voucherNum, Date rbsDate, String reason) {
        super();
        this.cost = cost;
        this.voucherNum = voucherNum;
        this.rbsDate = rbsDate;
        this.reason = reason;
    }

    public ReimburseApplyForm(Reimburse reimburse) {
        super();
        this.cost = reimburse.getCost();
        this.reason = reimburse.getReason();
        this.rbsDate = reimburse.getReimburseDate();
        this.voucherNum = reimburse.getVoucherNum();
        this.decision = reimburse.getResult();

        this.setProcessInstanceId(reimburse.getProcessInstanceId());
        this.setProject(reimburse.getProject());
        this.setState(ProcessState.of(reimburse.getState()));
        this.setProcessDefId(reimburse.getProcessDefinitionId());
        this.setFrmId(reimburse.getFormId());

        FlowType flowType = new FlowType();
        flowType.setCode(reimburse.getCategoryCode());
        flowType.setId(reimburse.getCategoryId());
        flowType.setName(reimburse.getCategoryName());
        this.setFlowType(flowType);
    }

    /**
     * 显示审批意见表单
     */
    public void showComment() {
        this.showComment = 1;
    }

    /**
     * 不显示审批意见表单
     */
    public void disableComment() {
        this.showComment = 0;
    }


}
