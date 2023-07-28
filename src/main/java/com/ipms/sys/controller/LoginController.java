package com.ipms.sys.controller;

import com.ipms.sys.model.dto.LoginForm;
import com.ipms.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {


//    @GetMapping("/toLogin")
//    public R<String> toLogin() {
//
//        return R.success("to login");
//    }

    /**
     * login process url
     * @param form
     * @return
     */
    @PostMapping("/doLogin")
    public R<String> doLogin(@RequestBody LoginForm form) {
        log.info("Login processing....");

        return R.success("Login Success!");
    }
}
