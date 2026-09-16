package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface FieldWorkRepository extends JpaRepository<FieldWork, String> {

    //项目/井号/考勤人/补助名称/考勤人部门
    @Query("select fw from FieldWork fw " +
            "where fw.isDeleted = false " +
            "and fw.reviewerId = :userid " +
            "and fw.reviewTime is null " +
            "and (:query is null or :query = '' " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
//            "    or fw.departmentName like %:query% " +
            "    or fw.username like %:query% " +
            "    or exists (select fi.id from FieldWorkItem fi " +
            "               where fi.fid = fw.id and fi.allowanceName like %:query%)) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL or fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL or fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
            )
    Page<FieldWork> findTodoByQuery(String query, String userid, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);

    //项目/井号/考勤人/补助名称/考勤人部门
    @Query("select fw from FieldWork fw " +
            "where fw.isDeleted = false " +
            "and fw.reviewerId = :userid " +
            "and fw.reviewTime is not null " +
            "and (:query is null or :query = '' " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
//            "    or fw.departmentName like %:query% " +
            "    or fw.username like %:query% " +
            "    or exists (select fi.id from FieldWorkItem fi " +
            "               where fi.fid = fw.id and fi.allowanceName like %:query%)) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL or  fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL OR  fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
    )
    Page<FieldWork> findDoneByQuery(String query, String userid, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);


    @Query("select fw from FieldWork fw " +
            "where fw.isDeleted = false " +
            "and fw.createUserid = :userid " +
            "and (:query is null or :query = '' " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
//            "    or fw.departmentName like %:query% " +
            "    or fw.username like %:query%" +
            "    or exists (select fi.id from FieldWorkItem fi " +
            "               where fi.fid = fw.id and fi.allowanceName like %:query%)) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL or   fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL or  fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
    )
    Page<FieldWork> findApplyByQuery(String query, String userid, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);


    @Query("select fw from FieldWork fw " +
            "where fw.userid = :userid " +
            "and fw.isDeleted = false " +
            "and (:query is null or :query = '' " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
            "    or fw.username like %:query%" +
            "    or exists (select fi.id from FieldWorkItem fi " +
            "               where fi.fid = fw.id and fi.allowanceName like %:query%)) " +
            "and (:state is null or  :state = '' or fw.reviewResult = :state) " +
            "and (:startDate IS NULL or   fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL or  fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate asc "
    )
    Page<FieldWork> findAtdByQuery(String query, String userid, String state, LocalDate startDate, LocalDate endDate, Pageable pageable);


    //项目/井号/考勤人/补助名称/考勤人部门/审批人
    @Query("select fw from FieldWork fw " +
            "where 1=1 " +
            "and (:query is null or :query = '' " +
            "    or fw.createUsername like %:query% " +
            "    or fw.reviewerName like %:query% " +
            "    or fw.project like %:query% " +
            "    or fw.well like %:query% " +
//            "    or fw.departmentName like %:query% " +
//            "    or fw.username like %:query%" +
            "    or exists (select fi.id from FieldWorkItem fi " +
            "               where fi.fid = fw.id and fi.allowanceName like %:query%)) " +
            "and (:username is null or :username = '' or fw.username = :username)" +
            "and (:ignoreState = true or fw.reviewResult in :states) " +
            "and (:startDate IS NULL OR  fw.attendanceDate >= :startDate) " +
            "and (:endDate IS NULL OR fw.attendanceDate <= :endDate) " +
            "order by fw.attendanceDate desc, fw.createDate desc "
    )
    Page<FieldWork> findAllByQuery(String username, String query, boolean ignoreState, Collection<String> states,
                                   LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 批量加载当前页野外考勤及补助明细，避免一对多 fetch join 导致主查询内存分页。
     */
    @Query("select distinct fw from FieldWork fw left join fetch fw.items where fw.id in :ids")
    List<FieldWork> findAllWithItemsByIdIn(Collection<String> ids);

    List<FieldWork> findByJobNumberAndAttendanceDateBetween(String jobNumber, LocalDate startDate, LocalDate endDate);

    List<FieldWork> findByReviewerIdAndAttendanceDateBetween(String reviewerId, LocalDate startDate, LocalDate endDate);


    /**
     * 根据用户信息查询野外考勤
     * @param jobNumber - 查询该工号用户的考勤
     * @param userDept - 查询该部门的所有用户的考勤
     * @param company - 查询公司列表中所有的用户考勤
     * @param startDate - 考勤开始日期（包含）
     * @param endDate - 考勤结束日期（包含）
     */
    @Query("select fw from FieldWork fw " +
            "left join EmployeeInfo e on fw.jobNumber = e.jobNumber" +
            " join fetch fw.items fi " +
            "where 1=1 " +
            "and (:jobNumber is null or :jobNumber = '' or fw.jobNumber = :jobNumber)" +
            "and (:userDept is null or :userDept = '' or e.dept = :userDept) " +
            "and (:company is null or :company = '' or e.company = :company) " +
            "and fw.attendanceDate >= :startDate " +
            "and fw.attendanceDate <= :endDate")
    List<FieldWork> findRecordsByUserInfo(String jobNumber, String userDept, String company, LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query("update FieldWork fw set fw.isDeleted = true where fw.id = :id")
    void softDeleteById(String id);

    List<FieldWork> findByAttendanceDateAndUserid(LocalDate attendanceDate, String userid);

}
