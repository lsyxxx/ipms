package com.abt.sys.model.dto;

import com.abt.common.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 用户角色
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserRole extends User {

    private String roleId;
    private String roleName;
    private boolean status;
}
