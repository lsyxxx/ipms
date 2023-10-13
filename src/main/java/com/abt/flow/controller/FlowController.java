package com.abt.flow.controller;

import com.abt.ApplicationContextHolder;
import com.abt.common.model.R;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.FlowRequestForm;
import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.model.entity.FlowScheme;
import com.abt.flow.service.FlowEntry;
import com.abt.flow.service.FlowInfoService;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.task.Comment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程相关
 */
@RestController
@Slf4j
@RequestMapping("/wf/base")
@Tag(name = "FlowController", description = "流程相关")
@CrossOrigin
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
    @Deprecated
    public R<List<FlowScheme>> flowScheme(@RequestParam int page, @RequestParam int limit) {
        final List<FlowScheme> list = flowInfoService.getFlowScheme();
        return R.success(list, list.size());
    }

    @Operation(summary = "流程操作日志列表")
    @Parameter(name = "id", description = "流程id")
    @GetMapping("/log")
    public R<List<FlowOperationLog>> getOperateLog(@NotNull @RequestParam String id) {

        final List<FlowOperationLog> logs = flowOperationLogService.getByProcessInstanceId(id);
        final List<Comment> comments = flowInfoService.getComments(id);
        if (!comments.isEmpty()) {
            Map<String, Comment> commentMap = comments.stream()
                    .collect(Collectors.toMap(Comment::getTaskId, comment -> comment));
            logs.forEach(i -> {
                i.setComment(commentMap.get(i.getTaskId()).getFullMessage());
            });
        }

        return R.success(logs, logs.size());
    }


    /**
     * 打开流程实例详情
     * @return
     */
    @Operation(summary = "打开流程实例详情")
    @Parameter(name = "id", description = "流程实例id")
    @Parameter(name = "service", description = "业务对应service beanName")
    @GetMapping("/get")
    public R get(String id, String service) {
        final FlowEntry bean = (FlowEntry) ApplicationContextHolder.getBean(service);
        return R.success(bean.get(id));
    }



}
