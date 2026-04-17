package com.abt.chkmodule.repository;

import com.abt.chkmodule.entity.CheckModuleInstrumentRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckModuleInstrumentRelRepository extends JpaRepository<CheckModuleInstrumentRel, String> {

    /**
     * 删除一个检测项目关联的所有仪器关系
      * @param moduleId
     */
    @Modifying
    @Query("DELETE FROM CheckModuleInstrumentRel r WHERE r.checkModuleRef.checkModuleId = :moduleId")
    void deleteByCheckModuleId(String moduleId);



    @Query("select case when count(rel) > 0 then true else false end from CheckModuleInstrumentRel rel where rel.checkModuleRef.checkModuleId = :checkModuleId")
    boolean existsByCheckModuleId(String checkModuleId);

    @Query("SELECT r.checkModuleRef.checkModuleId FROM CheckModuleInstrumentRel r WHERE r.instrumentId = :instrumentId")
    List<String> findModuleIdsByInstrumentId(String instrumentId);
}