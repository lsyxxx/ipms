package com.abt.flow.service;

import com.abt.common.model.RequestForm;
import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.model.entity.BizFlowRelation;

import java.util.List;

/**
 * 流程信息相关
 */
public interface FlowInfoService {


    /**
     * 用户申请的所有流程(历史、正在进行的、删除的...)
     * @return 默认根据创建日期排序
     */
    List<BizFlowRelation> getUserApplyFlows(RequestForm form);

    /**
     * 获取所有可用的业务类型 未删除&&已启用
     */
    List<FlowCategory> findAllEnabled(int page, int size);

    //不分页
    List<FlowCategory> findAllEnabled();

    /**
     * 查询用户的待办业务
     */
    List<BizFlowRelation> getTodoFlows(RequestForm form);

    /**
     * 查看用户的已处理业务
     */
    List<BizFlowRelation> getCompletedFlows(RequestForm form);

    /**
     * 查看用户的流程
     */
    List<BizFlowRelation> getFlows(RequestForm form);

}
