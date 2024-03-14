package com.abt.wfbak.repository;

import com.abt.wfbak.model.TaskDTO;

import java.util.List;

/**
 * @description 流程复合查询
 */
public interface WorkFlowRepository{
    List<TaskDTO> findProcessByStartUseridAndDayRange(String userid, String processStartDate, String processEndDay, int page, int size);

    List<TaskDTO> findTaskByAssigneeAndDayRange(String userid, String taskStartDay, String taskEndDay, boolean isFinished, int page, int size);

}
