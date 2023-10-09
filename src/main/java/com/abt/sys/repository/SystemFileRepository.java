package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
public interface SystemFileRepository extends JpaRepository<SystemFile, String> {

    @Modifying
    @Transactional
    @Query("UPDATE SystemFile set isDeleted = true where id = ?1")
//    @Query(value = "update T_sys_file set is_del = 1 where id = ?", nativeQuery = true)
    void softDelete(String id);
}
