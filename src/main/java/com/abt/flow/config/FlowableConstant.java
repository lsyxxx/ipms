package com.abt.flow.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
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
    /**
     * 流程参数ProcessVariables中key
     * 下一个执行人
     */
    public static final String PV_NEXT_ASSIGNEE = "nextAssignee";
    public static final String PV_PROCESS_DESC = "processDesc";


    /**
     * 流程中参数Key, value统一为Form对象
     * TODO: 因为流程中参数flowable会保存到数据库，考虑最小参数。
     */
    public static final String PV_FORM = "form_";


}
