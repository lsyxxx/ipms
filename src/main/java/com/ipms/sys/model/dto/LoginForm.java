package com.ipms.sys.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录表单对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {

    private String username;
    private String password;
}
