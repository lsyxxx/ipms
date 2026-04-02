package com.abt.openuser.controller;

import com.abt.common.model.R;
import com.abt.openuser.entity.OpenUserInfo;
import com.abt.openuser.model.OpenUserRequestForm;
import com.abt.openuser.service.OpenUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部用户管理
 */
@RestController
@Slf4j
@RequestMapping("/open/user")
public class OpenUserController {
    private final OpenUserService openUserService;

    public OpenUserController(OpenUserService openUserService) {
        this.openUserService = openUserService;
    }

    /**
     * 多条件分页查询
     *
     * @param form 查询参数
     */
    @GetMapping("/page")
    public R<Page<OpenUserInfo>> findPageByQuery(@ModelAttribute OpenUserRequestForm form) {
        final Page<OpenUserInfo> page = openUserService.findByQuery(form);
        return R.success(page);
    }



}
