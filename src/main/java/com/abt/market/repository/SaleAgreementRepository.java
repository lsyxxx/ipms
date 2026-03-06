package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import com.abt.sys.model.CountQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface SaleAgreementRepository extends JpaRepository<SaleAgreement, String>, JpaSpecificationExecutor<SaleAgreement> {
    // **
    // 财务和结算等其他关联模块极度依赖的方法
    // **
    List<SaleAgreement> findByIdIsIn(List<String> ids);
    // **
    // 底层 SQL 范围查询方法（包含处理缺月的日期逻辑，10个参数）
    // **
    @Query("select s from SaleAgreement s where " +
            "(:query is null or :query = '' " +
            "or s.code like CONCAT('%', :query, '%') " +
            "or s.name like CONCAT('%', :query, '%') " +
            "or s.partyA like CONCAT('%', :query, '%') " +
            "or FUNCTION('STR', s.amount) like CONCAT('%', :query, '%')) " +
            "and (:contractType is null or :contractType = '' or s.type = :contractType) " +
            "and (:partyA is null or :partyA = '' or s.partyA = :partyA) " +
            "and (:partyB is null or :partyB = '' or s.partyB = :partyB) " +
            "and (:attribute is null or :attribute = '' or s.attribute = :attribute) " +
            "and (:createDateStart is null or s.createDate >= :createDateStart) " +
            "and (:createDateEnd is null or s.createDate < :createDateEnd) " +
            "and (:signDateStartInt is null or s.signYear is null or (s.signYear * 10000 + COALESCE(s.signMonth, 12) * 100 + COALESCE(s.signDay, 31)) >= :signDateStartInt) " +
            "and (:signDateEndInt is null or s.signYear is null or (s.signYear * 10000 + COALESCE(s.signMonth, 1) * 100 + COALESCE(s.signDay, 1)) < :signDateEndInt)")
    Page<SaleAgreement> findByQuery(
            @Param("query") String query,
            @Param("contractType") String contractType,
            @Param("partyA") String partyA,
            @Param("partyB") String partyB,
            @Param("attribute") String attribute,
            @Param("createDateStart") LocalDateTime createDateStart,
            @Param("createDateEnd") LocalDateTime createDateEnd,
            @Param("signDateStartInt") Integer signDateStartInt,
            @Param("signDateEndInt") Integer signDateEndInt,
            Pageable pageable
    );
    // 根据创建时间范围查询（用于本周/本月/今年新增统计）
    List<SaleAgreement> findByCreateDateBetweenOrderByCreateDateDesc(LocalDateTime start, LocalDateTime end);
    // 根据创建时间范围统计数量（用于看板统计）
    int countByCreateDateBetween(LocalDateTime start, LocalDateTime end);
    // 统计所有去重后的甲方数量（用于看板统计）
    @Query("select count(distinct s.partyA) from SaleAgreement s")
    int countAllDistinctByPartyA();
    // 统计合同最多的甲方客户（用于看板统计）
    @Query("select new com.abt.sys.model.CountQuery(s.partyA, count(s.id)) from SaleAgreement s group by s.partyA order by count(s.id) desc")
    List<CountQuery> findPartyAWithMostContracts(Pageable pageable);
}