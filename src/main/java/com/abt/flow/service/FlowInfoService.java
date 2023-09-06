package com.abt.flow.service;

import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.entity.BizFlowCategory;
import com.abt.flow.model.entity.BizFlowRelation;

import java.util.List;

/**
 * 流程信息相关
 */
public interface FlowInfoService {


    /**
     * 用户申请的所有流程(历史、正在进行的、删除的...)
     * @param userId
     * @param page
     * @param size
     * @param query 查询 customName.contains()
     * @return 默认根据创建日期排序
     */
    List<BizFlowRelation> getUserApplyFlows(String userId, int page, int size, String query);

    /**
     * 获取所有可用的业务类型 未删除&&已启用
     * @return
     */
    List<BizFlowCategory> findAllEnabled(int page, int size);


    List<BizFlowRelation> find(String userId, int page, int size, String query, String type);

    /**
     * 查询用户的待办业务
     * @param userId
     * @param page
     * @param size
     * @param query
     * @return
     */
    List<BizFlowRelation> getTodoFlows(String userId, int page, int size, String query);

    /**
     * 查看用户的已处理业务
     * @param userId
     * @param page
     * @param size
     * @param query
     * @return
     */
    List<BizFlowRelation> getInvokedFlows(String userId, int page, int size, String query);

    /**
     * 查看用户的流程
     * @param userId
     * @param page
     * @param size
     * @param query
     * @param type 申请/待办/已办 类型
     * @return
     */
    List<BizFlowRelation> getFlows(String userId, int page, int size, String query, String type);
}
