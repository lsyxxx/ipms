package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.abt.wf.config.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
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
     * Contants.STATE_DETAIL_*
     */
    @Column(name="biz_state", columnDefinition="VARCHAR(128)")
    private String businessState;

    /**
     * 流程状态，为camunda流程状态
     * ACTIVE, COMPLETED, SUSPEND,...
     */
    @Column(name="proc_state", columnDefinition="VARCHAR(64)")
    private String processState;

    /**
     * 业务是否结束，区别流程
     */
    @Column(columnDefinition="BIT")
    private boolean isFinished;

    /**
     * 业务类型，如费用报销，开票确认
     */
    @Column(name="srv_name", columnDefinition="VARCHAR(64)")
    private String serviceName;


}
