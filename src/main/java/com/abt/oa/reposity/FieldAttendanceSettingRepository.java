package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWorkAttendanceSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldAttendanceSettingRepository extends JpaRepository<FieldWorkAttendanceSetting, String> {

    List<FieldWorkAttendanceSetting> findByEnabledOrderBySortAsc(boolean enabled);

    List<FieldWorkAttendanceSetting> findByName(String name);

    List<FieldWorkAttendanceSetting> findByShortName(String shortName);
}