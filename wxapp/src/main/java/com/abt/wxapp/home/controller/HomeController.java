package com.abt.wxapp.home.controller;

import com.abt.wxapp.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 */
@RestController
@Slf4j
@RequestMapping("/home")
public class HomeController {

    @GetMapping("/hello")
    public R<Object> hello() {
        return R.success("hello");
    }




}
