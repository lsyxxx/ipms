package com.abt.wf.repository;

import com.abt.wf.entity.TripMain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TripMainRepository extends JpaRepository<TripMain, String> {


    /**
     * 用户申请的
     */
    //审批编号/出差人员/总金额/申请人,状态,创建时间
    @Query("select e from TripMain e " +
//            "left join fetch e.details  " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.sum) like %:query% " +
//            "   or e.createUsername like %:query% " +
            "   or e.reason like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) "
            )
    Page<TripMain> findUserApplyByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select e from TripMain e " +
//            "left join fetch e.details  " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu  " +
            "where (:userid is null or :userid = '' or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.sum) like %:query% " +
            "   or e.createUsername like %:query% " +
            "   or e.reason like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<TripMain> findUserTodoByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select e from TripMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu  " +
            "where (:userid is null or :userid = '' or rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.sum) like %:query% " +
            "   or e.createUsername like %:query% " +
            "   or e.reason like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) " +
            "order by e.updateDate desc ")
    List<TripMain> findUserTodoList(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, String taskDefKey);

    @Query("select count(e) from TripMain e " +
            "left join e.currentTask rt " +
            "where (:userid is null or :userid = '' or rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.sum) like %:query% " +
            "   or e.createUsername like %:query% " +
            "   or e.reason like %:query%) "
    )
    int countTodoByQuery(String userid, String query, String taskDefKey);

    @Query("select e from TripMain e " +
//            "left join fetch e.details  " +
            "left join fetch ActHiTaskInst ht on ht.procInstId = e.processInstanceId " +
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:userid is null or :userid = '' or ht.assignee = :userid) " +
            "and (ht.endTime is not null) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.sum) like %:query% " +
            "   or e.reason like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<TripMain> findUserDoneByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select e from TripMain e " +
//            "left join fetch e.details  " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.sum) like %:query% " +
            "   or e.reason like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<TripMain> findAllByQueryPaged(String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    //包含当前任务
    @Query("select e from TripMain e " +
            "left join fetch e.details d " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where e.id = :id " +
            "order by d.sort asc")
    TripMain findWithCurrentTaskById(String id);



}