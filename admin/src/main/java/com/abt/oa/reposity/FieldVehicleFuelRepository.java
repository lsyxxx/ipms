package com.abt.oa.reposity;

import com.abt.oa.entity.FieldVehicleFuel;
import com.abt.oa.model.FieldVehicleFuelSummary;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/** 加油明细及完整有效记录的聚合，空日期行同样参与汇总。 */
public interface FieldVehicleFuelRepository extends JpaRepository<FieldVehicleFuel, String> {
    boolean existsByMainId(String mainId);

    Optional<FieldVehicleFuel> findByIdAndMainId(String id, String mainId);

    @Query("SELECT f FROM FieldVehicleFuel f WHERE f.mainId = :mainId " +
            "ORDER BY CASE WHEN f.fuelDate IS NULL THEN 1 ELSE 0 END, f.fuelDate, f.createDate, f.id")
    List<FieldVehicleFuel> findItems(@Param("mainId") String mainId);

    @Query("SELECT new com.abt.oa.model.FieldVehicleFuelSummary(SUM(f.fuelQuantity), SUM(f.amount), COUNT(f.id), MAX(f.fuelDate)) " +
            "FROM FieldVehicleFuel f WHERE f.mainId = :mainId")
    FieldVehicleFuelSummary findSummary(@Param("mainId") String mainId);

}
