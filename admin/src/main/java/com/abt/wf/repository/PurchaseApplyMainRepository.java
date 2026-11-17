package com.abt.wf.repository;

import com.abt.sys.model.dto.OrgDTO;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseSummaryAmount;
import org.apache.poi.ss.usermodel.PageMargin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseApplyMainRepository extends JpaRepository<PurchaseApplyMain, String> {

    /** 只扫描主表编号；从表关键字与已办记录使用 EXISTS，游标分页避免展开明细。 */
    @Query("""
            SELECT DISTINCT e.id FROM PurchaseApplyMain e LEFT JOIN e.currentTask rt
            WHERE e.id > :afterId AND e.createDate <= :snapshotTime
              AND (:state IS NULL OR :state = '' OR e.businessState = :state)
              AND (:query IS NULL OR :query = '' OR e.id LIKE CONCAT('%', :query, '%')
                   OR e.createUsername LIKE CONCAT('%', :query, '%')
                   OR EXISTS (SELECT d.id FROM PurchaseApplyDetail d WHERE d.main.id = e.id AND d.name LIKE CONCAT('%', :query, '%')))
              AND (:startDate IS NULL OR e.createDate >= :startDate)
              AND (:endDate IS NULL OR e.createDate <= :endDate)
              AND ((:scope = 'MY' AND e.createUserid = :userid)
                OR (:scope = 'TODO' AND rt.assignee = :userid)
                OR (:scope = 'DONE' AND EXISTS (SELECT h.id FROM ActHiTaskInst h
                    WHERE h.procInstId = e.processInstanceId AND h.assignee = :userid
                      AND h.endTime IS NOT NULL AND LOWER(h.taskDefKey) NOT LIKE '%apply%'))
                OR (:scope = 'ALL' AND (:deptIgnore = TRUE OR e.createDeptId IN :deptList)))
            ORDER BY e.id
            """)
    Slice<String> findDocumentExportIds(String scope, String userid, String query, String state,
                                       boolean deptIgnore, List<String> deptList,
                                       LocalDateTime startDate, LocalDateTime endDate,
                                       LocalDateTime snapshotTime, String afterId, Pageable pageable);

    /** 按当前批次编号加载完整明细，调用方将 IN 参数限制在 100 个以内。 */
    @Query("SELECT DISTINCT e FROM PurchaseApplyMain e LEFT JOIN FETCH e.details WHERE e.id IN :ids")
    List<PurchaseApplyMain> findDocumentExportBatch(List<String> ids);

    @Query("SELECT p FROM PurchaseApplyMain p LEFT JOIN FETCH p.details WHERE p.id = :id ")
    PurchaseApplyMain findByIdWithDetails(String id);


    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (e.createUserid = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or exists (select dtl.id from PurchaseApplyDetail dtl " +
            "              where dtl.main.id = e.id and dtl.name like %:query%)" +
            "   ) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findMyApplyPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where 1=1 " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or exists (select dtl.id from PurchaseApplyDetail dtl " +
            "              where dtl.main.id = e.id and dtl.name like %:query%)" +
            "   ) " +
            "and (:userid is null or :userid = '' or e.createUserid = :userid) " +
            "and (:deptIgnore = true or e.createDeptId in :deptList) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findAllByQueryPaged(String userid, String query, String state, boolean deptIgnore, List<String> deptList, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or exists (select dtl.id from PurchaseApplyDetail dtl " +
            "              where dtl.main.id = e.id and dtl.name like %:query%)" +
            "   ) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findMyTodoPaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select count(distinct e) from PurchaseApplyMain e " +
            "left join e.currentTask rt " +
            "where (rt.assignee = :userid) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or exists (select dtl.id from PurchaseApplyDetail dtl " +
            "              where dtl.main.id = e.id and dtl.name like %:query%)" +
            "   ) "
    )
    int countTodoByQuery(String userid, String query, String taskDefKey);

    @Query("select distinct e from PurchaseApplyMain e " +
            "left join fetch e.currentTask rt " +
            "left join fetch rt.tuser tu " +
            "where (rt.assignee = :userid) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:taskDefKey is null or :taskDefKey = '' or rt.taskDefKey = :taskDefKey)" +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or exists (select dtl.id from PurchaseApplyDetail dtl " +
            "              where dtl.main.id = e.id and dtl.name like %:query%)" +
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
            "where lower(ht.taskDefKey) not like '%apply%' " +
            "and (:userid is null or :userid = '' or ht.assignee = :userid) " +
            "and (ht.endTime is not null) " +
            "and (:state is null or :state = '' or e.businessState = :state) " +
            "and (:query IS NULL OR :query = '' " +
            "   or e.id like %:query% " +
            "   or e.createUsername like %:query%" +
            "   or exists (select dtl.id from PurchaseApplyDetail dtl " +
            "              where dtl.main.id = e.id and dtl.name like %:query%)" +
            "   ) " +
            "AND (:startDate IS NULL  OR e.createDate >= :startDate) " +
            "AND (:endDate IS NULL  OR e.createDate <= :endDate) "
    )
    Page<PurchaseApplyMain> findMyDonePaged(String userid, String query, String state, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("""
    select distinct new com.abt.sys.model.dto.OrgDTO(e.createDeptId, o.name)
    from PurchaseApplyMain e
    left join Org o on e.createDeptId = o.id
    where e.createDeptId is not null
""")
    List<OrgDTO> findDeptAll();

}
