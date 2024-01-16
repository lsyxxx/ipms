package com.abt.chemicals.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/test/chm/base")
public class BasicDataController {

    /**
     * 查询分类
     */
    @GetMapping("/type/search")
    public void queryType(@RequestParam String word, @RequestParam int level) {

    }

}
