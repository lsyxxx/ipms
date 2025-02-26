package com.abt.material.repository;

import com.abt.material.entity.Inventory;
import com.abt.material.entity.MaterialDetail;
import jakarta.persistence.NamedEntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, String> {

    /**
     * 获取最新物品的库存信息
     * @param materialId 物品id
     * @param warehouseId 仓库id
     */
    @Query("select inv from Inventory inv " +
            "left join fetch inv.materialDetail " +
            "left join fetch inv.warehouse " +
            "where inv.materialId = :materialId " +
            "and inv.warehouseId = :warehouseId " +
            "and inv.updateDate = (select max(i2.updateDate) from Inventory i2 where i2.materialId = :materialId and i2.warehouseId = :warehouseId)")
    Optional<Inventory> findOneLatestInventory(String materialId, String warehouseId);


    //使用List<String> 实现in，is null会报错
    @Query("select i1 from Inventory i1 " +
            "left join fetch i1.materialDetail " +
            "left join fetch i1.warehouse " +
            "left join fetch i1.alert " +
            "where 1=1" +
            "and i1.updateDate = (SELECT MAX(i2.updateDate) FROM Inventory i2 " +
            "                       WHERE i2.materialId = i1.materialId " +
            "                       AND i2.warehouseId = i1.warehouseId) " +
            "and ('all' in :materialTypeIds  or i1.materialId in :materialTypeIds) " +
            "and ('all' in :warehouseIds or i1.warehouseId in :warehouseIds) " +
            "and (:name is null or :name = '' or i1.materialDetail.name like %:name%) "
    )
    Page<Inventory> findLatestInventory(List<String> materialTypeIds, List<String> warehouseIds, String name, Pageable pageable);

    @EntityGraph(attributePaths = {"materialDetail", "warehouse"})
    List<Inventory> findByOrderId(String orderId);

    void deleteByOrderId(String orderId);
}