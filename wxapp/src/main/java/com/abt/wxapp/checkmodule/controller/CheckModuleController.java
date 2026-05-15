package com.abt.wxapp.checkmodule.controller;

import com.abt.wxapp.checkmodule.model.CheckModuleDetailVo;
import com.abt.wxapp.checkmodule.model.ProjectHomeItemVo;
import com.abt.wxapp.checkmodule.model.ProjectListItemVo;
import com.abt.wxapp.checkmodule.service.CheckModuleWxService;
import com.abt.wxapp.common.model.R;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序检测项目相关，实现方法
 *
 */
@RestController
@RequestMapping("/checkmodule")
@RequiredArgsConstructor
@Validated
public class CheckModuleController {

    private final CheckModuleWxService checkModuleWxService;



}
