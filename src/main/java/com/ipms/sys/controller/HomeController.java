package com.ipms.sys.controller;

import com.ipms.sys.model.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理首页
 */
@RestController
@Slf4j
public class HomeController {

    @GetMapping("/home")
    public R<String> home() {
        return R.success("HomePage");
    }

    @GetMapping("/login")
    public R<String> login() {
        log.info("Login api");
        return R.success("Login");
    }

    @GetMapping("/succLogin")
    public R<String> success() {
        log.info("Login success");
        return R.success("Login success!");
    }

    @GetMapping("/failLogin")
    public R<String> fail() {
        log.info("Login failed");
        return R.fail("Login failed");
    }

    @GetMapping("/error")
    public R error(HttpServletRequest request, HttpServletResponse response) {
        log.info("Something wrong...");
        //TODO 具体错误？
        return R.fail("Unknown error");
    }

    @GetMapping("/invalidSession")
    public R invalidSession() {
        log.info("Session out of time!");
        return R.invalidSession();
    }


    @GetMapping("/test")
    public R<String> test() {
        log.info("test!");
        return R.success("Test");
    }
}
