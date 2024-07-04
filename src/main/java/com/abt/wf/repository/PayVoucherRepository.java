package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.entity.PayVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface PayVoucherRepository extends JpaRepository<PayVoucher, String>, JpaSpecificationExecutor<PayVoucher> {

    /**
     * 我创建的
     * 审批编号/付款金额/合同名称/付款说明/申请人
     */
    @Query("select e from PayVoucher e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or :userid = '' or e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.payAmount) like %:query% " +
            "   or e.contractName like %:query% " +
            "   or e.payDesc like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<PayVoucher> findUserApplyByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 我已处理
     */
    @Query("select e from PayVoucher e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join fetch ActHiTaskInst ht on ht.procInstId = e.processInstanceId " +
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:userid is null or ht.assignee = :userid) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.payAmount) like %:query% " +
            "   or e.contractName like %:query% " +
            "   or e.payDesc like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<PayVoucher> findUserDoneByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 待办
     * 模糊搜索:开票金额/审批编号/客户名称
     */
    @Query("select e from PayVoucher e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.payAmount) like %:query% " +
            "   or e.contractName like %:query% " +
            "   or e.payDesc like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<PayVoucher> findUserTodoByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 所有
     * 审批编号/付款金额/合同名称/付款说明/申请人
     */
    @Query("select e from PayVoucher e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.payAmount) like %:query% " +
            "   or e.contractName like %:query% " +
            "   or e.payDesc like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<PayVoucher> findAllByQueryPaged(String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


}
