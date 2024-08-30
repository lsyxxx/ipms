package com.abt.sys.controller;

import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserRequestForm;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.Org;
import com.abt.sys.service.CompanyService;
import com.abt.sys.service.EmployeeService;
import com.abt.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import com.abt.common.model.R;
import com.abt.common.entity.Company;

import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("/sys/user")
@Slf4j
public class UserController{
    private final UserService<UserView, User> sqlServerUserService;
    private final EmployeeService employeeService;
    private final Company ABT;
    private final Company GRD;
    private final Company DC;

    public UserController(@Qualifier("sqlServerUserService") UserService<UserView, User> sqlServerUserService,
                          EmployeeService employeeService,
                          Company ABT, Company GRD, Company DC) {
        this.sqlServerUserService = sqlServerUserService;
        this.employeeService = employeeService;
        this.ABT = ABT;
        this.GRD = GRD;
        this.DC = DC;
    }

    @GetMapping("/all/simple")
    public R<List<User>> getAllSimpleUser(@RequestParam(required = false) Integer status) {
        final List<User> list = sqlServerUserService.getAllSimpleUser(status);
        return R.success(list, list.size());
    }

    @GetMapping("/dept/{jobNumber}")
    public R<User> getUserDepartment(@PathVariable String jobNumber) {
        final User userDept = sqlServerUserService.getUserDept(jobNumber);
        return R.success(userDept);
    }

    @GetMapping("/com")
    public R<List<Company>> getAllCompany() {
        final List<Company> allCompany = List.of(ABT, GRD, DC);
        return R.success(allCompany, allCompany.size());
    }

    @GetMapping("/dept1")
    public R<List<Org>> findAllDept() {
        final List<Org> allDept = sqlServerUserService.findAllDept();
        return R.success(allDept, allDept.size());
    }

    @GetMapping("/loginemp")
    public R<EmployeeInfo> findLoginUserEmployeeInfo() {
        UserView user = TokenUtil.getUserFromAuthToken();
        String empnum = user.getEmpnum();
        final EmployeeInfo employee = employeeService.findByJobNumber(empnum);
        return R.success(employee);
    }
    @GetMapping("/emp/userid")
    public R<EmployeeInfo> findEmployeeInfo(String userid) {
        final EmployeeInfo employee = employeeService.findUserByUserid(userid);
        return R.success(employee);
    }

    @GetMapping("/basic")
    public R<EmployeeInfo> findBasicUserInfo(@RequestParam(required = false) String userid) {
        if (StringUtils.isBlank(userid)) {
            userid = TokenUtil.getUseridFromAuthToken();
        }
        final EmployeeInfo employee = employeeService.findUserByUserid(userid);
        return R.success(employee);
    }

}
