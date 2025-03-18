package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import com.abt.sys.model.entity.EmployeeInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalarySlipRepositoryTest {
    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Test
    void countByIsSendAndMainId() {
        final int count = salarySlipRepository.countByIsSendAndMainId(true, "202406171718617591951");
        System.out.println(count);
    }


    @Test
    void find() {
//        final List<SalarySlip> list = salarySlipRepository.findEntireDataByMainId("202406181718689774230");
//        assertNotNull(list);
//        list.forEach(i -> {
//            final EmployeeInfo employeeInfo = i.getEmployeeInfo();
//            System.out.println(String.format("name: %s, dept: %s, deptName: %s", employeeInfo.getName(), employeeInfo.getDept(), employeeInfo.getDepartment().getName()));
//        });
    }

    @Test
    void findByJobNumberAndYearMonthContaining() {
        final List<SalarySlip> list = salarySlipRepository.findByJobNumberAndYearMonthContaining("071", "2025");
        assertNotNull(list);
        list.forEach(i -> {
            System.out.printf("时间: %s%n", i.getYearMonth());
        });
    }

    @Test
    void findAllUserUnchecked() {
        System.out.println("===== findAllUserUnchecked");
        final List<SalarySlip> list = salarySlipRepository.findAllUnchecked();
        assertNotNull(list);
        System.out.println(list.size());
        list.forEach(i -> {
            System.out.printf("user: %s%n", i.getName());
        });

    }

    @Test
    void findWithSignatureByMainId() {
        Set<String> jmSet = new HashSet<>();
        final List<SalarySlip> list = salarySlipRepository.findWithSignatureByMainIdAndJobNumberSet("202411251732506630211", null);
        assertNotNull(list);
        System.out.println(list.size());
        list.forEach(i -> {
//            System.out.printf("name: %s, emp: %s, user: %s%n",
//                    i.getName(),
//                    i.getEmployeeInfo() == null ? "" : i.getEmployeeInfo().getName(),
//                    i.getUser() == null ? "" : i.getUser().getUsername());
        });
    }

}