package com.abt.wf.config;

/**
 * chang
 */
public class Constants {

    /**
     * 审批中
     */
    public static final int STATE_APPROVING = 0;
    /**
     * 审批通过，可以是流程完成全部审批通过，也可以是流程未完成
     * isFinished = true, 表示该流程审批已通过。
     * isFinished = false, 表示该流程未完成，最近审批人通过
     *
     */
    public static final int STATE_PASS = 1;
    /**
     * 审批拒绝(流程已完成)
     */
    public static final int STATE_REJECT = 2;
    /**
     * 撤销
     */
    public static final int STATE_CANCEL = 3;
    /**
     * 用户删除
     */
    public static final int STATE_DELETE = 4;

    /**
     * 暂存
     */
    public static final int STATE_TEMP = 99;

    public static final String DELETE_REASON_REJECT = "用户审批拒绝";
    public static final String DELETE_REASON_REVOKE = "用户撤销流程";
    public static final String DELETE_REASON_AUTO = "流程正常结束";
    public static final String DELETE_REASON_DELETE = "用户手动删除";

    /**
     * 流程结束者-系统
     */
    public static final String TERMINATE_SYS = "system";

    public static final String REJECT = "reject";
    public static final String PASS = "pass";

    /*-----------------------
     * 审批状态。参考钉钉
     * ----------------------
     */
    public static final String STATE_DESC_APPROVING = "审批中";
    public static final String STATE_DESC_FINISHED = "已结束";
    public static final String STATE_DESC_TERMINATED = "终止";

    /*-----------------------
     * 审批详情(Task)中的状态。参考钉钉
     * ----------------------
     */
    public static final String STATE_DESC_REJECT = "已拒绝";
    public static final String STATE_DESC_PASS = "已通过";
    public static final String STATE_DESC_CANCEL = "已撤销";

}
