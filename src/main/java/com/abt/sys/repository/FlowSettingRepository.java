package com.abt.sys.repository;


import com.abt.sys.model.entity.FlowSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 */
public interface FlowSettingRepository extends JpaRepository<FlowSetting, String> {
    List<FlowSetting> findByKeyOrderByCreateDateDesc(@Param("key") String key);

    List<FlowSetting> findByTypeOrderByCreateDate(@Param("type") String type);
}
