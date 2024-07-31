package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface FieldWorkRepository extends JpaRepository<FieldWork, String> {

    Page<FieldWork> findByCreateUseridAndAttendanceDate(String userid, LocalDate attendanceDate, Pageable pageable);


    //项目/井号/考勤人/补助名称/考勤人部门
    @Query("select DISTINCT fw from FieldWork fw " +
            "left join fetch fw.items fi " +
            "where 1=1 " +
            "and fw.reviewerId = :userid " +
            "and fw.reviewTime is null " +
            "and (:query is null or :query = '' " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
            "    or fw.departmentName like %:query% " +
            "    or fw.username like %:query% " +
            "    or fi.allowanceName like %:query%) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL or fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL or fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
            )
    Page<FieldWork> findTodoFetchedByQuery(String query, String userid, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);

    //项目/井号/考勤人/补助名称/考勤人部门
    @Query("select DISTINCT fw from FieldWork fw " +
            "left join fetch fw.items fi " +
            "where 1=1 " +
            "and fw.reviewerId = :userid " +
            "and fw.reviewTime is not null " +
            "and (:query is null or :query = '' " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
            "    or fw.departmentName like %:query% " +
            "    or fw.username like %:query% " +
            "    or fi.allowanceName like %:query%) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL or  fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL OR  fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
    )
    Page<FieldWork> findDoneFetchedByQuery(String query, String userid, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);


    @Query("select DISTINCT fw from FieldWork fw " +
            "left join fetch fw.items fi " +
            "where 1=1 " +
            "and fw.createUserid = :userid " +
            "and (:query is null or :query = '' " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
            "    or fw.departmentName like %:query% " +
            "    or fw.username like %:query%" +
            "    or fi.allowanceName like %:query%) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL or   fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL or  fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
    )
    Page<FieldWork> findApplyFetchedByQuery(String query, String userid, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);


    //项目/井号/考勤人/补助名称/考勤人部门/审批人
    @Query("select DISTINCT fw from FieldWork fw " +
            "left join fetch fw.items fi " +
            "where 1=1 " +
            "and (:query is null or :query = '' " +
            "    or fw.createUsername like %:query% " +
            "    or fw.reviewerName like %:query% " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
            "    or fw.departmentName like %:query% " +
            "    or fw.username like %:query%" +
            "    or fi.allowanceName like %:query%) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL OR  fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL OR fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
    )
    Page<FieldWork> findAllFetchedByQuery(String query, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<FieldWork> findByJobNumberAndReviewResultAndAttendanceDateBetweenOrderByAttendanceDate(String jobNumber, String result, LocalDate startDate, LocalDate endDate);
}