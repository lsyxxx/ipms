package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 借方记账
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DebitBookKeeping extends AuditInfo {

    /**
     * 关联业务id
     */
    private String businessId;

    /**
     * 业务表名(wf_rbs)
     */
    private String table;
}
