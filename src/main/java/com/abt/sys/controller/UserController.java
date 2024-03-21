package com.abt.sys.controller;

import com.abt.common.model.User;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.abt.common.model.R;

import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("/sys/user")
@Slf4j
public class UserController{
    private final UserService<UserView, User> sqlServerUserService;

    public UserController(@Qualifier("sqlServerUserService") UserService<UserView, User> sqlServerUserService) {
        this.sqlServerUserService = sqlServerUserService;
    }

    @GetMapping("/all/simple")
    public R<List<User>> getAllSimpleUser(@RequestParam(required = false) Integer status) {
        final List<User> list = sqlServerUserService.getAllSimpleUser(status);
        return R.success(list, list.size());
    }

}
