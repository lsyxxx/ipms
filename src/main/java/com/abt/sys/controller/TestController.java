package com.abt.sys.controller;

import com.abt.flow.model.ApplyForm;
import com.abt.flow.model.entity.FlowScheme;
import com.abt.flow.model.entity.Reimburse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 仅用来测试，正式需要删除
 */
@RestController
@Slf4j
@RequestMapping("/test")
@Tag(name = "TestController", description = "仅用来测试，正式需要删除")
public class TestController {

    @PostMapping("/get")
    public void get(@Validated @RequestBody ApplyForm<Reimburse> applyForm) {
        log.info("applyForm -- {}", applyForm);
    }
}
