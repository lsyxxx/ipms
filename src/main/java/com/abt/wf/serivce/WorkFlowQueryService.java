package com.abt.wf.serivce;

import com.abt.wf.model.TaskDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程查询
 */
public interface WorkFlowQueryService {


    List<TaskDTO> queryTaskListByStartUserid(String starter, int page, int size);
}
