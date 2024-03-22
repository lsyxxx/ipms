package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 搜索条件：分页, 审批编号, 审批结果，审批状态，创建人，创建时间
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReimburseRequestForm extends RequestForm {

    private String createUserid;
    private String createUsername;

}
