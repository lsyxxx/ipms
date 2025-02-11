package com.abt.material.repository;

import com.abt.material.entity.StockOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockOrderRepository extends JpaRepository<StockOrder, String> {

    @EntityGraph(value = "StockOrder.withStock", type = EntityGraph.EntityGraphType.LOAD)
    Optional<StockOrder> findWithStockListById(String id);


}