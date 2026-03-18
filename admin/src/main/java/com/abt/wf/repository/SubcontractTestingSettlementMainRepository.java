package com.abt.wf.repository;

import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.entity.SubcontractTestingSettlementMain;
import com.abt.wf.model.SbctSummaryData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SubcontractTestingSettlementMainRepository extends JpaRepository<SubcontractTestingSettlementMain, String> {

    @Query("""
    select e from SubcontractTestingSettlementMain e
    left join fetch e.currentTask rt
    left join fetch rt.tuser tu
    where (e.createUserid = :userid)
    and (:state is null or :state = '' or e.businessState = :state)
    and (:query is null or :query = '' or e.id like %:query% or e.subcontractCompany like %:query%)
    and (:startDate is null or e.createDate >= :startDate)
    and (:endDate is null or e.createDate < :endDate)
""")
    Page<SubcontractTestingSettlementMain> findMyApplyPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    @Query("""
    select e from SubcontractTestingSettlementMain e
    left join fetch e.currentTask rt
    left join fetch rt.tuser tu
    where 1=1
    and (:userid is null or :userid = '' or e.createUserid = :userid)
    and (:state is null or :state = '' or e.businessState = :state)
    and (:query is null or :query = '' or e.id like %:query% or e.createUsername like %:query% or e.subcontractCompany like %:query%)
    and (:startDate is null or e.createDate >= :startDate)
    and (:endDate is null or e.createDate < :endDate)
""")
    Page<SubcontractTestingSettlementMain> findAllByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("""
    select e from SubcontractTestingSettlementMain e
    left join fetch e.currentTask rt
    left join fetch rt.tuser tu
    where rt.assignee = :userid
    and (:state is null or :state = '' or e.businessState = :state)
    and (:query is null or :query = '' or e.id like %:query% or e.createUsername like %:query% or e.subcontractCompany like %:query%)
    and (:startDate is null or e.createDate >= :startDate)
    and (:endDate is null or e.createDate < :endDate)
""")
    Page<SubcontractTestingSettlementMain> findMyTodoPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("""
    select count(e) from SubcontractTestingSettlementMain e
    left join e.currentTask rt
    where rt.assignee = :userid
    and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)
    and (:query is null or :query = '' or e.id like %:query% or e.createUsername like %:query% or e.subcontractCompany like %:query%)
""")
    int countTodoByQuery(String userid, String query, String taskDefKey);

    @Query("""
    select e from SubcontractTestingSettlementMain e
    left join fetch e.currentTask rt
    left join fetch rt.tuser tu
    where rt.assignee = :userid
    and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)
    and (:state is null or :state = '' or e.businessState = :state)
    and (:query is null or :query = '' or e.id like %:query% or e.createUsername like %:query% or e.subcontractCompany like %:query%)
    and (:startDate is null or e.createDate >= :startDate)
    and (:endDate is null or e.createDate < :endDate)
""")
    List<SubcontractTestingSettlementMain> findUserTodoList(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, String taskDefKey);

    @Query("""
    select e from SubcontractTestingSettlementMain e
    left join fetch e.currentTask rt
    left join fetch rt.tuser tu
    left join fetch ActHiTaskInst ht on ht.procInstId = e.processInstanceId
    where lower(ht.taskDefKey) not like '%apply%'
    and (:userid is null or :userid = '' or ht.assignee = :userid)
    and (:state is null or :state = '' or e.businessState = :state)
    and (:query is null or :query = '' or e.id like %:query% or e.createUsername like %:query% or e.subcontractCompany like %:query%)
    and (:startDate is null or e.createDate >= :startDate)
    and (:endDate is null or e.createDate < :endDate)
""")
    Page<SubcontractTestingSettlementMain> findMyDonePaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    @Query("""
    select e from SubcontractTestingSettlementMain e
    left join fetch e.details
    left join fetch e.currentTask rt
    where e.id = :id
""")
    SubcontractTestingSettlementMain findWithDetails(String id);


    @Query("""
    select new com.abt.wf.model.SbctSummaryData(d.entrustId, d.checkModuleId, d.checkModuleName, count(d.id), d.price, sum(d.price))
    from SubcontractTestingSettlementDetail d
    where d.main.id = :mid
    group by d.entrustId, d.checkModuleId, d.checkModuleName, d.price
""")
    List<SbctSummaryData> getSummaryData(String mid);

}