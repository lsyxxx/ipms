package com.abt.flow.service;

import com.abt.common.model.RequestForm;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.entity.FlowCategory;

import java.util.List;

/**
 * 流程信息相关
 */
public interface FlowInfoService {


    /**
     * 用户申请的所有流程(历史、正在进行的、删除的...)
     *
     * @return 默认根据创建日期排序
     */
    List<FlowInfoVo> getUserApplyFlows(RequestForm form);

    /**
     * 获取所有可用的业务类型 未删除&&已启用
     */
    List<FlowCategory> findAllEnabled(int page, int size);

    //不分页
    List<FlowCategory> findAllEnabled();

    /**
     * 查询用户的待办业务
     */
    List<FlowInfoVo> getTodoFlows(RequestForm form);

    /**
     * 查看用户的已处理业务
     */
    List<FlowInfoVo> getCompletedFlows(RequestForm form);

    /**
     * 查看用户的流程
     */
    List<FlowInfoVo> getFlows(RequestForm form);

    /**
     * 根据id查询流程类型
     * @param id
     * @return
     */
    FlowCategory getFlowCategory(String id);

}
