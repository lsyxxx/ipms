package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *  开票申请搜索表单
 *  合同名称，合同编号, 客户id， 客户name, 项目名称, 申请人，申请部门id, 申请部门name
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InvoiceApplyRequestForm extends RequestForm {

    private String contractNo;
    private String contractName;
    private String deptId;
    private String deptName;
    private String project;
    private String clientId;
    private String clientName;

}
