package com.abt.wf.config;

/**
 * chang
 */
public class Constants {

    public static final String VAR_KEY_ENTITY = "entityId";

    /*-----------------------
     * 流程删除原因
     * -----------------------
     */
    public static final String DELETE_REASON_REJECT = "用户审批拒绝";
    public static final String DELETE_REASON_REVOKE = "用户撤销流程";
    public static final String DELETE_REASON_AUTO = "流程正常结束";
    public static final String DELETE_REASON_DELETE = "用户手动删除";

    /**
     * 流程结束者-系统
     */
    public static final String TERMINATE_SYS = "system";

    public static final String DECISION_REJECT = "reject";
    public static final String DECISION_REJECT_DESC = "已拒绝";
    public static final String DECISION_PASS = "pass";
    public static final String DECISION_PASS_DESC = "已通过";


    /*-----------------------
     * 审批详情中的状态。参考钉钉
     * ----------------------
     */
    public static final String STATE_DETAIL_ALL = "全部";
    public static final String STATE_DETAIL_REJECT = "已拒绝";
    public static final String STATE_DETAIL_PASS = "已通过";
    public static final String STATE_DETAIL_REVOKE = "已撤销";
    public static final String STATE_DETAIL_DELETE = "已删除";

    public static final String STATE_DETAIL_ACTIVE = "审批中";
    public static final String STATE_DETAIL_APPLY = "申请";

    public static final String OPERATOR_SYS = "system";
    public static final String OPERATE_TYPE_SYS = "system";
    public static final String OPERATE_TYPE_USER = "user";

    /*-----------------------
     * 流程节点
     * ----------------------
     */

    /**
     * 审批节点类型：顺序依次审批
     */
    public static final int APPROVAL_TYPE_SEQ = 0;

    //--- taskType
    /**
     * 申请节点
     */
    public static final String TASK_TYPE_APPLY = "apply";
    /**
     * 审批节点
     */
    public static final String TASK_TYPE_APPROVAL = "approval";
    /**
     * 抄送节点
     */
    public static final String TASK_TYPE_COPY = "copy";
    public static final String TASK_NAME_COPY = "抄送";

    //--- selectUserType
    /**
     * 选择用户方式-用户自选
     */
    public static final int SELECT_USER_TYPE_MANUAL = 0;
    /**
     * 选择用户方式-指定用户
     */
    public static final int SELECT_USER_TYPE_SPECIFIC = 1;

    public static final String SERVICE_RBS = "日常报销";
    public static final String SERVICE_INV = "开票申请";
    public static final String SERVICE_TRIP = "差旅报销";

    public static final String TRANSPORTATION_AIRPLANE = "飞机";
    public static final String TRANSPORTATION_CAR = "自驾";
    public static final String TRANSPORTATION_TRAIN = "火车";
    public static final String TRANSPORTATION_HIGHSPEED = "高铁";
    public static final String TRANSPORTATION_OTHER = "其他";



}
