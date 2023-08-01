package com.ipms.sys.controller;

import com.ipms.common.model.R;
import com.ipms.sys.model.entity.RoleFunc;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  授权
 */
@RestController
@Slf4j
@RequestMapping("/sys/au")
@Tag(name = "AuthorizationController", description = "权限管理")
public class AuthorizationController {


    /**
     * 为功能func授权角色
     * @return
     */
    @PostMapping("/add/rfr")
    public R<List<RoleFunc>> addRoleFuncRelations(List<RoleFunc> list) {

        return new R<List<RoleFunc>>();
    }
}
