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
 * 小程序检测项目列表与详情
 */
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final CheckModuleWxService checkModuleWxService;

    @GetMapping("/find-list")
    public R<List<ProjectListItemVo>> findList() {
        return R.success(checkModuleWxService.findList());
    }

    @GetMapping("/find-by-id/{id}")
    public R<CheckModuleDetailVo> findById(@PathVariable("id") @NotBlank(message = "检测项目ID不能为空") String id) {
        return R.success(checkModuleWxService.findById(id));
    }

    /**
     * 首页简化项目
     */
    @GetMapping("/find-home-list")
    public R<List<ProjectHomeItemVo>> findHomeList() {
        return R.success(checkModuleWxService.findHomeList());
    }
}
