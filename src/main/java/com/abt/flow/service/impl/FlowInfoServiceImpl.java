package com.abt.flow.service.impl;

import com.abt.common.util.MessageUtil;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.FlowRequestForm;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.repository.FlowCategoryRepository;
import com.abt.flow.service.FlowInfoService;
import com.abt.http.dto.WebApiDto;
import com.abt.http.service.HttpConnectService;
import com.abt.sys.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowInfoServiceImpl implements FlowInfoService {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    private final HttpConnectService<WebApiDto> httpConnectService;


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

    @Value("webapi.http.api.flowschemes")
    private String flowSchemeApi;

    @Override
    public List<FlowInfoVo> getUserApplyFlows(FlowRequestForm form) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .startedBy(form.getUserid());
        if (StringUtils.isNotBlank(form.getState())) {
            //approve, reject
            query.processInstanceBusinessStatusLike("%" + form.getState() + "%");
        }
        if (StringUtils.isNotBlank(form.getResult())) {
            //active 审批中, terminated 终止 , Finished 完成
            query.processInstanceBusinessStatusLike("%" + form.getResult() + "%");
        }
        if (StringUtils.isNotBlank(form.getId())) {
            //审批编号
            query.processInstanceBusinessKey(form.getId());
        }
        if (StringUtils.isNotBlank(form.getType())) {
            //业务类型
            query.variableValueEquals(FlowableConstant.PV_BIZ_ID, form.getType());
        }

        List<HistoricProcessInstance> list = query.orderByProcessInstanceStartTime().desc()
                .listPage(form.getFirstResult(), form.getLimit());
        list = ListUtils.emptyIfNull(list);
        List<FlowInfoVo> collect = list.stream().map(h -> fulfill(h)).toList();
        return collect;
    }

    private FlowInfoVo fulfill(HistoricProcessInstance processInstance) {
        //1.
        FlowInfoVo flowInfoVo = FlowInfoVo.create(processInstance);

        //2.当前负责人
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        flowInfoVo.updateTask(task);

        //3. 其它需要从variables获取
        setProcessVariables(processInstance.getId(), flowInfoVo);

        return flowInfoVo;
    }

    private void setProcessVariables(String procId, FlowInfoVo flowInfoVo) {
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(procId).list();
        flowInfoVo.updateVariables(list);
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
        return flowCategoryRepository.findAll(enabledExample, Sort.by(Sort.Direction.DESC, ORDERBY_SORT, ORDERBY_CRTDATE));
    }

    @Override
    public List<FlowInfoVo> getTodoFlows(FlowRequestForm form) {
        //没有查询条件
        List<Task> tasks = taskService.createTaskQuery().active().taskAssignee(form.getUserid()).orderByTaskCreateTime().asc().listPage(form.getFirstResult(), form.getLimit());

        tasks = ListUtils.emptyIfNull(tasks);

        return tasks.stream().map(t -> {
            String procId = t.getProcessInstanceId();
            HistoricProcessInstance process = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
            FlowInfoVo flowInfoVo = FlowInfoVo.create(process);
            flowInfoVo.updateTask(t);

            List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery().processInstanceId(procId).list();
            flowInfoVo.updateVariables(vars);

            return flowInfoVo;
        }).toList();
    }

    @Override
    public List<FlowInfoVo> getCompletedFlows(FlowRequestForm form) {

        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(form.getId()).finished();

        if (StringUtils.isNotBlank(form.getState())) {
            query.processInstanceBusinessKeyLike("%" + form.getState() + "%");
        }
        if (StringUtils.isNotBlank(form.getResult())) {
            query.processInstanceBusinessKeyLike("%" + form.getResult() + "%");
        }
        if (StringUtils.isNotBlank(form.getId())) {
            //审批编号
            query.processInstanceBusinessKey(form.getId());
        }
        if (StringUtils.isNotBlank(form.getType())) {
            //业务类型
            query.processVariableValueEquals(FlowableConstant.PV_BIZ_ID, form.getType());
        }


        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskInvolvedUser(form.getId()).finished()
                .orderByTaskCreateTime().desc()
                .listPage(form.getFirstResult(), form.getLimit());
        list = ListUtils.emptyIfNull(list);

        return list.stream().map(i -> {
            String procId = i.getProcessInstanceId();
            HistoricProcessInstance process = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
            FlowInfoVo vo = FlowInfoVo.create(process);
            Task task = taskService.createTaskQuery().processInstanceId(procId).active().singleResult();
            vo.updateTask(task);
            List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery().processInstanceId(procId).list();
            vo.updateVariables(vars);
            return vo;
        }).toList();
    }

    @Override
    public List<FlowInfoVo> getFlows(FlowRequestForm form) {
        switch (form.getType()) {
            case TYPE_WAIT -> {
               return getTodoFlows(form);
            }
            case TYPE_DISPOSED -> {
                return getCompletedFlows(form);
            }
            default -> {
                //没有参数
                return getUserApplyFlows(form);
            }
        }
    }


    @Override
    public List<Comment> getComments(String procId) {
        //order by time desc
        return taskService.getProcessInstanceComments(procId);
    }

}
