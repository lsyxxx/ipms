package com.abt.finance.repository;

import com.abt.finance.entity.ReceivePayment;
import jakarta.persistence.NamedEntityGraph;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReceivePaymentRepository extends JpaRepository<ReceivePayment, String> {


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

}