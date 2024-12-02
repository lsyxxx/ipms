package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceOffset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceOffsetRepository extends JpaRepository<InvoiceOffset, String>, JpaSpecificationExecutor<InvoiceOffset> {

    /**
     * 我创建的
     * 模糊搜索: 审批编号/供应商/发票金额/创建人
     */
    @Query("select e from InvoiceOffset e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or :userid = '' or e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.supplierName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceOffset> findUserApplyByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 我已处理
     * 模糊搜索: 审批编号/供应商/发票金额/创建人
     */
    @Query("select e from InvoiceOffset e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join fetch ActHiTaskInst ht on ht.procInstId = e.processInstanceId " +
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:userid is null or ht.assignee = :userid) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.supplierName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceOffset> findUserDoneByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 待办
     * 模糊搜索: 审批编号/供应商/发票金额/创建人
     */
    @Query("select e from InvoiceOffset e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.supplierName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceOffset> findUserTodoByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select e from InvoiceOffset e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.supplierName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) " +
            "order by e.updateDate desc ")
    List<InvoiceOffset> findUserTodoList(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, String taskDefKey);

    @Query("select count(e) from InvoiceOffset e " +
            "left join e.currentTask rt " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.supplierName like %:query% " +
            "   or e.createUsername like %:query%) "
    )
    int countTodoByQuery(String userid, String query, String taskDefKey);

    /**
     * 所有
     * 模糊搜索: 审批编号/供应商/发票金额/创建人
     */
    @Query("select e from InvoiceOffset e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.invoiceAmount) like %:query% " +
            "   or e.supplierName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<InvoiceOffset> findAllByQueryPaged(String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


}