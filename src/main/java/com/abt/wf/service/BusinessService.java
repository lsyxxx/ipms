package com.abt.wf.service;

import com.abt.common.model.RequestForm;
import com.abt.wf.entity.TripReimburse;

import java.util.List;

/**
 * 业务相关
 * T: 查询条件
 * R: 返回数据对象实体
 */
public interface BusinessService<T extends RequestForm, R> {

    /**
     * 查询所有业务记录
     *
     * @param requestForm 查询条件
     */
    List<R> findAllByCriteriaPageable(T requestForm);

    /**
     * 查询我申请的
     * @param requestForm 查询条件
     */
    List<R> findMyApplyByCriteriaPageable(T requestForm);

    /**
     * 查询我的已办列表
     * criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
     */
    List<R> findMyDoneByCriteriaPageable(T requestForm);

    /**
     * 查询我的待办列表
     * criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
     */
    List<R> findMyTodoByCriteria(T requestForm);

    R saveEntity(R entity);
    R load(String entityId);
    String getEntityId(R entity);
    String getServiceName();
}