package com.abt.material.repository;

import com.abt.material.entity.Stock;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface StockRepository extends JpaRepository<Stock, String> {

    @Query("select st from Stock st " +
            "left join fetch StockOrder so on st.orderId = so.id " +
            "where (:stockType is null or so.stockType = :stockType) " +
            "and (:query is null or :query = '' or st.orderId like %:query% or st.materialName like %:query%) " +
            "and (:startDate is null or so.orderDate >= :startDate) " +
            "and (:endDate is null or so.orderDate <= :endDate) " +
            "order by so.orderDate, st.materialName, so.stockType")
    Page<Stock> findByQueryPageable(String query, Integer stockType, String startDate, String endDate, Pageable pageable);

    void deleteByOrderId(@NotNull @Size(max = 64) String orderId);
}