package com.abt.sys.service.impl;

import com.abt.sys.model.entity.Employee;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.util.WithQueryUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceImplTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void getByExample() {
        EmployeeInfo condition = new EmployeeInfo();
        condition.setDept("c3626282-a499-4354-8330-b49fff6887b9");
        condition.setPosition("部门经理");
        final List<EmployeeInfo> list = employeeService.getByExample(condition);
        assertNotNull(list);
        list.forEach(i -> {
            System.out.printf("emp: %s, %s, %s %n", i.getName(), i.getDepartment().getName(), i.getJobNumber());
        });
    }

    @Test
    void getByJobNumber() {
        EmployeeInfo emp = employeeService.findByJobNumber("9999");
        emp = WithQueryUtil.build(emp);
        System.out.println(emp);
    }
}