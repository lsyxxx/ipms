package com.abt.flow.controller;

import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.FlowRequestForm;
import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.service.FlowInfoService;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程相关
 */
@RestController
@Slf4j
@RequestMapping("/wf/base")
@Tag(name = "FlowController", description = "流程相关")
public class FlowController {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    private final FlowInfoService flowInfoService;
    private final FlowOperationLogService flowOperationLogService;

    public FlowController(FlowInfoService flowInfoService, FlowOperationLogService flowOperationLogService) {
        this.flowInfoService = flowInfoService;
        this.flowOperationLogService = flowOperationLogService;
    }

    @Operation(summary = "查看用户申请的流程")
    @Parameter(name = "form", description = "请求参数，包括分页(page,size)与搜索参数(query)，id, type")
    @GetMapping("/load")
    public R<List<FlowInfoVo>> flowList(FlowRequestForm form) {
        UserView user = TokenUtil.getUserFromAuthToken();
        if (form == null) {
            form = FlowRequestForm.createNoPaging();
        }
        form.setUser(user);

        List<FlowInfoVo> list = flowInfoService.getFlows(form);

        return R.success(list, list.size());
    }

    @Operation(summary = "流程类型列表")
    @Parameter(name = "form", description = "请求参数，包括分页(page,size)与搜索参数(query)，id, type")
    @GetMapping("/cat")
    public R<List<FlowCategory>> flowCategory(@RequestParam int page, @RequestParam int limit) {
        UserView user = TokenUtil.getUserFromAuthToken();
        List<FlowCategory> list = flowInfoService.findAllEnabled(page, limit);
        return R.success(list, list.size());
    }


    @Operation(summary = "流程操作日志列表")
    @Parameter(name = "id", description = "流程id")
    @GetMapping("/log")
    public R<List<FlowOperationLog>> getOperateLog(@NotNull @RequestParam String id) {

        List<FlowOperationLog> logs = flowOperationLogService.getByProcessInstanceId(id);

        return R.success(logs, logs.size());
    }
    

}
