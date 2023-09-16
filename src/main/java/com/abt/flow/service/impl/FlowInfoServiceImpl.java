package com.abt.flow.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.util.TimeUtil;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.ProcessState;
import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.repository.FlowCategoryRepository;
import com.abt.flow.service.FlowInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class FlowInfoServiceImpl implements FlowInfoService {

    public static final String ORDERBY_CRTDATE = "createDate";
    public static final String ORDERBY_SORT= "sortCode";

    private final FlowCategoryRepository flowCategoryRepository;

    private final Example<FlowCategory> enabledExample;

    private final HistoryService historyService;
    private final RuntimeService runtimeService;

    private final TaskService taskService;

    /**
     * 已处理
     */
    public static final String TYPE_DISPOSED = "disposed";

    /**
     * 待处理
     */
    public static final String TYPE_WAIT = "wait";

    public FlowInfoServiceImpl(FlowCategoryRepository flowCategoryRepository, Example<FlowCategory> enabledExample, HistoryService historyService, RuntimeService runtimeService, TaskService taskService) {
        this.flowCategoryRepository = flowCategoryRepository;
        this.enabledExample = enabledExample;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Override
    public List<FlowInfoVo> getUserApplyFlows(RequestForm form) {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().startedBy(form.getId()).orderByProcessInstanceStartTime().list();
        list = ListUtils.emptyIfNull(list);
        List<FlowInfoVo> collect = list.stream().map(h -> convert(h)).collect(Collectors.toList());
        return collect;
    }

    private FlowInfoVo convert(HistoricProcessInstance processInstance) {
        FlowInfoVo flowInfoVo = create(processInstance);
        //TODO
        //1. 审批状态，审批结果
        String bizState = processInstance.getBusinessStatus();
        ProcessState state = ProcessState.of(bizState);
        //审批状态
        flowInfoVo.setState(state);
        flowInfoVo.setResult(state.auditResult());

        //2.当前负责人
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        flowInfoVo.setCurrentUser(task.getAssignee());

        //其它需要从variables获取


        return flowInfoVo;
    }



    public FlowInfoVo create(HistoricProcessInstance process) {
        FlowInfoVo vo = new FlowInfoVo();
        vo.setProcInstId(process.getId());
        vo.setBusinessKey(process.getBusinessKey());
        vo.setCreateDate(TimeUtil.from(process.getStartTime()));
        vo.setCreateUserid(process.getStartUserId());
        return vo;
    }


    @Override
    public List<FlowCategory> findAllEnabled(int page, int size) {
        if (size == 0) {
            //不分页
            return findAllEnabled();
        }

        //分页
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, ORDERBY_SORT, ORDERBY_CRTDATE);
        Page<FlowCategory> all = flowCategoryRepository.findAll(enabledExample, pageRequest);
        if (all.hasContent()) {
            return all.getContent();
        }
        return List.of();
    }
    @Override
    public List<FlowCategory> findAllEnabled() {
        List<FlowCategory> all = flowCategoryRepository.findAll(enabledExample, Sort.by(Sort.Direction.DESC, ORDERBY_SORT, ORDERBY_CRTDATE));
        return all;
    }

    @Override
    public List<FlowInfoVo> getTodoFlows(RequestForm form) {
        return null;
    }

    @Override
    public List<FlowInfoVo> getCompletedFlows(RequestForm form) {
        return null;
    }

    @Override
    public List<FlowInfoVo> getFlows(RequestForm form) {
        return null;
    }


    @Override
    public FlowCategory getFlowCategory(String id) {
        return null;
    }

}
