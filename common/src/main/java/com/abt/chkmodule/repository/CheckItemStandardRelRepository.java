package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckItemStandardRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CheckItemStandardRelRepository extends JpaRepository<CheckItemStandardRel, String> {

    /**
     * 删除一个检测项目关联的所有参数-标准关系
     * @param moduleId
     */
    @Modifying
    @Query("DELETE FROM CheckItemStandardRel r WHERE r.checkItemId IN (SELECT i.id FROM CheckItem i WHERE i.checkModuleId = :moduleId)")
    void deleteByCheckModuleId(String moduleId);
}