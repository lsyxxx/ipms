package com.abt.flow.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.common.util.MessageUtil;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.ProcessState;
import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.repository.BizFlowCategoryRepository;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowInfoService;
import com.abt.sys.exception.BadRequestParameterException;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class FlowInfoServiceImpl implements FlowInfoService {

    public static final String ORDERBY_CRTDATE = "createDate";
    public static final String ORDERBY_SORT= "sortCode";

    private final BizFlowCategoryRepository bizFlowCategoryRepository;

    private final Example<FlowCategory> enabledExample;

    private final BizFlowRelationRepository bizFlowRelationRepository;

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

    public FlowInfoServiceImpl(BizFlowCategoryRepository bizFlowCategoryRepository, Example<FlowCategory> enabledExample, BizFlowRelationRepository bizFlowRelationRepository, HistoryService historyService, RuntimeService runtimeService, TaskService taskService) {
        this.bizFlowCategoryRepository = bizFlowCategoryRepository;
        this.enabledExample = enabledExample;
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }


    @Override
    public List<BizFlowRelation> getUserApplyFlows(RequestForm form) {
        PageRequest pageRequest = PageRequest.of(form.getPage(), form.getLimit(), Sort.Direction.DESC, ORDERBY_CRTDATE);
        Page<BizFlowRelation> all = bizFlowRelationRepository.findByStarterIdOrCustomNameContainingOrderByStartDateDesc(form.getId(), form.getQuery(), pageRequest);
        return all.getContent();
    }


    @Override
    public List<FlowCategory> findAllEnabled(int page, int size) {
        if (size == 0) {
            //不分页
            return findAllEnabled();
        }

        //分页
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, ORDERBY_SORT, ORDERBY_CRTDATE);
        Page<FlowCategory> all = bizFlowCategoryRepository.findAll(enabledExample, pageRequest);
        if (all.hasContent()) {
            return all.getContent();
        }
        return List.of();
    }
    @Override
    public List<FlowCategory> findAllEnabled() {
        List<FlowCategory> all = bizFlowCategoryRepository.findAll(enabledExample, Sort.by(Sort.Direction.DESC, ORDERBY_SORT, ORDERBY_CRTDATE));
        return all;
    }


    @Override
    public List<BizFlowRelation> getTodoFlows(RequestForm form) {
        log.info("---- 查询用户待办流程: user: {}, page: {}, limit: {}", form.getId(), form.getPage(), form.getLimit());
        PageRequest pageRequest = PageRequest.of(form.getPage(), form.getLimit(), Sort.Direction.DESC, ORDERBY_CRTDATE);
        Page<BizFlowRelation> page = bizFlowRelationRepository.findByCurrentUserAndStateOrCustomNameContainingOrderByLastUpdateDateDesc(form.getId(), ProcessState.Active.code(), form.getQuery(), pageRequest);
        return page.getContent();
    }

    @Override
    public List<BizFlowRelation> getCompletedFlows(RequestForm form) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee(form.getId()).list();
        List<BizFlowRelation> relList = list.stream().map(i -> {
            BizFlowRelation rel = new BizFlowRelation();
            Map<String, Object> processVariables = runtimeService.getVariables(i.getProcessInstanceId());
            rel.setCustomName(String.valueOf(processVariables.get(FlowableConstant.PV_CUSTOM_NAME)));
            rel.setProcInstId(i.getProcessInstanceId());
            rel.setProcDefId(i.getProcessDefinitionId());
            return rel;
        }).collect(Collectors.toList());
        return relList;
    }

    @Override
    public List<BizFlowRelation> getFlows(RequestForm form) {
        List<BizFlowRelation> list;
        switch (form.getType().toLowerCase()) {
            case "": list = getUserApplyFlows(form); break;
            case TYPE_DISPOSED:
                list = getCompletedFlows(form);
                break;
            case TYPE_WAIT:
                list = getTodoFlows(form);
                break;
            default:
                log.error("Bad Request - 未知的参数:{}", form.getType());
                throw new BadRequestParameterException(MessageUtil.format("flow.service.FlowInfoServiceImpl.getFlows", form.getType()));
        }
        return list;
    }

}
