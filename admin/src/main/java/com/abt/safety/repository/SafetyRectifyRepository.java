package com.abt.safety.repository;

import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.entity.SafetyRectify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SafetyRectifyRepository extends JpaRepository<SafetyRectify, Integer> {
  List<SafetyRectify> findByRecordId(String recordId);


  @Query("""
    select r from SafetyRectify r where r.recordId = :recordId and r.checkTime is null
""")
  Optional<SafetyRectify> findRunningRectify(String recordId);


  @Modifying
  @Query("""
    update SafetyRectify set rectifierId = :rectifierId, rectifierName = :rectifierName, dispatcherId = :dispatcherId, dispatcherName = :dispatcherName, dispatchTime = :dispatchTime  where id = :id
""")
  void updateRectifyUser(int id, String rectifierId, String rectifierName, String dispatcherId, String dispatcherName, LocalDateTime dispatchTime);
}