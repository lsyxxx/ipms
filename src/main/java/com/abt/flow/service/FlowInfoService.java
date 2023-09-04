package com.abt.flow.service;

import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.entity.BizFlowCategory;

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
     * @return 默认根据创建日期排序
     */
    List<ProcessVo> getUserApplyFlows(String userId, int page, int size);

    /**
     * 获取所有可用的业务类型 未删除&&已启用
     * @return
     */
    List<BizFlowCategory> findAllEnabled(int page, int size);
}
