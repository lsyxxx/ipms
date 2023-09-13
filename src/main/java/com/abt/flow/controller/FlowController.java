package com.abt.flow.controller;

import com.abt.common.model.R;
import com.abt.common.model.RequestForm;
import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.service.FlowInfoService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
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

    public FlowController(FlowInfoService flowInfoService) {
        this.flowInfoService = flowInfoService;
    }

    @Operation(summary = "查看用户申请的流程")
    @Parameter(name = "form", description = "请求参数，包括分页(page,size)与搜索参数(query)，id, type")
    @GetMapping("/load")
    public R flowList(@RequestParam int page, @RequestParam int limit, @RequestParam(required = false) String type, @RequestParam(required = false) String query) {
        UserView user = TokenUtil.getUserFromAuthToken();

        RequestForm form = RequestForm.of(page, limit, type, query);
        form.setId(user.getId());

        List<BizFlowRelation> list = flowInfoService.getFlows(form);

        return R.success(list, list.size());
    }

    @Operation(summary = "流程类型列表")
    @Parameter(name = "form", description = "请求参数，包括分页(page,size)与搜索参数(query)，id, type")
    @GetMapping("/cat")
    public R flowCategory(@RequestParam int page, @RequestParam int limit) {
        UserView user = TokenUtil.getUserFromAuthToken();
        //不分页
        List<FlowCategory> list = flowInfoService.findAllEnabled(page, limit);
        return R.success(list, list.size());
    }
    

}
