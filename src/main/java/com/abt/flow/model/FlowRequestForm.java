package com.abt.flow.model;

import com.abt.common.model.RequestForm;
import lombok.Data;

/**
 * 流程查询表单
 */
@Data
public class FlowRequestForm extends RequestForm {
    /**
     * 审批状态
     */
    private String state;
    /**
     * 审批结果
     */
    private String result;
    /**
     * 流程创建人
     */
    private String starter;

}
