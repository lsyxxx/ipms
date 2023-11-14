package com.abt.flow.model;

import com.abt.common.model.User;
import com.abt.flow.model.entity.FlowScheme;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * 流程请求对象
 */
@Data
public class ApplyForm<T> {
    /**
     * 业务数据对象
     * 对应业务实体对象: Reimburse
     */
    @Valid
    private T data;

    private String service;

    /**
     * 业务类型
     */
    private FlowScheme flowScheme;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 部门主管
     */
    private User deptManager;

    /**
     * 技术负责人审批
     */
    private User techManager;

    /**
     * 指定人审批
     */
    private User assignee;

    /**
     * 申请人
     */
    private User applicant;


}
