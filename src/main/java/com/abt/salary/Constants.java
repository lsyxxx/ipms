package com.abt.salary;

/**
 *
 */
public class Constants {

    public static final String S_UUID_KEY = "Salary_Session_UUID";

    /**
     * 工资临时数据SESSION key
     */
    public static final String S_SL_UUID = "SalaryUUID";
    public static final String S_SL_MAIN = "SalaryMain";
    public static final String S_SL_PREVIEW = "SalaryPreview";
    public static final String S_SL_FILE = "SalaryFile";

    public static final String NETPAID_COLNAME = "本月实发工资";
    public static final String JOBNUMBER_COLNAME = "工号";
    public static final String NAME_COLNAME = "姓名";

    /**
     * 表示不存在行/列
     */
    public static final int EXCLUDE_IDX = -1;


    public static final String ERR_JOBNUM_NULL = "工号数据缺失";
    public static final String ERR_JOBNUM_NOT_EXIST = "工号不存在";
    public static final String ERR_JOBNUM_DUPLICATED = "工号重复";
    public static final String ERR_USERNAME_NULL = "姓名数据缺失";
    public static final String ERR_NETPAID_NULL = "本月实发工资数据缺失";
    public static final String ERR_USER_NOT_FIT = "工号和姓名不一致";
    public static final String ERR_USER_EXIT = "存在离职员工";

    public static final int SL_ROW_INFO_IDX = 0;
}


