package com.abt.oa.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.oa.entity.OrgLeader;
import com.abt.oa.service.OrgLeaderService;
import com.abt.sys.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/save")
    public R<Object> save(@RequestBody @Valid List<OrgLeader> list, @NotNull(message = "工号不能为空") @RequestParam String jobNumber) {
        orgLeaderService.save(list, jobNumber);
        return R.success("保存成功!");
    }

    @GetMapping("/ceos")
    public R<List<User>> findCeos() {
        final List<User> list = employeeService.findDCEOs();
        return R.success(list);
    }

    @GetMapping("/del")
    public R<Object> deleteAllDept(@NotNull(message = "工号不能为空") String jobNumber) {
        orgLeaderService.deleteByJobNumber(jobNumber);
        return R.success("删除成功!");
    }
}
