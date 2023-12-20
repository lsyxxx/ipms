package com.abt.wf.serivce.impl;

import com.abt.common.util.TimeUtil;
import com.abt.wf.model.TaskDTO;
import com.abt.wf.repository.WorkFlowRepository;
import com.abt.wf.serivce.WorkFlowQueryService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Comment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class WorkFlowQueryServiceImpl implements WorkFlowQueryService {

    private final WorkFlowRepository workFlowRepository;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    /**
     * 查询时间，参考飞书
     * 0 自定义时间
     */
    private int queryTime;

    public WorkFlowQueryServiceImpl(WorkFlowRepository workFlowRepository, HistoryService historyService, RuntimeService runtimeService, TaskService taskService) {
        this.workFlowRepository = workFlowRepository;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Override
    public List<TaskDTO> queryMyRbs(String starter, LocalDate processStartDay, LocalDate processEndDay, int page, int size) {
        return workFlowRepository.findProcessByStartUseridAndDayRange(starter,
                TimeUtil.yyyy_MM_ddString(processStartDay),
                TimeUtil.yyyy_MM_ddString(processEndDay),
                page, size);
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


    @Override
    public List<TaskDTO> queryProcessInstanceLog(String processInstanceId, String userid) {
        List<TaskDTO> tasks = new ArrayList<>();
        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        //节点名称,审批人,审批结果, 审批意见, 审批时间
        list.forEach(i -> {
            final List<Comment> taskComments = taskService.getTaskComments(i.getId());
            TaskDTO dto = TaskDTO.from(i);
            dto.setComments(taskComments);
            tasks.add(dto);
        });

        return tasks;
    }

}
