package com.abt.wf.repository;

import com.abt.wf.entity.PurchaseApplyMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseApplyMainRepository extends JpaRepository<PurchaseApplyMain, String> {

    @Query("SELECT p FROM PurchaseApplyMain p LEFT JOIN FETCH p.details WHERE p.id = :id")
    PurchaseApplyMain findByIdWithDetails(String id);
}