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
@RequestMapping("/wf/base")
@Tag(name = "FlowController", description = "流程相关")
public class FlowController {

    /**
     * 已处理
     */
    public static final String TYPE_DISPOSED = "disposed";

    /**
     * 待处理
     */
    public static final String TYPE_WAIT = "wait";

    private final FlowInfoService flowInfoService;

    public FlowController(FlowInfoService flowInfoService) {
        this.flowInfoService = flowInfoService;
    }

    @Operation(summary = "查看用户申请的流程")
    @Parameter(name = "type", description = "查询流程类型(null我的/disposed已处理/wait待处理")
    @Parameter(name = "query", description = "查询流程的参数")
    @GetMapping("/load")
    public R flowList(@RequestParam String type, @RequestParam String query) {
        UserView user = TokenUtil.getUserFromAuthToken();




        return R.success();
    }
    

}
