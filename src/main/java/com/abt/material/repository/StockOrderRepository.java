package com.abt.material.repository;

import com.abt.material.entity.StockOrder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockOrderRepository extends JpaRepository<StockOrder, String> {

    /**
     * 查询所有关联的属性
     * @param id 实体id
     */
    @EntityGraph(attributePaths = {"warehouse", "stockList"})
    @Query("select s from StockOrder s where s.id = :id")
    Optional<StockOrder> findByIdWithAllDetail(String id);


}