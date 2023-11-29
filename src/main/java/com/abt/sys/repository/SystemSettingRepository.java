package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 流程配置
 */
public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {

    List<SystemSetting> findByKeyOrderByCreateDateDesc(@Param("key") String key);

    List<SystemSetting> findByTypeOrderByCreateDate(@Param("type") String type);
}

