package com.ipms.sys.controller;

import com.ipms.sys.model.dto.LoginForm;
import com.ipms.common.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "LoginController", description = "登录")
public class LoginController {


    /**
     * login process url
     * @param form
     * @return
     */
    @Operation(summary = "登录api")
    @Parameter(name = "LoginForm", description = "登录表单")
    @PostMapping("/doLogin")
    public R<String> doLogin(@RequestBody LoginForm form) {
        log.info("Login processing....");

        return R.success("Login Success!");
    }
}
