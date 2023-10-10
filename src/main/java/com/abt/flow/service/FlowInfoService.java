package com.abt.flow.service;

import com.abt.common.model.RequestForm;
import com.abt.flow.model.FlowInfoVo;
import com.abt.flow.model.FlowRequestForm;
import com.abt.flow.model.entity.FlowCategory;
import org.flowable.engine.task.Comment;

import java.util.List;

/**
 * 流程信息相关
 */
public interface FlowInfoService {


    /**
     * 用户申请的所有流程(历史、正在进行的、删除的...)
     * @return 默认根据创建日期排序
     */
    List<FlowInfoVo> getUserApplyFlows(FlowRequestForm form);

    /**
     * 获取所有可用的业务类型 未删除&&已启用
     */
    List<FlowCategory> findAllEnabled(int page, int size);

    //不分页
    List<FlowCategory> findAllEnabled();

    /**
     * 查询用户的待办业务
     */
    List<FlowInfoVo> getTodoFlows(FlowRequestForm form);

    /**
     * 查看用户的已处理业务
     */
    List<FlowInfoVo> getCompletedFlows(FlowRequestForm form);

    /**
     * 查看用户的流程
     */
    List<FlowInfoVo> getFlows(FlowRequestForm form);


    List<Comment> getComments(String procId);
}
