package com.abt.finance.repository;

import com.abt.finance.entity.ReceivePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReceivePaymentRepository extends JpaRepository<ReceivePayment, String>, JpaSpecificationExecutor<ReceivePayment> {


    @EntityGraph(value = "ReceivePayment.withReference", type = EntityGraph.EntityGraphType.LOAD)
    Optional<ReceivePayment> findWithReferenceById(String id);


    @Query("select p from ReceivePayment p " +
            "where (:query is null or :query = '' " +
            "or p.createUsername like %:query% " +
            "or FUNCTION('STR', p.amount) like %:query% " +
            "or p.customerName like %:query% " +
            ") " +
            "order by p.createDate desc, p.createUserid, p.customerName")
    List<ReceivePayment> findByQuery(String query);


    @Query("select p from ReceivePayment p " +
            "where (:query is null or :query = '' " +
            "   or p.createUsername like %:query% " +
            "   or FUNCTION('STR', p.amount) like %:query% " +
            "   or p.customerName like %:query% ) " +
            "and (:notifyUserid is null or p.notifyStrings like %:notifyUserid%)" +
            "and (:receiveDateStart is null or p.receiveDate >= :receiveDateStart) " +
            "and (:receiveDateEnd is null or p.receiveDate <= :receiveDateEnd) "
    )
    Page<ReceivePayment> findByQuery(String query, String notifyUserid, LocalDate receiveDateStart, LocalDate receiveDateEnd, Pageable pageable);


}