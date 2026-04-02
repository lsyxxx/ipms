package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CheckItemRepository extends JpaRepository<CheckItem, String> {

    /**
     * 删除一个检测项目关联的所有子参数
     * @param moduleId 检测项目id
     */
    @Modifying
    @Query("DELETE FROM CheckItem c WHERE c.checkModuleRef.checkModuleId = :moduleId")
    void deleteByCheckModuleId(String moduleId);

}