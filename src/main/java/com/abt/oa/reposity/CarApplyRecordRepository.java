package com.abt.oa.reposity;

import com.abt.oa.entity.CarApplyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CarApplyRecordRepository extends JpaRepository<CarApplyRecord, String> {

    @Query("SELECT c FROM CarApplyRecord c WHERE " +
            "(:keyword is null or (c.carInfo.carNo like concat('%', :keyword, '%') or c.driver like concat('%', :keyword, '%'))) " +
            "and (:startDate IS NULL OR c.departureDate >= :startDate)  " +
            "and (:endDate IS NULL OR c.departureDate <= :endDate) " +
            "order by c.id asc")
    List<CarApplyRecord> findByKeywordAndDate(String keyword, LocalDateTime startDate, LocalDateTime endDate);


}