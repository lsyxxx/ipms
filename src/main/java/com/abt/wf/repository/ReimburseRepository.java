package com.abt.wf.repository;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReimburseRepository extends JpaRepository<Reimburse, String>, JpaSpecificationExecutor<Reimburse> {

    /**
     * 我的待办查询
     */
    @Query("select r from Reimburse r left join fetch ActRuTask rt on r.processInstanceId = rt.procInstId " +
            "left join fetch TUser tu on rt.assignee = tu.id " +
            "where rt.assignee = :userid " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.reason like %:query%) " +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) ")
    Page<Reimburse> findMyTodoPaged(String userid, String query, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 我创建的
     */
    @Query("select r from Reimburse r left join fetch ActRuTask rt on r.processInstanceId = rt.procInstId " +
            "left join fetch TUser tu on rt.assignee = tu.id " +
            "where r.createUserid = :userid " +
            "and (:query IS NULL OR :query = '' " +
            "   or r.id like %:query% " +
            "   or FUNCTION('STR', r.cost) like %:query% " +
            "   or r.reason like %:query%) " +
            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.createDate <= :endDate) ")
    Page<Reimburse> findMyApplyPaged(String userid, String query, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    /**
     * 我已处理
     */
//    @Query("select r from Reimburse r " +
//            "left join fetch ActHiTaskInst ht on r.processInstanceId = ht.procInstId " +
//            "left join fetch TUser tu on ht.assignee = tu.id " +
//            "where r.createUserid = :userid " +
//            "and (:query IS NULL OR :query = '' " +
//            "   or r.id like %:query% " +
//            "   or FUNCTION('STR', r.cost) like %:query% " +
//            "   or r.reason like %:query%)" +
//            "AND (:startDate IS NULL OR r.createDate >= :startDate) " +
//            "AND (:endDate IS NULL OR r.createDate <= :endDate) ")
//    Page<Reimburse> findMyDonePaged(String userid, String query, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
