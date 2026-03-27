package com.abt.testing.controller;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.service.CheckModuleService;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 检测项目相关
 */
@RestController
@Slf4j
@RequestMapping("/chk/module")
public class CheckModuleController {

    private final CheckModuleService checkModuleService;

    public CheckModuleController(CheckModuleService checkModuleService) {
        this.checkModuleService = checkModuleService;
    }


    /**
     * 校验名称是否重复
     * @param name 检测项目名称
     */
    @GetMapping("/validate/name")
    public R<Boolean> validateDuplicatedName(String name) {
        boolean isValid = checkModuleService.isDuplicatedName(name);
        return R.success(isValid);
    }


    /**
     * 暂存一个检测检测项目
     * 只用输入名称
     */
    @PostMapping("/draft")
    public R<Object> draft(@Validated(ValidateGroup.Temp.class) @RequestBody CheckModule checkModule) {
        checkModuleService.draft(checkModule);
        return R.success("暂存成功");
    }

    public void publishNew() {

    }
}
