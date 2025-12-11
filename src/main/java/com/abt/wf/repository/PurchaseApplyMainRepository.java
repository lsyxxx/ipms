package com.abt.wf.repository;

import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseSummaryAmount;
import org.apache.poi.ss.usermodel.PageMargin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseApplyMainRepository extends JpaRepository<PurchaseApplyMain, String> {

    @Query("SELECT p FROM PurchaseApplyMain p LEFT JOIN FETCH p.details WHERE p.id = :id ")
    PurchaseApplyMain findByIdWithDetails(String id);


    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join PurchaseApplyDetail dtl on e.id = dtl.main.id " +
            "where (e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or dtl.name like %:query%" +
            "   ) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findMyApplyPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join PurchaseApplyDetail dtl on e.id = dtl.main.id " +
            "where 1=1 " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or dtl.name like %:query%" +
            "   ) " +
            "and (:userid is null or :userid = '' or e.createUserid = :userid) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findAllByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join PurchaseApplyDetail dtl on e.id = dtl.main.id " +
            "where (rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or dtl.name like %:query%" +
            "   ) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findMyTodoPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select count(distinct e) from PurchaseApplyMain e " +
            "left join e.currentTask rt " +
            "left join PurchaseApplyDetail dtl on e.id = dtl.main.id " +
            "where (rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or dtl.name like %:query%" +
            "   ) "
    )
    int countTodoByQuery(String userid, String query, String taskDefKey);

    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join PurchaseApplyDetail dtl on e.id = dtl.main.id " +
            "where (rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or dtl.name like %:query%" +
            "   ) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) " +
            "order by e.updateDate desc "
    )
    List<PurchaseApplyMain> findUserTodoList(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, String taskDefKey);


    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join ActHiTaskInst ht on ht.procInstId = e.processInstanceId " +
            "left join PurchaseApplyDetail dtl on e.id = dtl.main.id " +
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:userid is null or :userid = '' or ht.assignee = :userid) " +
            "and (ht.endTime is not null) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or dtl.name like %:query%" +
            "   ) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findMyDonePaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


}