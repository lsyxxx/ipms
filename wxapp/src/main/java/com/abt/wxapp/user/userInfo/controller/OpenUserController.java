package com.abt.wxapp.user.userInfo.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.wxapp.common.model.R;
import com.abt.wxapp.user.userInfo.entity.OpenUserInfo;
import com.abt.wxapp.user.userInfo.model.OpenUserInfoRequestForm;
import com.abt.wxapp.user.userInfo.service.OpenUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 微信小程序用户信息 Controller
 */
@RequestMapping("/wxapp/user/info")
@RequiredArgsConstructor
@RestController
public class OpenUserController {

    private final OpenUserService openUserInfoService;

    /**
     * 分页多条件查询用户信息
     */
    @GetMapping("/page")
    public Page<OpenUserInfo> findByQuery(OpenUserInfoRequestForm requestForm) {
        return openUserInfoService.findByQuery(requestForm);
    }

    /**
     * 编辑/保存客户信息
     */
    @PostMapping("/save")
    public R<String> save(@RequestBody @Validated(ValidateGroup.Save.class) OpenUserInfo openUserInfo) {
        openUserInfoService.saveUser(openUserInfo);
        return R.success("保存成功");
    }
}
