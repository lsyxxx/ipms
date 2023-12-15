package com.abt.wf.repository;

import com.abt.wf.model.TaskDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @description 流程复合查询
 */
public interface WorkFlowRepository{
    List<TaskDTO> findProcessByStartUseridAndDayRange(String userid, String processStartDate, String processEndDay, int page, int size);

//    List<TaskDTO> findByStartUserid(String userid);

    List<TaskDTO> findTaskByAssigneeAndDayRange(String userid, String taskStartDay, String taskEndDay, int page, int size);

//    List<TaskDTO> findByAssignee(String userid);
}
