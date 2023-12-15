package com.abt.wf.serivce.impl;

import com.abt.wf.model.TaskDTO;
import com.abt.wf.repository.WorkFlowRepository;
import com.abt.wf.serivce.WorkFlowQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class WorkFlowQueryServiceImpl implements WorkFlowQueryService {

    private final WorkFlowRepository workFlowRepository;

    public WorkFlowQueryServiceImpl(WorkFlowRepository workFlowRepository) {
        this.workFlowRepository = workFlowRepository;
    }

    public List<TaskDTO> queryMyRbs(String starter, String starterName, int page, int size) {
        List<TaskDTO> tasks = queryTaskListByStartUserid(starter, page, size);
        return tasks;
    }


    @Override
    public List<TaskDTO> queryTaskListByStartUserid(String starter, int page, int size) {
        int skip = (page - 1) * size;
        List<TaskDTO> taskList = workFlowRepository.findByStartUserid(starter)  ;
        List<TaskDTO> paged = taskList.stream().skip(skip).limit(size).toList();
        return paged;
    }

}
