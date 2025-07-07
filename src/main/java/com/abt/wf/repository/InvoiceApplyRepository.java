package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface InvoiceApplyRepository extends JpaRepository<InvoiceApply, String>, JpaSpecificationExecutor<InvoiceApply> {

    /**
     * 我创建的
     * 模糊搜索:开票金额/审批编号/客户名称
     */
    @Query("select e from InvoiceApply e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or :userid = '' or e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.clientName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceApply> findUserApplyByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 我已处理
     */
    @Query("select e from InvoiceApply e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join fetch ActHiTaskInst ht on ht.procInstId = e.processInstanceId " +
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:userid is null or ht.assignee = :userid) " +
            "and (ht.endTime is not null) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.clientName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceApply> findUserDoneByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 待办
     * 模糊搜索:开票金额/审批编号/客户名称
     */
    @Query("select e from InvoiceApply e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.clientName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceApply> findUserTodoByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select e from InvoiceApply e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.clientName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) " +
            "order by e.updateDate desc ")
    List<InvoiceApply> findUserTodoList(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, String taskDefKey);

    @Query("select count(e) from InvoiceApply e " +
            "left join e.currentTask rt " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.clientName like %:query% " +
            "   or e.createUsername like %:query%) "
    )
    int countMyTodo(String userid, String query, String taskDefKey);

    /**
     * 所有
     */
    @Query("select e from InvoiceApply e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.clientName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceApply> findAllByQueryPaged(String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<InvoiceApply> findByIdIsIn(List<String> ids);


    List<InvoiceApply> findByIdIn(Collection<String> ids);

    List<InvoiceApply> findBySettlementIdIn(Collection<String> settlementIds);

    List<InvoiceApply> findBySettlementId(String settlementId);
}
