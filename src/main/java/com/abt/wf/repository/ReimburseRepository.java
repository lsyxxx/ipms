package com.abt.wf.repository;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReimburseRepository extends JpaRepository<Reimburse, String>, JpaSpecificationExecutor<Reimburse> {

    /**
     * 我的待办查询
     */
    @Query("select r from Reimburse r " +
            "left join fetch r.currentTask rt " +
            "left join fetch rt.tuser tu  " +
            "where (:userid is null or :userid = '' or rt.assignee = :userid) " +
            "and (:state is null or :state = '' or r.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.createUsername like %:query% " +
            "   or r.reason like %:query%) " +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) ")
    Page<Reimburse> findMyTodoPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select count(r) from Reimburse r " +
            "left join r.currentTask rt " +
            "where (:userid is null or :userid = '' or rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or  rt.taskDefKey = :taskDefKey) " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.createUsername like %:query% " +
            "   or r.reason like %:query%) "
    )
    int countMyTodo(String userid, String query, String taskDefKey);

    @Query("select r from Reimburse r " +
            "left join fetch r.currentTask rt " +
            "left join fetch rt.tuser tu  " +
            "where (:userid is null or :userid = '' or rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:state is null or :state = '' or r.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.createUsername like %:query% " +
            "   or r.reason like %:query%) " +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) " +
            "order by r.updateDate desc"
    )
    List<Reimburse> findUserTodoList(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, String taskDefKey);


    /**
     * 我创建的
     */
    @Query("select r from Reimburse r " +
            "left join fetch r.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or :userid = '' or r.createUserid = :userid) " +
            "and (:state is null or :state = '' or r.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.createUsername like %:query% " +
            "   or r.reason like %:query%) " +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) " +
            "order by r.updateDate desc"
    )
    Page<Reimburse> findMyApplyPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 我已处理
     * 删除正在进行的流程时，会完成当前正在进行的节点。实际该节点并未完成。
     */
    @Query("select r from Reimburse r " +
            "left join fetch r.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "left join fetch ActHiTaskInst ht on ht.procInstId = r.processInstanceId " +
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:state is null or :state = '' or r.businessState = :state) " +
            "and (:userid is null or :userid = '' or ht.assignee = :userid) " +
            "and ht.endTime is not null " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.createUsername like %:query% " +
            "   or r.reason like %:query%)" +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) ")
    Page<Reimburse> findMyDonePaged(String userid, String query, String state,  LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 所有
     */
    @Query("select r from Reimburse r " +
            "left join fetch r.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:state is null or :state = '' or r.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.createUsername like %:query% " +
            "   or r.reason like %:query%)" +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) ")
    Page<Reimburse> findAllByQueryPaged(String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);




}
