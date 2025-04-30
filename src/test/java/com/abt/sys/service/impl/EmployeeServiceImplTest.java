package com.abt.sys.service.impl;

import com.abt.sys.model.dto.EmployeeInfoRequestForm;
import com.abt.sys.model.entity.Employee;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.util.WithQueryUtil;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.model.EmployeeSignatureDTO;
import org.junit.jupiter.api.Assertions;
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

    @Test
    void findWithSignature() {
        final EmployeeInfoRequestForm requestForm = new EmployeeInfoRequestForm();
        requestForm.setPage(1);
        requestForm.setLimit(40);
        requestForm.setIsExit(true);
        final List<EmployeeSignatureDTO> usigs = employeeService.findWithSignature(requestForm);
        Assertions.assertNotNull(usigs);
        System.out.println(usigs.size());
        for (EmployeeSignatureDTO usig : usigs) {
            System.out.printf("[%s, %s], fileName: %s \n", usig.getName(), usig.getJobNumber(), usig.getFileName());
        }

    }
}