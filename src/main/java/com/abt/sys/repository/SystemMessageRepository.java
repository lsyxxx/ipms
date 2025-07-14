package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemMessage;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SystemMessageRepository extends JpaRepository<SystemMessage, String> {

    @Query("""
    select s from SystemMessage s
    where (s.toId = :toId)
    and (:toStatus is null or s.toStatus = :toStatus)
    and ('all' in :typeIds or s.typeId in :typeIds)
    and (:startDate is null or s.createTime >= :startDate)
    and (:endDate is null or s.createTime <= :endDate)
""")
    Page<SystemMessage> findAllBy(String toId, List<String> typeIds, Integer toStatus, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    @Query("update SystemMessage s set s.toStatus = 1, s.readTime = :readTime  where s.id = :id")
    @Modifying
    @Transactional
    void updateReadById(LocalDateTime readTime,  String id);

    @Query("update SystemMessage s set s.toStatus = 1, s.readTime = :readTime  where s.toId = :toId")
    @Modifying
    @Transactional
    void updateReadAllByToId(String toId, LocalDateTime readTime);
}