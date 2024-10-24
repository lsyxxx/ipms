package com.abt.finance.repository;

import com.abt.finance.entity.CreditBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CreditBookRepository extends JpaRepository<CreditBook, String>, JpaSpecificationExecutor<CreditBook> {

  @EntityGraph(value = "CreditBook.all", type = EntityGraph.EntityGraphType.FETCH)
  Page<CreditBook> findAll(Specification<CreditBook> specification, Pageable pageable);

//  List<CreditBook> findByBusinessIdOrderByCreateDateDesc(String businessId);


}