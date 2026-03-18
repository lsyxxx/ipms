package com.abt.oa.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.oa.entity.OrgLeader;
import com.abt.oa.service.OrgLeaderService;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/orgleader")
public class OrgLeaderController {
    private final OrgLeaderService orgLeaderService;
    private final EmployeeService employeeService;

    public OrgLeaderController(OrgLeaderService orgLeaderService, EmployeeService employeeService) {
        this.orgLeaderService = orgLeaderService;
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    public R<List<OrgLeader>> findAll() {
        final List<OrgLeader> all = orgLeaderService.findAll();
        return R.success(all);
    }


    @PostMapping("/saveOne")
    public R<Object> saveOne(@RequestBody @Valid OrgLeader orgLeader) {
        orgLeaderService.saveOne(orgLeader);
        return R.success("保存成功!");
    }


    @PostMapping("/save")
    public R<Object> save(@RequestBody @Valid List<OrgLeader> list, @NotNull(message = "工号不能为空") @RequestParam String jobNumber) {
        orgLeaderService.save(list, jobNumber);
        return R.success("保存成功!");
    }

    @GetMapping("/ceos")
    public R<List<User>> findCeos() {
        final List<User> list = employeeService.findDCEOs();
        final List<EmployeeInfo> chiefEmp = employeeService.findByPosition("董事长");
        if (chiefEmp != null) {
            chiefEmp.forEach(i -> {
                list.add(i.toUser());
            });
        }

        final List<EmployeeInfo> ceoEmp = employeeService.findByPosition("总经理");
        if (ceoEmp != null) {
            ceoEmp.forEach(i -> {
                list.add(i.toUser());
            });
        }
        return R.success(list);
    }

    @GetMapping("/orgLeaderOptions")
    public R<List<User>> findLeaderOptions() {
        final List<User> orgLeaderOptions = orgLeaderService.findOrgLeaderOptions();
        return R.success(orgLeaderOptions);
    }


    @GetMapping("/hr")
    public R<List<User>> findHr() {
        final List<EmployeeInfo> hrEmp = employeeService.findByPosition("人力资源");
        List<User> users = new ArrayList<>();
        if (hrEmp != null) {
            hrEmp.forEach(i -> {
                users.add(i.toUser());
            });
        }
        return R.success(users);
    }

    @GetMapping("/del")
    public R<Object> deleteAllDept(@NotNull(message = "工号不能为空") String jobNumber) {
        orgLeaderService.deleteByJobNumber(jobNumber);
        return R.success("删除成功!");
    }

    @GetMapping("/deleteById")
    public R<Object> deleteById(String id) {
        orgLeaderService.deleteById(id);
        return R.success("删除成功");
    }
}
