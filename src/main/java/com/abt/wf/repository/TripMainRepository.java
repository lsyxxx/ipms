package com.abt.wf.repository;

import com.abt.wf.entity.TripMain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TripMainRepository extends JpaRepository<TripMain, String> {


    //审批编号/出差人员/总金额/申请人,状态,创建时间
    @Query("select e from TripMain e " +
            "left join fetch e.details  " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (:userid is null or :userid = '' or e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or FUNCTION('STR', e.sum) like %:query% " +
            "   or e.staff like %:query% " +
            "   or e.createUsername like %:query%) " +
            "AND (:startDate IS NULL OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.createDate <= :endDate) ")
    Page<TripMain> findUserApplyByQueryPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);




}