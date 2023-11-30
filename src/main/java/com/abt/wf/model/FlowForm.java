package com.abt.wf.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 流程表单基类
 */
@Data
@Accessors(chain = true)
public class FlowForm {

    /**
     * 流程定义id
     */
    private String processDefId;
    private String processDefKey;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程启动人
     */
    private String starter;

    /**
     * 流程启动时间
     */
    private LocalDateTime startTime;


}
