package com.ipms.sys.controller;

import com.ipms.common.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "HomeController", description = "首页")
public class HomeController {



    @Operation(summary = "错误统一返回")
    @GetMapping("/error")
    public R<String> error() {
        log.info("Something wrong...");
        //TODO 具体错误？
        return R.fail("Something wrong...");
    }

    @Operation(summary = "Session超时", description = "session超时desc")
    @GetMapping("/invalidSession")
    public R invalidSession() {
        log.info("Session out of time!");
        return R.invalidSession();
    }

}
