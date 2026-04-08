package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckItemRepository extends JpaRepository<CheckItem, String> {

    /**
     * 删除一个检测项目关联的所有子参数
     */
    @Modifying
    @Query("DELETE FROM CheckItem c WHERE c.checkModuleRef.checkModuleId = :moduleId")
    void deleteByCheckModuleId(String moduleId);


    @Query("select case when count(ci) > 0 then true else false end from CheckItem ci where ci.checkModuleRef.checkModuleId = :checkModuleId")
    boolean existsByCheckModuleId(String checkModuleId);

    /**
     * 批量查询多个检测项目对应的所有子参数
     */
    @Query("SELECT c FROM CheckItem c WHERE c.checkModuleRef.checkModuleId IN :checkModuleIds")
    List<CheckItem> findByCheckModuleIdIn(List<String> checkModuleIds);
}