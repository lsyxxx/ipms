package com.ipms.sys.controller;

import com.ipms.common.model.R;
import com.ipms.sys.model.entity.User;
import com.ipms.sys.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统管理-用户操作
 */
@RestController
@RequestMapping("/sys/u")
@Slf4j
@Tag(name = "SysUserController", description = "系统用户管理")
public class SysUserController {
    private final RoleService roleService;
    private final UserServiceImpl userService;
    public SysUserController(RoleService roleService, UserServiceImpl userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @Operation(summary = "查询所有未删除用户")
    @GetMapping("/all")
    public R<List<User>> findAll() {
        List<User> userList = userService.findAll();
        log.debug("user list == {}", userList);
        return R.success(userList);
    }


}
