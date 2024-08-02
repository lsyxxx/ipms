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

    /**
     * 考勤月起始时间id
     */
    public static String attendanceStartDayId = "attendance_start";
    /**
     * 考勤月结束时间id
     */
    public static String attendanceEndDayId = "attendance_end";

}
