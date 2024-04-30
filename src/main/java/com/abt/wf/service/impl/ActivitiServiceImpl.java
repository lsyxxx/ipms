package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.Loan;
import com.abt.wf.model.LoanRequestForm;
import com.abt.wf.model.TaskWrapper;
import com.abt.wf.model.act.ActHiTaskInstance;
import com.abt.wf.model.act.ActRuTask;
import com.abt.wf.repository.act.ActRuTaskRepository;
import com.abt.wf.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.abt.common.util.QueryUtil.like;

/**
 *
 */
@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    private final WorkFlowConfig workFlowConfig;
    private final ActRuTaskRepository actRuTaskRepository;

    public ActivitiServiceImpl(WorkFlowConfig workFlowConfig, ActRuTaskRepository actRuTaskRepository) {
        this.workFlowConfig = workFlowConfig;
        this.actRuTaskRepository = actRuTaskRepository;
    }

    @Override
    public List<TaskWrapper> findFinanceTask(String assignee, String... defKeys) {
        return List.of();
    }

    @Override
    public List<User> findDefaultCopyUsers() {
        return workFlowConfig.workflowDefaultCopy();
    }


    public ActRuTask findActRuTaskByProcInstId(String id) {
        return actRuTaskRepository.findByProcInstId(id);
    }

    public void findDone(String assignee, String procDefKey) {
        ActHiTaskSpecifications specs = new ActHiTaskSpecifications();
        Specification<ActHiTaskInstance> cr = Specification.where(specs.assigneeEquals(assignee))
                .and(specs.taskIsDone())
                .and(specs.processDefinitionKeyLike(procDefKey))
                .and(specs.taskDefinitionKeyNotLike("apply"))
                ;
    }

    static class ActHiTaskSpecifications {
        public Specification<ActHiTaskInstance> assigneeEquals(String assignee) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(assignee)) {
                    return builder.equal(root.get("assignee"), assignee);
                }
                return null;
            };
        }

        public Specification<ActHiTaskInstance> processInstanceIdEquals(String procId) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(procId)) {
                    return builder.equal(root.get("procInstId"), procId);
                }
                return null;
            };
        }

        public Specification<ActHiTaskInstance> processDefinitionKeyLike(String procDefKey) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(procDefKey)) {
                    return builder.like(root.get("procDefKey"), like(procDefKey));
                }
                return null;
            };
        }

        public Specification<ActHiTaskInstance> taskDefinitionKeyNotLike(String taskDefKey) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(taskDefKey)) {
                    return builder.notLike(root.get("taskDefKey"), like(taskDefKey));
                }
                return null;
            };
        }

        public Specification<ActHiTaskInstance> taskIsDone() {
            return (root, query, builder) -> builder.isNotNull(root.get("endTime"));
        }
    }

}
