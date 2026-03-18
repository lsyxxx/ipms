package com.abt.wxapp.sys.repository;

import com.abt.wxapp.sys.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 系统配置数据库操作
 */
public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {
}
