package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWorkAttendanceSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FieldAttendanceSettingRepository extends JpaRepository<FieldWorkAttendanceSetting, String>, JpaSpecificationExecutor<FieldWorkAttendanceSetting> {

    List<FieldWorkAttendanceSetting> findByEnabledOrderBySortAsc(boolean enabled);

    List<FieldWorkAttendanceSetting> findByName(String name);

    List<FieldWorkAttendanceSetting> findByShortName(String shortName);

    List<FieldWorkAttendanceSetting> findByVidOrderByVersionDesc(String vid);
}
