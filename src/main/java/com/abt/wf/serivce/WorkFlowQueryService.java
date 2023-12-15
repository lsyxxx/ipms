package com.abt.wf.serivce;

import com.abt.wf.model.TaskDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程查询
 */
public interface WorkFlowQueryService {


    List<TaskDTO> queryMyRbs(String starter, LocalDate processStartDay, LocalDate processEndDay, int page, int size);

    List<TaskDTO> queryTaskListByStartUserid(String taskAssignee, LocalDate taskStartDay, LocalDate taskEndDay, int page, int size);

//    List<TaskDTO> queryTaskListByStartUserid(String starter, int page, int size);
}
