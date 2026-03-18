package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SystemErrorLogRepository extends JpaRepository<SystemErrorLog, String> {

    @Modifying
    @Query("""
        update SystemErrorLog set isSolved = true where id = :id
    """)
    void updateSolved(String id);
}