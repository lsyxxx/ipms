package com.abt.flow.model.entity;

import com.abt.common.model.AuditInfo;
import com.abt.flow.model.ProcessState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * 流程业务数据表流所需流程数据, 流程业务数据表需要继承
 * 1. 创建/更新信息(AuditInfo), 表示流程实例的更新/创建
 * 2. 流程类型FlowCategory: id/name/code
 * 3. 流程实例相关: 实例id, 流程状态, 当前执行用户
 *
 */
@Data
@MappedSuperclass
public class FlowBusinessBase extends AuditInfo {

    /**
     * 流程实例id
     */
    @Schema(description = "流程实例Id")
    @Column(columnDefinition = "VARCHAR(128)")
    private String processInstanceId;

    /**
     * 流程实例状态
     * @see ProcessState
     */
    @Schema(description = "流程实例状态，参考processSate.value")
    @Column(columnDefinition = "VARCHAR(32)")
    private String state;

    /**
     * 当前任务用户
     */
    @Schema(description = "当前任务用户id")
    @Column(name = "cur_userid", columnDefinition = "VARCHAR(200)")
    private String currentUserid;

    @Schema(description = "当前任务用户name")
    @Column(name = "cur_username", columnDefinition = "VARCHAR(200)")
    private String currentUsername;

    @Schema(description = "当前任务用户code")
    @Column(name = "cur_usercode", columnDefinition = "VARCHAR(200)")
    private String currentUserCode;
    /**
     * 流程业务类型id
     */
    @Schema(description = "流程类型ID")
    @Column(name = "cat_id", columnDefinition = "VARCHAR(200)")
    private String categoryId;
    /**
     * 流程业务类型code
     */
    @Schema(description = "流程类型代码")
    @Column(name = "cat_code", columnDefinition = "VARCHAR(50)")
    private String categoryCode;
    /**
     * 流程业务类型name
     */
    @Schema(description = "流程类型NAMEs")
    @Column(name = "cat_name", columnDefinition = "VARCHAR(200)")
    private String categoryName;

    /**
     * 流程定义ID;
     */
    @Schema(description = "流程定义ID")
    @Column(columnDefinition = "VARCHAR(128)")
    private String processDefinitionId;




}
