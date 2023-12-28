package com.abt.wf.serivce.impl;

import com.abt.common.util.QueryUtil;
import com.abt.common.util.TimeUtil;
import com.abt.wf.model.ApprovalTask;
import com.abt.wf.model.ReimburseDTO;
import com.abt.wf.model.TaskDTO;
import com.abt.wf.repository.WorkFlowRepository;
import com.abt.wf.serivce.ReimburseService;
import com.abt.wf.serivce.WorkFlowQueryService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class WorkFlowQueryServiceImpl implements WorkFlowQueryService {

    private final WorkFlowRepository workFlowRepository;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private final ReimburseService reimburseService;

    private final RepositoryService repositoryService;

    private final Map<String, ProcessDefinition> processDefinitionMap;

    private final Map<String, BpmnModelInstance> bpmnModelInstanceMap;




    /**
     * 查询时间，参考飞书
     * 0 自定义时间
     */
    private int queryTime;

    public WorkFlowQueryServiceImpl(WorkFlowRepository workFlowRepository, HistoryService historyService, RuntimeService runtimeService, TaskService taskService, ReimburseService reimburseService, RepositoryService repositoryService, @Qualifier("processDefinitionMap") Map<String, ProcessDefinition> processDefinitionMap, @Qualifier("bpmnModelInstanceMap") Map<String, BpmnModelInstance> bpmnModelInstanceMap) {
        this.workFlowRepository = workFlowRepository;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.reimburseService = reimburseService;
        this.repositoryService = repositoryService;
        this.processDefinitionMap = processDefinitionMap;
        this.bpmnModelInstanceMap = bpmnModelInstanceMap;
    }

    @Override
    public List<ReimburseDTO> queryMyRbs(String starter, LocalDate processStartDay, LocalDate processEndDay, int page, int size) {
        List<ReimburseDTO> entities = reimburseService.queryByStater(starter, page, size);
        List<TaskDTO> list = workFlowRepository.findProcessByStartUseridAndDayRange(starter,
                TimeUtil.yyyy_MM_ddString(processStartDay),
                TimeUtil.yyyy_MM_ddString(processEndDay),
                QueryUtil.NO_PAGING, QueryUtil.NO_PAGING);
       Map<String, TaskDTO> temp = list.stream().collect(Collectors.toMap(TaskDTO::getProcessInstanceId, taskDTO -> taskDTO));
        entities.forEach(i -> {
            i.setTaskDTO(temp.get(i.getProcessInstanceId()));
        });
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

    @Override
    public List<TaskDTO> queryMyDoneList(String userid, LocalDate taskStartTime, LocalDate taskEndTime, int page, int size) {
        return workFlowRepository.findTaskByAssigneeAndDayRange(userid,
                TimeUtil.yyyy_MM_ddString(taskStartTime),
                TimeUtil.yyyy_MM_ddString(taskEndTime),
                true,
                page, size);
    }

//    @Override
//    public List<List<TaskDTO>> queryProcessInstanceLog(String processInstanceId) {
//        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
//        LinkedHashMap<String, List<TaskDTO>> temp = new LinkedHashMap<>();
//        for (HistoricTaskInstance historicTaskInstance : list) {
//            String taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
//            List<TaskDTO> taskDTOList = temp.getOrDefault(taskDefinitionKey, new ArrayList<>());
//            taskDTOList.add(TaskDTO.from(historicTaskInstance));
//            temp.put(taskDefinitionKey, taskDTOList);
//        }
//        return new ArrayList<>(temp.values());
//    }

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
            }

            approvalTask.addTask(dto);
            apprList.add(approvalTask);
        }
        return apprList;
    }



    public Collection<CamundaProperty> queryUserTaskBpmnModelExtensionProperties(BpmnModelInstance bpmnModelInstance, String taskDefId) {
        UserTask userTaskModel = bpmnModelInstance.getModelElementById(taskDefId);
        ExtensionElements extensionElements = userTaskModel.getExtensionElements();
        Collection<CamundaProperty> properties = extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .singleResult()
                .getCamundaProperties();
        return properties;
    }



}
