package com.abt.wxapp.checkmodule.controller;

import com.abt.wxapp.checkmodule.model.DynamicSchemeVo;
import com.abt.wxapp.checkmodule.service.CheckModuleWxService;
import com.abt.wxapp.common.model.R;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 动态表单 Schema
 */
@RestController
@RequestMapping("/check-module")
@RequiredArgsConstructor
@Validated
public class WxCheckModuleController {

    private final CheckModuleWxService checkModuleWxService;

    @GetMapping("/find-form-schema")
    public R<DynamicSchemeVo> findFormSchemaById(@RequestParam("formSchemaId") @NotNull(message = "表单配置ID不能为空") Long formSchemaId) {
        return R.success(checkModuleWxService.findFormSchemaById(formSchemaId));
    }
}
