package com.abt.wf.listener;

import com.abt.common.model.User;
import com.abt.sys.repository.UserRepository;
import com.abt.wf.entity.WorkflowBase;
import jakarta.persistence.PrePersist;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */

@Slf4j
public class JpaWorkflowListener {

    private final UserRepository userRepository;

    public JpaWorkflowListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据用户id插入用户名称及申请用户的部门信息
     * createDeptId/creatDeptName/createTeamId/createTeamName
     * @param entity 实体
     */
    @PrePersist
    public <T extends WorkflowBase> void insertCreateDept(T entity) {
        String createUserid = entity.getCreateUserid();
        if (createUserid != null && !createUserid.isEmpty()) {
            try {
                final User user = userRepository.getEmployeeDeptByUserid(createUserid);
                if (user != null) {
                    entity.setCreateDeptId(user.getDeptId());
                    entity.setCreateDeptName(user.getDeptName());
                    entity.setCreateTeamId(user.getTeamId());
                    entity.setCreateTeamName(user.getTeamName());entity.setCreateUsername(user.getUsername());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                log.warn("未获取用户信息 - createUserid: {}", createUserid);
            }

        } else {
            log.warn("未获取创建用户id! - Service: {}, ProcInstId: {}", entity.getServiceName(), entity.getProcessInstanceId());
        }
     }
}
