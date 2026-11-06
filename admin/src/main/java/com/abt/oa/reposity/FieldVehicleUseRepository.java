package com.abt.oa.reposity;

import com.abt.oa.entity.FieldVehicleUse;
import com.abt.oa.model.FieldVehicleLastMileage;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** 野外用车主表访问，不展开一对多明细进行分页。 */
public interface FieldVehicleUseRepository extends JpaRepository<FieldVehicleUse, String>, JpaSpecificationExecutor<FieldVehicleUse> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM FieldVehicleUse u WHERE u.id = :id")
    Optional<FieldVehicleUse> findLockedById(@Param("id") String id);

    @Query("SELECT u.carId FROM FieldVehicleUse u WHERE u.id = :id")
    Optional<String> findCarId(@Param("id") String id);

    boolean existsByCarIdAndStatus(String carId, Integer status);

    @Query("SELECT DISTINCT u.carId FROM FieldVehicleUse u WHERE u.carId IN :carIds AND u.status = :using")
    List<String> findUsingCarIds(@Param("carIds") Collection<String> carIds, @Param("using") int using);

    @Query("SELECT u FROM FieldVehicleUse u WHERE u.carId = :carId AND u.status = :finished " +
            "ORDER BY u.returnTime DESC, u.id DESC")
    List<FieldVehicleUse> findLatestEnded(@Param("carId") String carId, @Param("finished") int finished, Pageable pageable);

    @Query("SELECT new com.abt.oa.model.FieldVehicleLastMileage(u.carId, u.endMileage) " +
            "FROM FieldVehicleUse u WHERE u.carId IN :carIds AND u.status = :finished " +
            "AND NOT EXISTS (SELECT n.id FROM FieldVehicleUse n WHERE n.carId = u.carId AND n.status = :finished " +
            "AND (n.returnTime > u.returnTime OR (n.returnTime = u.returnTime AND n.id > u.id)))")
    List<FieldVehicleLastMileage> findLatestMileages(@Param("carIds") Collection<String> carIds, @Param("finished") int finished);
}
