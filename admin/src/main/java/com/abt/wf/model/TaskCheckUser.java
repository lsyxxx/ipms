package com.abt.wf.model;

import com.abt.common.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 审批人
 */
@Getter
@Setter
public class TaskCheckUser {
    private List<User> users;
    private String taskDefKey;

}
