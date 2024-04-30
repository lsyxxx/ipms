package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 发票冲账
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InvoiceOffsetRequestForm extends RequestForm {

    /**
     * 合同名称
     */
    private String contractName;
    private String taskDefKey;
    private String procDefKey;
    /**
     * 0： ALL
     * 1： TODO_
     * 2. DONE
     */
    private int queryMode = 0;
}
