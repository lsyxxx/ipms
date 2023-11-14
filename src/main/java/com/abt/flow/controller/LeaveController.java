package com.abt.flow.controller;

import com.abt.common.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请假申请
 */
@RestController
@Slf4j
@RequestMapping("/wf/leave")
@Tag(name = "LeaveController", description = "请假申请")
public class LeaveController {


    @Operation(summary = "获取一个请假实例详情")
    @Parameter(name = "applyForm", description = "申请业务数据form")
    @GetMapping("/get")
    public R get(String procId ) {
        log.info("leave --- {}", procId);
        return R.success();
    }
}
