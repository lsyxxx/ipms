package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ApprovalTask;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.TaskDTO;
import com.abt.wf.repository.WorkFlowRepository;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.service.WorkFlowQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;

/**
 *
 */
@Service
@Slf4j
public class WorkFlowQueryServiceImpl implements WorkFlowQueryService {

    private final WorkFlowRepository workFlowRepository;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private final ReimburseService reimburseService;

    private final RepositoryService repositoryService;

    private final Map<String, ProcessDefinition> processDefinitionMap;

    private final Map<String, BpmnModelInstance> bpmnModelInstanceMap;

    private final UserService sqlServerUserService;


    /**
     * 查询时间，参考飞书
     * 0 自定义时间
     */
    private int queryTime;

    public WorkFlowQueryServiceImpl(WorkFlowRepository workFlowRepository, HistoryService historyService, RuntimeService runtimeService, TaskService taskService, ReimburseService reimburseService, RepositoryService repositoryService, @Qualifier("processDefinitionMap") Map<String, ProcessDefinition> processDefinitionMap, @Qualifier("bpmnModelInstanceMap") Map<String, BpmnModelInstance> bpmnModelInstanceMap, @Qualifier("sqlServerUserService") UserService sqlServerUserService) {
        this.workFlowRepository = workFlowRepository;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.reimburseService = reimburseService;
        this.repositoryService = repositoryService;
        this.processDefinitionMap = processDefinitionMap;
        this.bpmnModelInstanceMap = bpmnModelInstanceMap;
        this.sqlServerUserService = sqlServerUserService;
    }

    public static Collection<CamundaProperty> queryUserTaskBpmnModelExtensionProperties(BpmnModelInstance bpmnModelInstance, String taskDefId) {
        UserTask userTaskModel = bpmnModelInstance.getModelElementById(taskDefId);
        ExtensionElements extensionElements = userTaskModel.getExtensionElements();
        return extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .singleResult()
                .getCamundaProperties();
    }

    @Override
    public List<ReimburseForm> queryMyRbs(String starter, LocalDate processStartDay, LocalDate processEndDay, int page, int size) {
        List<ReimburseForm> entities = reimburseService.queryByStater(starter, page, size);
//        List<TaskDTO> list = workFlowRepository.findProcessByStartUseridAndDayRange(starter,
//                TimeUtil.yyyy_MM_ddString(processStartDay),
//                TimeUtil.yyyy_MM_ddString(processEndDay),
//                QueryUtil.NO_PAGING, QueryUtil.NO_PAGING);
//        Map<String, TaskDTO> temp = list.stream().collect(Collectors.toMap(TaskDTO::getProcessInstanceId, taskDTO -> taskDTO));
        Map<String, Task> temp = new HashMap<>();
        long start = System.currentTimeMillis();
        //查询当前task
        entities.forEach(i -> {
            boolean isFinished = i.isFinished();
            if (!isFinished) {
                long t1 = System.currentTimeMillis();
                //未完成的流程查询当前任务，已完成的不用
                final Task task = taskService.createTaskQuery().active().processInstanceId(i.getProcessInstanceId()).singleResult();
                temp.put(task.getProcessInstanceId(), task);
                long t2 = System.currentTimeMillis();
                i.setCurrentTask(task);
                //task assignee
                i.setCurrentTaskUserid(task.getAssignee());
                final User taskUser = sqlServerUserService.getSimpleUserInfo(new User(task.getAssignee()));
                log.debug("currentTaskUser info: {}", taskUser.toString());
                i.setCurrentTaskUsername(taskUser.getUsername());
                log.info("查询一个task时间: {}ms", (t2-t1));
            }
        });
        long end = System.currentTimeMillis();
        log.info("使用流程api查询所有流程时间: {}ms", (end-start));
        return entities;
    }

    @Override
    public List<TaskDTO> queryMyTodoList(String userid, LocalDate taskStartTime, LocalDate taskEndTime, int page, int size) {
        return workFlowRepository.findTaskByAssigneeAndDayRange(userid,
                TimeUtil.yyyy_MM_ddString(taskStartTime),
                TimeUtil.yyyy_MM_ddString(taskEndTime),
                false,
                page, size);
    }


    public List<ReimburseForm> queryMyTodoList2(String userid, int page, int size) {
        List<ReimburseForm> list = new ArrayList<>();
        int firstResult = (page - 1) * size;
        final List<Task> tasks = taskService.createTaskQuery().active().taskAssignee(userid).listPage(firstResult, size);
        if (!CollectionUtils.isEmpty(tasks)) {
            tasks.forEach(i -> {
                final String entityId = this.getEntityIdFromProcessVariables(i.getProcessInstanceId());
                try {
                    ReimburseForm form = reimburseService.loadReimburse(entityId);
                } catch (Exception e) {
                    log.error("查询报销实体对象失败: ", e);
                }
            });
        }


        return list;
    }

    @Override
    public List<TaskDTO> queryMyDoneList(String userid, LocalDate taskStartTime, LocalDate taskEndTime, int page, int size) {
        return workFlowRepository.findTaskByAssigneeAndDayRange(userid,
                TimeUtil.yyyy_MM_ddString(taskStartTime),
                TimeUtil.yyyy_MM_ddString(taskEndTime),
                true,
                page, size);
    }

    /**
     * 多实例流程记录查询
     *
     * @param processInstanceId 流程实例id
     */
    @Override
    public List<ApprovalTask> queryProcessInstanceLog(String processInstanceId) {
        List<ApprovalTask> apprList = new ArrayList<>();
        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        final String processDefinitionKey = list.get(0).getProcessDefinitionKey();
        final String processDefinitionId = list.get(0).getProcessDefinitionId();
        Map<String, ApprovalTask> map = new HashMap<>();
        for (HistoricTaskInstance historicTaskInstance : list) {
            TaskDTO dto = TaskDTO.from(historicTaskInstance);
            String taskDefId = dto.getTaskDefKey();
            String taskName = dto.getTaskDefName();
            ApprovalTask approvalTask = map.get(taskDefId);
            if (approvalTask == null) {
                approvalTask = new ApprovalTask();
                approvalTask.setTaskDefId(taskDefId);
                approvalTask.setTaskDefName(taskName);
                approvalTask.setProcessInstanceId(dto.getProcessInstanceId());
                approvalTask.setProcessDefKey(processDefinitionKey);
                approvalTask.setProcessDefId(processDefinitionId);
                BpmnModelInstance bpmnModelInstance = bpmnModelInstanceMap.get(processDefinitionKey);
                final Collection<CamundaProperty> extensionProperties = queryUserTaskBpmnModelExtensionProperties(bpmnModelInstance, taskDefId);
                approvalTask.setProperties(extensionProperties);
                map.put(taskDefId, approvalTask);
            }
            if (StringUtils.isNotBlank(dto.getAssigneeId())) {
                final User simpleUserInfo = sqlServerUserService.getSimpleUserInfo(new User(dto.getAssigneeId()));
                dto.setAssigneeName(simpleUserInfo.getUsername());
            }
            approvalTask.addTask(dto);
            apprList.add(approvalTask);
        }
        return apprList;
    }

    @Override
    public String getEntityIdFromProcessVariables(String processInstanceId) {
        final VariableInstance variableInstance = runtimeService.createVariableInstanceQuery().processInstanceIdIn(processInstanceId).variableName(WorkFlowExecutionServiceImpl.VARS_ENTITY_ID).singleResult();
        if (variableInstance != null) {
            return variableInstance.getValue().toString();
        }
        return null;
    }

}
