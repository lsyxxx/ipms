package com.abt.wf.service;

import com.abt.wf.model.ApprovalTask;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.TaskDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 流程查询
 */
public interface WorkFlowQueryService {


    /**
     * 我申请的报销流程列表
     *
     * @param starter         申请人
     * @param processStartDay 搜索流程开始日期1
     * @param processEndDay   搜索流程开始日期2
     * @param page            页数
     * @param size            单页数量
     */
    List<ReimburseForm> queryMyRbs(String starter, LocalDate processStartDay, LocalDate processEndDay, int page, int size);

    /**
     * 查询我的待办
     * @param userid 用户id
     * @param taskStartTime 搜索任务开始日期1
     * @param taskEndTime 搜索任务开始日期2
     * @param page 页数
     * @param size 单页数量
     * @return
     */
    List<TaskDTO> queryMyTodoList(String userid, LocalDate taskStartTime, LocalDate taskEndTime, int page, int size);

    List<TaskDTO> queryMyDoneList(String userid, LocalDate taskStartTime, LocalDate taskEndTime, int page, int size);

    List<ApprovalTask> queryProcessInstanceLog(String processInstanceId);
}
