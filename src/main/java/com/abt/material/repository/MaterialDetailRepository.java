package com.abt.material.repository;

import com.abt.material.entity.MaterialDetail;
import com.abt.material.model.IMaterialDetailDTO;
import com.abt.material.model.MaterialDetailDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaterialDetailRepository extends JpaRepository<MaterialDetail, String> {

    @Query("select m from MaterialDetail m " +
            "where 1=1 " +
            "and (:deptId is null or :deptId = '' or m.deptId = :deptId) " +
            "or (m.deptId is null or m.deptId = '')"
    )
    Page<MaterialDetail> findByQueryPageable(String deptId, Pageable pageable);

    @EntityGraph(attributePaths = {"materialType"})
    @NotNull
    List<MaterialDetail> findAll(@NotNull Sort sort);


    @Query("select m as materialDetail, i as inventory, w as warehouse " +
            "from MaterialDetail m " +
            "left join fetch m.materialType " +
            "left join Inventory i on m.id = i.materialId " +
            "left join Warehouse w on w.id = i.warehouseId " +
            "where 1=1 " +
            "and (i.updateDate = (select max(i2.updateDate) from Inventory i2 where i2.materialId = m.id) or i.updateDate is null) " +
            "and ('all' in :materialTypeIds or  m.materialTypeId in :materialTypeIds) " +
            "and ('all' in :warehouseIds or w.id in :warehouseIds) " +
            "order by m.materialTypeId, m.name, i.warehouseId")
    List<IMaterialDetailDTO> findAllWithInventories(List<String> materialTypeIds, List<String> warehouseIds);
}