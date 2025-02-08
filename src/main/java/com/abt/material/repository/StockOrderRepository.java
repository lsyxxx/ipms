package com.abt.material.repository;

import com.abt.material.entity.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockOrderRepository extends JpaRepository<StockOrder, String> {
}