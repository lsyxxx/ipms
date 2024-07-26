package com.abt.common.model;

import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.EmployeeInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {

    /**
     * 用户唯一ID
     */
    private String id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 编号，比如工号
     */
    private String code;

    /**
     * 是否是管理人员
     */
    private boolean isManager = false;

    //-- 部门
    private String deptId;
    private String deptName;
    //-- 班组/科室
    private String teamId;
    private String teamName;

    //岗位
    private String position;

    //employee表唯一id
    private String employeeId;

    public User() {
        super();
    }

    public User(UserView user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.code = user.getAccount();
    }

    public User(EmployeeInfo user) {
        this.username = user.getName();
        this.id = user.getUserid();
        this.code = user.getJobNumber();
        this.deptId = user.getDept();
        if (user.getDepartment() != null) {
            this.deptName = user.getDepartment().getName();
        }
        this.position = user.getPosition();
        this.employeeId = user.getId();
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, String name) {
        this.id = id;
        this.username = name;
    }

    public User(String id, String name, String code) {
        this.id = id;
        this.username = name;
        this.code = code;
    }


}
