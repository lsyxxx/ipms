package com.abt.sys.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;


    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    @Override
    public EmployeeInfo findByJobNumber(String jobNumber) {
        if (StringUtils.isBlank(jobNumber)) {
            throw new MissingRequiredParameterException("员工工号");
        }
        return employeeRepository.findByJobNumber(jobNumber);
    }


    @Override
    public List<EmployeeInfo> findAllByExit(boolean exit) {
        final List<EmployeeInfo> list = employeeRepository.findByIsExit(exit);
        list.forEach(i -> {
            if (i.getTUser() != null) {
                i.setUserid(i.getTUser().getId());
            }
        });
        return list;
    }
}
