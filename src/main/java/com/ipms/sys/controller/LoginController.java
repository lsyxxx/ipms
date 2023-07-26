package com.ipms.sys.controller;

import com.ipms.sys.model.LoginForm;
import com.ipms.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private final AuthenticationProvider authenticationProvider;

    public LoginController(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @GetMapping("/toLogin")
    public R<String> toLogin() {

        return R.success("to login");
    }

    @PostMapping("/doLogin")
    public R<String> doLogin(@RequestBody LoginForm form) {
        log.info("Login processing....");
        //test
        if (form == null) {
            form = new LoginForm("user", "22222");
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword());
        Authentication auth = authenticationProvider.authenticate(token);
        if (auth == null) {
            log.error("User authentication fail! username - {}", form.getUsername());
        } else {
            log.info("User authentication success!");
        }

        return R.success("Login Success!");
    }
}
