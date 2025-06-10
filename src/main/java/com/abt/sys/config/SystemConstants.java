package com.abt.sys.config;

/**
 *
 */
public class SystemConstants {

    public static final String SYSTEM_USER_ID = "00000000-0000-0000-0000-00000000000";
    public static final String SYSTEM_USER_NAME = "System";

    /**
     * 抄送信息类型
     */
    public static final String SYSMSG_TYPE_ID_COPY = "COPY_MSG";
    public static final String SYSMSG_TYPE_NAME_COPY = "抄送";

    public static final String SYSMSG_TYPE_ID_TIP = "TIP_MSG";
    public static final String SYSMSG_TYPE_NAME_TIP = "提醒";

    //重要
    public static final String SYSMSG_TYPE_IMPORTANT = "IMPORTANT_MSG";
    public static final String SYSMSG_TYPE_NAME_IMPORTANT = "重要";
    public static final String SYSMSG_RESULT_IMPORTANT = "important";

    public static final String SYSMSG_TYPE_ID_SL_CHK = "SL_CHK_MSG";



    public static final Integer STATUS_DEL = 9;
    public static final Integer STATUS_UNREAD = 0;
    public static final Integer STATUS_READ = 1;

    public static final Integer FROM_STATUS_DEL = -1;
    public static final Integer FROM_STATUS_DEFAULT = 0;

}
