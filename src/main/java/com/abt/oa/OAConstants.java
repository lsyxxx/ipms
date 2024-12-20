package com.abt.oa;

/**
 *
 */
public class OAConstants {

    /**
     * 行政通知状态：草稿
     */
    public static final int ANNOUNCEMENT_STATUS_TEMP = 0;
    /**
     * 行政通知状态：发布
     */
    public static final int ANNOUNCEMENT_STATUS_PUBLISH = 1;

    public static final String ANNOUNCEMENT_ZDTYPE_ALL = "1";
    public static final String ANNOUNCEMENT_ZDTYPE_SPEC = "2";


    public static final String ANNOUNCEMENT_ATTACHMENT_UNREAD = "1";
    public static final String ANNOUNCEMENT_ATTACHMENT_READ = "2";

    public static final String ANNOUNCEMENT_ATTACHMENT_UNHF = "1";
    public static final String ANNOUNCEMENT_ATTACHMENT_HF = "2";

    public static final String ANNOUNCEMENT_REQUIRE_HF = "1";
    public static final String ANNOUNCEMENT_UNREQUIRE_HF = "2";

    public static final String ANNOUNCEMENT_FILETYPE_RULES = "5";

    /**
     * 待我审批
     */
    public static final String QUERY_MODE_TODO = "todo";
    /**
     * 我已审批
     */
    public static final String QUERY_MODE_DONE = "done";
    /**
     * 我提交的
     */
    public static final String QUERY_MODE_APPLY = "apply";
    /**
     * 所有
     */
    public static final String QUERY_MODE_ALL = "all";

    public static final String FW_REJECT = "拒绝";
    public static final String FW_PASS = "通过";
    public static final String FW_WAITING = "待审批";
    public static final String FW_WITHDRAW = "已撤销";

    /**
     * 考勤月起始时间id
     */
    public static String attendanceStartDayId = "attendance_start";
    /**
     * 考勤月结束时间id
     */
    public static String attendanceEndDayId = "attendance_end";

    /**
     * open auth 流程状态。草稿: -1
     */
    public static final String OPENAUTH_FLOW_STATE_DRAFT = "-1";
    /**
     * open auth 流程状态。完成(通过): 1
     */
    public static final String OPENAUTH_FLOW_STATE_FINISH = "1";
    /**
     * open auth 流程状态。正在运行: 0
     */
    public static final String OPENAUTH_FLOW_STATE_RUN = "0";
    /**
     * open auth 流程状态。没有通过: 3
     */
    public static final String OPENAUTH_FLOW_STATE_UNPASS = "3";
    /**
     * open auth 流程状态。驳回: 4
     */
    public static final String OPENAUTH_FLOW_STATE_REJECT = "4";


    /**
     * 野外作业部门-工程技术部(延安)
     */
    public static final String FIELD_DEPT_Y = "c3626282-a499-4354-8330-b49fff6887b9";
    /**
     * 野外作业部门-西南项目部(成都  )
     */
    public static final String FIELD_DEPT_C = "e5b9f524-485b-496f-9786-8a92a1a9ad2c";


    public static final String COMPANY_A = "A";
    public static final String COMPANY_G = "G";
    public static final String COMPANY_D = "D";
}
