package com.abt.oa.service.impl;

import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.reposity.FieldAttendanceSettingRepository;
import com.abt.oa.reposity.FieldWorkRepository;
import com.abt.oa.service.FieldWorkService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class FieldWorkServiceImpl implements FieldWorkService {

    private final FieldAttendanceSettingRepository fieldAttendanceSettingRepository;
    private final FieldWorkRepository fieldWorkRepository;

    public FieldWorkServiceImpl(FieldAttendanceSettingRepository fieldAttendanceSettingRepository, FieldWorkRepository fieldWorkRepository) {
        this.fieldAttendanceSettingRepository = fieldAttendanceSettingRepository;
        this.fieldWorkRepository = fieldWorkRepository;
    }

    @Override
    public List<FieldWorkAttendanceSetting> findAllSettings() {
        return fieldAttendanceSettingRepository.findAll(Sort.by("sort").ascending());
    }

    @Override
    public void saveSetting(FieldWorkAttendanceSetting fieldAttendanceSetting) {
        fieldAttendanceSettingRepository.save(fieldAttendanceSetting);
    }

    @Override
    public List<FieldWorkAttendanceSetting> findAllEnabledAllowance() {
        return fieldAttendanceSettingRepository.findByEnabledOrderBySortAsc(true);
    }

    //查询野外考勤记录
    public List<FieldWork> findRecordBy() {
        return null;
    }

    @Override
    public void saveAttendance(FieldWork fw) {
        fieldWorkRepository.save(fw);
    }


}
