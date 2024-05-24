package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import com.abt.sys.model.CountQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
}