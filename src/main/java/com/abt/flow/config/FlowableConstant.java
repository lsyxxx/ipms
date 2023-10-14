package com.abt.flow.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程相关常量
 * ProcessVariable key
 */
@Configuration
@Data
public class FlowableConstant {

    /**
     * 流程图字体
     */
    @Value("${abt.flowable.diagram.font}")
    private String diagramFont;

    /**
     * 流程图比例
     */
    @Value("${abt.flowable.diagram.scaleFactor}")
    private double scaleFactor;


    //-------------- 流程参数设置
    /**
     * 流程参数ProcessVariables中key
     * customName 自定义流程名称
     */
    public static final String PV_CUSTOM_NAME = "customName";
    public static final String PV_PROCESS_DESC = "processDesc";


    //-------------- 流程业务类型相关
    /**
     * 业务类型
     */
    public static final String PV_BIZ_CODE = "businessType";
    public static final String PV_BIZ_ID = "businessId";
    public static final String PV_BIZ_NAME = "businessName";

    /**
     * 历史审批人
     */
    public static final String PV_HIS_INVOKERS = "historyAuditors";
    /**
     * 流程申请人
     */
    public static final String PV_APPLICANT = "applicant";


    //----------------- 审批流程常用角色
    /**
     * 部门主管assignee key USER_ID
     * 对应bpmn文件flowable:assignee
     */
    public static final String PV_DEPT_MANAGER = "deptManager";
//    public static final String PV_DEPT_MANAGER_CODE = "deptManager_code";
//    public static final String PV_DEPT_MANAGER_NAME = "deptManager_name";

    /**
     * 技术负责人assignee key
     * 对应bpmn文件flowable:assignee
     */
    public static final String PV_TECH_MANAGER = "techManager";
//    public static final String PV_TECH_MANAGER_CODE = "techManager_code";
//    public static final String PV_TECH_MANAGER_NAME = "techManager_name";
    /**
     * 总经理
     */
    public static final String PV_CEO = "ceo";
//    public static final String PV_CEO_CODE = "ceo_code";
//    public static final String PV_CEO_NAME = "ceo_name";
    /**
     * 财务主管
     */
    public static final String PV_FI_MANAGER = "fiManager";
//    public static final String PV_FI_MANAGER_CODE = "fiManager_code";
//    public static final String PV_FI_MANAGER_NAME = "fiManager_name";
    /**
     * 税务会计
     */
    public static final String PV_TAX_OFFICER = "taxOfficer";
//    public static final String PV_TAX_OFFICER_CODE = "taxOfficer_code";
//    public static final String PV_TAX_OFFICER_NAME = "taxOfficer_name";
    /**
     * 财务会计
     */
    public static final String PV_ACCOUNTANCY = "accountancy";
//    public static final String PV_ACCOUNTANCY_CODE = "accountancy_code";
//    public static final String PV_ACCOUNTANCY_NAME = "accountancy_name";
    /**
     * 流程参数ProcessVariables中key
     * 下一个执行人
     */
    public static final String PV_NEXT_ASSIGNEE = "nextAssignee";
//    public static final String PV_NEXT_ASSIGNEE_CODE = "nextAssignee_code";
//    public static final String PV_NEXT_ASSIGNEE_NAME = "nextAssignee_name";


    /**
     * 出纳
     */
    public static final String PV_CASHIER = "cashier";





    //----------------- 流程常用表单
    /**
     * 保存在流程参数(processVariables中的form表单key, 对应bpmn文件flowable:assignee
     * 流程中参数Key, value统一为Form对象
     * 因为流程中参数flowable会保存到数据库，最好不保存过大数据。
     */
    public static final String PV_FORM = "applyForm";

    //----------------- 审批状态
    /**
     * 审批状态
     */
    public static final String PV_AUDIT_STATE = "auditState";

    /**
     * 审批结果
     */
    public static final String PV_AUDIT_RESULT = "auditResult";

    /**
     * 表单模板ID
     */
    public static final String PV_FORM_ID = "formId";



    //-----------------------------------
    //----------- 报销流程
    //-----------------------------------

    /**
     * 业务相关
     * 业务描述：报销-报销事由，等
     */
    public static final String PV_DES = "description";

    /**
     * 流程对应服务
     */
    public static final String PV_SERVICE = "service";

    /**
     * 报销费用
     */
    public static final String PV_COST = "cost";


}
