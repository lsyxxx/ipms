package com.abt.wf.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程设置
 */
@RestController
@Slf4j
@RequestMapping("/wf/set")
public class SettingController {


    /**
     * 流程参数设置
     */
    @PostMapping("/config")
    public void set() {

    }

    /**
     *
     */
    public void load() {

    }
}
