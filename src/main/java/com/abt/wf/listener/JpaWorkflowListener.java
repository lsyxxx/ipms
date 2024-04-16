package com.abt.wf.listener;

import com.abt.common.model.User;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.WorkflowBase;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */

@Slf4j
public class JpaWorkflowListener {

    private final UserService userService;

    public JpaWorkflowListener(@Qualifier("sqlServerUserService") UserService userService) {
        this.userService = userService;
    }

    /**
     * 根据用户id插入申请用户的部门信息
     * createDeptId/creatDeptName/createTeamId/createTeamName
     * @param entity 实体
     */
    @PrePersist
    public <T extends WorkflowBase> void insertCreateDept(T entity) {
        String createUserid = entity.getCreateUserid();
        if (createUserid != null && !createUserid.isEmpty()) {
            final User user = userService.getUserDeptByUserid(createUserid);
            entity.setCreateDeptId(user.getDeptId());
            entity.setCreateDeptName(user.getDeptName());
            entity.setCreateTeamId(user.getTeamId());
            entity.setCreateTeamName(user.getTeamName());
        } else {
            log.warn("未获取创建用户id! - Service: {}, ProcInstId: {}", entity.getServiceName(), entity.getProcessInstanceId());
        }


    }
}
