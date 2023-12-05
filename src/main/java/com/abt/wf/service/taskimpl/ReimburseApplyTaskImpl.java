package com.abt.wf.service.taskimpl;

import com.abt.common.model.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 处理申请的ServiceTask
 */
@Service("reimburseApplyTaskImpl")
@Slf4j
public class ReimburseApplyTaskImpl implements JavaDelegate {
    @Autowired
    private Map<String, User> flowSkipManagerMap;

    public ReimburseApplyTaskImpl() {
        super();
    }

//    public ReimburseApplyTaskImpl(Map<String, User> flowSkipManagerMap) {
//        this.flowSkipManagerMap = flowSkipManagerMap;
//    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("处理报销申请...");
        //1. execute variables
        double cost = (double) execution.getVariable("cost");
        String starter = (String) execution.getVariable("starter");
        //isLeader
        boolean isLeader = flowSkipManagerMap.containsKey(starter);
        execution.setVariable("isLeader", flowSkipManagerMap.containsKey(starter));
        //2 pre-compile

        //2. save to db

        //3. log
    }
}
