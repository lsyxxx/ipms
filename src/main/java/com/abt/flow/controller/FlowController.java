package com.abt.flow.controller;

import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.flow.service.FlowInfoService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 流程相关
 */
@RestController
@Slf4j
@RequestMapping("/flow")
@Tag(name = "FlowController", description = "流程相关")
public class FlowController {


    private final FlowInfoService flowInfoService;

    public FlowController(FlowInfoService flowInfoService) {
        this.flowInfoService = flowInfoService;
    }

    @Operation(summary = "查看用户申请的流程")
    @Parameter(name = "userId", description = "用户id")
    @GetMapping("/list")
    public R flowList() {
        UserView user = TokenUtil.getUserFromAuthToken();




        return R.success();
    }

}
