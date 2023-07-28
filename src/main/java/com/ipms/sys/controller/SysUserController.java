package com.ipms.sys.controller;

import com.ipms.common.model.R;
import com.ipms.sys.model.entity.User;
import com.ipms.sys.service.RoleService;
import com.ipms.sys.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统管理-用户操作
 */
@RestController
@RequestMapping("/u")
@Slf4j
public class SysUserController {
    private final RoleService roleService;
    private final UserService userService;
    public SysUserController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public R<List<User>> findAll(HttpServletRequest request, HttpServletResponse response) {
        List<User> userList = userService.userList();
        log.debug("user list == {}", userList);
        return R.success(userList);
    }


}
