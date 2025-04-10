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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            "left join fetch InventoryAlert a on i1.warehouseId =  a.id.warehouseId and i1.materialId = a.id.materialId " +
            "where 1=1" +
            "and i1.updateDate = (SELECT MAX(i2.updateDate) FROM Inventory i2 " +
            "                       WHERE i2.materialId = i1.materialId " +
            "                       AND i2.warehouseId = i1.warehouseId) " +
            "and ('all' in :materialTypeIds  or i1.materialDetail.materialTypeId in :materialTypeIds) " +
            "and ('all' in :warehouseIds or i1.warehouseId in :warehouseIds) " +
            "and (:showAlertOnly = false or i1.quantity < a.alertNum) " +
            "and (:name is null or :name = '' or i1.materialDetail.name like %:name%) " +
            "order by i1.warehouseId, i1.materialDetail.materialTypeId, i1.materialDetail.name"
    )
    Page<Inventory> findLatestInventory(List<String> materialTypeIds, List<String> warehouseIds, String name, boolean showAlertOnly, Pageable pageable);

    @EntityGraph(attributePaths = {"materialDetail", "warehouse"})
    List<Inventory> findByOrderId(String orderId);

    void deleteByOrderId(String orderId);


    @Query(value = """
        select top 1 * from stock_inventory where m_id = :materialId and wh_id = :whid and create_date <= :endDate  order by create_date desc
""", nativeQuery = true)
    Inventory findNearestBefore(LocalDate endDate, String materialId, String whid);
}