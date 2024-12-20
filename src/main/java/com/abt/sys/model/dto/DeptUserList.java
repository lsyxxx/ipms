package com.abt.sys.model.dto;

import com.abt.common.model.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门-用户列表
 */
@Data
public class DeptUserList {

    private String deptId;
    private String deptName;

    /**
     * 该部门下的所有用户
     */
    private List<User> users = new ArrayList<>();

    public void addUser(String userid, String username, String jobNumber) {
        User u = new User();
        u.setId(userid);
        u.setUsername(username);
        u.setCode(jobNumber);
        if (users == null) {
            users = new ArrayList<>();
        }
        users.add(u);
    }
}
