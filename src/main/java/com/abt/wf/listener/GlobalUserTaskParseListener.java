package com.abt.wf.listener;

import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.stereotype.Component;

/**
 * 为所有usertask添加监听
 */
@Component
public class GlobalUserTaskParseListener  extends AbstractBpmnParseListener {

    private final ApprovalResultListener approvalResultListener;

    public GlobalUserTaskParseListener(ApprovalResultListener approvalResultListener) {
        this.approvalResultListener = approvalResultListener;
    }

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        if (activity.getActivityBehavior() instanceof UserTaskActivityBehavior) {
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activity.getActivityBehavior()).getTaskDefinition();

            // 给 UserTask complete 绑定全局监听器
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_COMPLETE, approvalResultListener);
        }
    }



}
