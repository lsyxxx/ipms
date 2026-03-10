package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import com.abt.sys.model.CountQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SaleAgreementRepository extends JpaRepository<SaleAgreement, String>, JpaSpecificationExecutor<SaleAgreement> {

    List<SaleAgreement> findByCreateDateBetweenOrderByCreateDateDesc(LocalDateTime startDate, LocalDateTime endDate);
    int countByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT new com.abt.sys.model.CountQuery(c.partyA, COUNT(c)) " +
            "FROM SaleAgreement c " +
            "GROUP BY c.partyA " +
            "ORDER BY COUNT(c) DESC")
    List<CountQuery> findPartyAWithMostContracts(Pageable pageable);

    @Query("SELECT COUNT(DISTINCT(c.partyA)) FROM SaleAgreement c ")
    int countAllDistinctByPartyA();

    @Query("SELECT new com.abt.sys.model.CountQuery(MONTH(sa.createDate), COUNT(sa))  " +
            "FROM SaleAgreement sa " +
            "WHERE YEAR(sa.createDate) = :currentYear " +
            "GROUP BY MONTH(sa.createDate) " +
            "ORDER BY MONTH(sa.createDate) ASC ")
    List<CountQuery> countContractsByYearMonth(@Param("currentYear") int currentYear);

    @Query("select s from SaleAgreement s where " +
            "(:query is null or :query = '' " +
            "   or s.code like %:query% " +
            "   or s.name like %:query% " +
            "   or s.partyA like %:query% " +
            "   or FUNCTION('STR', s.amount) like %:query%) " +
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

    List<SaleAgreement> findByIdIsIn(Collection<String> ids);

    
}