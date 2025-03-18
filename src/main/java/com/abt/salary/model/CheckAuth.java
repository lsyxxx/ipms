package com.abt.salary.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户审批工资权限
 */
@Data
public class CheckAuth {
    /**
     * 审批角色
     */
    private String role = SL_CHK_USER;
    /**
     * 查看权限。默认查看自己的
     */
    private String viewAuth = SL_CHK_USER;
    private List<String> deptIds;
    private String jobNumber;
    private String name;
    private String position;

    public void addDeptId(String deptId) {
        if (this.deptIds == null) {
            this.deptIds = new ArrayList<>();
        }
        this.deptIds.add(deptId);
    }

    /**
     * 清除原来的并添加
     */
    public void clearAndSetAll(List<String> deptIds) {
        if (this.deptIds == null) {
            this.deptIds = new ArrayList<>();
        }
        this.deptIds.clear();
        this.deptIds.addAll(deptIds);
    }

    public void clearDept() {
        this.deptIds = null;
    }


    //所有
    public static final String SL_CHK_ALL = "all";
    //副总查看
    public static final String SL_CHK_DCEO = "dceo";
    //部门经理
    public static final String SL_CHK_DM = "dm";
    //仅看个人
    public static final String SL_CHK_USER = "user";

    public static final String SL_CHK_HR = "hr";

    public static final String SL_CHK_CEO = "ceo";
}
