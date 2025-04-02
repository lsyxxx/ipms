package com.abt.wf.repository;

import com.abt.wf.entity.PayVoucher;
import com.abt.wf.entity.SubcontractTesting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SubcontractTestingRepository extends JpaRepository<SubcontractTesting, String> {

    /**
     * 所有
     */
    @Query("select r from SubcontractTesting r " +
            "left join fetch r.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:state is null or :state = '' or r.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.subcontractCompanyName like %:query% " +
            "   or r.createUsername like %:query% " +
            "   or r.reason like %:query%)" +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) ")
    Page<SubcontractTesting> findAllByQueryPaged(String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 我已处理
     */
    @Query("select e from SubcontractTesting e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join fetch ActHiTaskInst ht on ht.procInstId = e.processInstanceId " +
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (ht.assignee = :userid and ht.endTime is not null) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.subcontractCompanyName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<SubcontractTesting> findUserDoneByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 待办
     * 模糊搜索:开票金额/审批编号/委托客户
     */
    @Query("select e from SubcontractTesting e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.subcontractCompanyName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<SubcontractTesting> findUserTodoByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 我创建的
     * 审批编号/付款金额/合同名称/付款说明/申请人
     */
    @Query("select e from SubcontractTesting e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or :userid = '' or e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.subcontractCompanyName like %:query% " +
            ") " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<SubcontractTesting> findUserApplyByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select e from SubcontractTesting e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.subcontractCompanyName like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) " +
            "order by e.updateDate desc ")
    List<SubcontractTesting> findUserTodoList(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, String taskDefKey);


    @Query("select count(e) from SubcontractTesting e " +
            "left join e.currentTask rt " +
            "where (rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.subcontractCompanyName like %:query% " +
            "   or e.createUsername like %:query%" +
            "   ) "
    )
    int countTodoByQuery(String userid, String query, String taskDefKey);
}