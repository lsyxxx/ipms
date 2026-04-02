package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CheckModuleRepository extends JpaRepository<CheckModule, String>{


    boolean existsByName(String name);

    /**
     * 检测项目-启用
     */
    @Modifying
    @Query("UPDATE CheckModule c SET c.enabled = true WHERE c.id = :id")
    void updateCheckModuleEnableById(String id);

    /**
     * 检测项目-禁用
     */
    @Modifying
    @Query("UPDATE CheckModule c SET c.enabled = false WHERE c.id = :id")
    void updateCheckModuleDisableById(String id);

    /**
     * 检测项目-删除暂存
     */
    @Modifying
    @Query("DELETE FROM CheckModule c WHERE c.id = :id")
    void deleteCheckModuleById(String id);

}