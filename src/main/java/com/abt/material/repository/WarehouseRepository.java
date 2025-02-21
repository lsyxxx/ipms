package com.abt.material.repository;

import com.abt.material.entity.Warehouse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    /**
     * 查询最大的sortNo，若无数据则返回0
     */
    @Query("select COALESCE(max(sortNo), 0) from Warehouse")
    Integer findMaxSortNo();

    List<Warehouse> findByName(String name);
}