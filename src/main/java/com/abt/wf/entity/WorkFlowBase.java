package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.abt.wf.config.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 流程基础数据
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkFlowBase extends AuditInfo {

    //-- process
    @Column(name="proc_def_key", columnDefinition="VARCHAR(128)")
    private String processDefinitionKey;

    @Column(name="proc_def_id", columnDefinition="VARCHAR(128)")
    private String processDefinitionId;

    //-- processInstance
    @Column(name="proc_inst_id", columnDefinition="VARCHAR(128)")
    private String processInstanceId;

    /**
     * 业务状态，需要区别流程状态
     */
    @Column(name="biz_state", columnDefinition="TINYINT")
    private String businessState;

    /**
     * 业务是否结束，区别流程
     */
    @Column(columnDefinition="BIT")
    private boolean isFinished;

    //-- 当前正在进行的task
    @Column(name="cur_task_id", columnDefinition="VARCHAR(128)")
    private String currentTaskId;
    @Column(name="cur_task_name", columnDefinition="VARCHAR(128)")
    private String currentTaskName;
    @Column(name="cur_task_userid", columnDefinition="VARCHAR(128)")
    private String currentTaskAssigneeId;
    @Column(name="cur_task_username", columnDefinition="VARCHAR(128)")
    private String currentTaskAssigneeName;

    /**
     * 结束流程的人
     * 正常结束流程是system
     * @see com.abt.wf.config.Constants
     */
    @Column(columnDefinition="VARCHAR(128)")
    private String terminateId = Constants.TERMINATE_SYS;
    @Column(columnDefinition="VARCHAR(128)")
    private String terminateName = Constants.TERMINATE_SYS;
}
