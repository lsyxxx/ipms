package com.abt.oa.reposity;

import com.abt.oa.entity.TSystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TSystemSettingRepository extends JpaRepository<TSystemSetting, String> {
}