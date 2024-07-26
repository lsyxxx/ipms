package com.abt.oa.service.impl;

import com.abt.common.model.User;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.reposity.FieldAttendanceSettingRepository;
import com.abt.oa.reposity.FieldWorkRepository;
import com.abt.oa.service.FieldWorkService;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Service
public class FieldWorkServiceImpl implements FieldWorkService {

    private final FieldAttendanceSettingRepository fieldAttendanceSettingRepository;
    private final FieldWorkRepository fieldWorkRepository;
    private final EmployeeService employeeService;

    public FieldWorkServiceImpl(FieldAttendanceSettingRepository fieldAttendanceSettingRepository, FieldWorkRepository fieldWorkRepository, EmployeeService employeeService) {
        this.fieldAttendanceSettingRepository = fieldAttendanceSettingRepository;
        this.fieldWorkRepository = fieldWorkRepository;
        this.employeeService = employeeService;
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

    //员工表中部门经理
    public static final String EMP_POS_MGR = "部门经理";
    //工程技术部，西南项目部
    public static final List<String> fieldWorkDept = List.of("c3626282-a499-4354-8330-b49fff6887b9", "e5b9f524-485b-496f-9786-8a92a1a9ad2c");

    //查询用户野外考勤审批人
    @Override
    public User findUserReviewer(String userDept) {
        if (fieldWorkDept.contains(userDept)) {
            EmployeeInfo example = new EmployeeInfo();
            example.setDept(userDept);
            example.setPosition(EMP_POS_MGR);
            //工程技术部和西南项目部选部门经理
            final List<EmployeeInfo> list = employeeService.getByExample(example);
            if (list != null && !list.isEmpty()) {
                return new User(list.get(0));
            }
        }
        return null;
    }


    public void addFieldWork(FieldWork fw) {

    }

}
