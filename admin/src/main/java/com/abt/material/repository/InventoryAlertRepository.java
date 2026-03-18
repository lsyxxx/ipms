package com.abt.material.repository;

import com.abt.material.entity.InventoryAlert;
import com.abt.material.entity.InventoryId;
import jakarta.persistence.NamedEntityGraphs;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryAlertRepository extends JpaRepository<InventoryAlert, InventoryId> {

    @Query("select a from InventoryAlert a " +
            "left join fetch a.materialDetail " +
            "left join fetch  a.warehouse " +
            "where 1=1 " +
            "and ('all' in :warehouseIds or a.id.warehouseId = :warehouseId) " +
            "and (:nameLike is null or :nameLike = '' or a.materialName like %:nameLike%)"
    )
    Page<InventoryAlert> findByQueryPageable(List<String> warehouseIds, String nameLike, Pageable pageable);

    //逻辑同findByQueryPageable
    @Query("select a from InventoryAlert a " +
            "left join fetch a.materialDetail " +
            "left join fetch  a.warehouse " +
            "where 1=1 " +
            "and ('all' in :warehouseIds or a.id.warehouseId = :warehouseId) " +
            "and (:nameLike is null or :nameLike = '' or a.materialName like %:nameLike%) " +
            "order by a.sortNo, a.id.warehouseId, a.id.materialId")
    @NotNull
    List<InventoryAlert> findAllBy(List<String> warehouseIds, String nameLike);

    @EntityGraph(attributePaths = {"materialDetail", "materialDetail.materialType", "warehouse"})
    List<InventoryAlert> findById_MaterialId(String idMaterialId);
}