package com.abt.flow.controller;

import com.abt.common.model.R;
import com.abt.flow.model.entity.FlowSetting;
import com.abt.flow.service.FlowSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程参数设置
 */
@RestController
@Slf4j
@RequestMapping("/wf/setting")
@Tag(name = "FlowSettingController", description = "流程参数设置")
public class FlowSettingController {

    private final FlowSettingService flowSettingService;

    public FlowSettingController(FlowSettingService flowSettingService) {
        this.flowSettingService = flowSettingService;
    }

    @Operation(summary = "添加一条参数")
    @PostMapping("/add")
    public R add() {


        return R.success();
    }

    @Operation(summary = "读取所有参数")
    @PostMapping("/load")
    public R<List<FlowSetting>> load() {

        List<FlowSetting> load = flowSettingService.load();

        return R.success(load, load.size());
    }


    @GetMapping("/test/addrbs")
    public void addReimburseAuditor() {
        flowSettingService.addDefaultFlowSkip();
    }
}
