package com.abt.oa.reposity;

import com.abt.oa.entity.CarInfo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/** 车辆档案查询与占用更新所需的行锁。 */
public interface CarInfoRepository extends JpaRepository<CarInfo, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CarInfo c WHERE c.id = :id")
    Optional<CarInfo> findLockedById(@Param("id") String id);

    @Query("SELECT c FROM CarInfo c WHERE (:isAvtive IS NULL OR c.isAvtive = :isAvtive) " +
            "AND (:pattern IS NULL OR c.carNo LIKE :pattern ESCAPE '!') ORDER BY c.carNo, c.id")
    List<CarInfo> findList(@Param("isAvtive") String isAvtive, @Param("pattern") String pattern);
}
