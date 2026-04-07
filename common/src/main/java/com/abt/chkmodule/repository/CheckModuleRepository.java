package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.model.ChannelEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 检测项目-多条件分页查询
     */
    @Query("SELECT c FROM CheckModule c " +
            "WHERE (:query IS NULL OR c.name LIKE %:query% OR c.code LIKE %:query% OR c.aliasNames LIKE %:query%) " +
            "AND (:checkUnitId IS NULL OR c.checkUnitId = :checkUnitId) " +
            "AND (:useChannel IS NULL OR c.useChannel = :useChannel) " +
            "AND (:enabled IS NULL OR c.enabled = :enabled) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:reqCma = false OR EXISTS (SELECT 1 FROM CheckItem i1 WHERE i1.checkModuleRef.checkModuleId = c.id AND i1.isCma = true)) " +
            "AND (:reqCnas = false OR EXISTS (SELECT 1 FROM CheckItem i2 WHERE i2.checkModuleRef.checkModuleId = c.id AND i2.isCnas = true)) " +
            "AND (:reqOther = false OR EXISTS (SELECT 1 FROM CheckItem i3 WHERE i3.checkModuleRef.checkModuleId = c.id AND i3.otherCertificate IS NOT NULL AND i3.otherCertificate <> ''))")
    Page<CheckModule> findByQuery(
            String query,
            String checkUnitId,
            ChannelEnum useChannel,
            Boolean enabled,
            Integer status,
            boolean reqCma,
            boolean reqCnas,
            boolean reqOther,
            Pageable pageable);
}