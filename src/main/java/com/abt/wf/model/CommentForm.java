package com.abt.wf.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 审批节点
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CommentForm extends FlowForm {
    public static final int APPROVAL = 1;
    public static final int REJECT = 0;

    /**
     * 用户评论
     */
    private String comment;

    /**
     * 审批结果
     * 0: 不通过
     * 1: 同意
     */
    private int decision = 0;


    /**
     * 当前审批用户
     */
    private String userId;

    /**
     * 审批通过
     */
    public boolean isApproval() {
        return decision == APPROVAL;
    }
    public boolean isReject() {
        return decision == REJECT;
    }

}
