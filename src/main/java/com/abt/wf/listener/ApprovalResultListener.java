package com.abt.wf.listener;

import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import static com.abt.wf.config.Constants.VAR_KEY_APPR_RESULT;
import static com.abt.wf.config.Constants.VAR_KEY_DECISION;

/**
 * 审批节点-判断结果
 */
@Component
@Slf4j
public class ApprovalResultListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        final String decision = WorkFlowUtil.getVariable(delegateTask, VAR_KEY_DECISION);
        delegateTask.getExecution().setVariable(VAR_KEY_APPR_RESULT, decision);
    }
}
