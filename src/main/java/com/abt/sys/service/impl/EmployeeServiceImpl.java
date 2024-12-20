package com.abt.sys.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.User;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.util.WithQueryUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
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
        return WithQueryUtil.build(employeeRepository.findByJobNumber(jobNumber));
    }


    @Override
    public EmployeeInfo findUserByUserid(String userid) {
        if (StringUtils.isBlank(userid)) {
            throw new MissingRequiredParameterException("员工id");
        }
        return WithQueryUtil.build(employeeRepository.findOneByUserid(userid));
    }


    @Override
    public User findBasicUserInfoByUserid(String userid) {
        EmployeeInfo employeeInfo = findUserByUserid(userid);
        User user = new User();
        user.setId(employeeInfo.getUserid());
        user.setEmployeeId(employeeInfo.getId());
        user.setUsername(employeeInfo.getName());
        user.setDeptId(employeeInfo.getDept());
        user.setDeptName(employeeInfo.getDeptName());
        user.setCode(employeeInfo.getJobNumber());
        return user;
    }


    @Override
    public List<EmployeeInfo> findAllByExit(boolean exit) {
        return WithQueryUtil.build(employeeRepository.findByIsExit(exit));
    }

    @Override
    public List<EmployeeInfo> getByExample(EmployeeInfo condition) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher::contains)
                .withIgnorePaths("enable", "sort");
        Example<EmployeeInfo> example = Example.of(condition, matcher);
        return WithQueryUtil.build(employeeRepository.findAll(example, Sort.by("jobNumber").ascending()));
    }

    @Override
    public String getUserCompanyByJobNumber(String jobNumber) {
        final EmployeeInfo emp = this.findByJobNumber(jobNumber);
        return emp.getCompany();
    }

    /**
     * 根据部门查询
     */
    public void getByDept(String ...deptId) {

    }
}
