package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 报销流程业务数据
 */
@Getter
@Setter
public class Reimburse extends AuditInfo {

    private String id;
    private String processInstanceId;
    private String processDefId;
    private String processDefKey;
    private double cost;
    private String reason;
    private LocalDateTime applyDate;

}
