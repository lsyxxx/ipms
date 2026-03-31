package com.abt.wxapp.user.userInfo.controller;

import com.abt.wxapp.user.userInfo.entity.OpenUserInfo;
import com.abt.wxapp.user.userInfo.model.OpenUserInfoRequestForm;
import com.abt.wxapp.user.userInfo.service.OpenUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信小程序用户信息 Controller
 */
@RequestMapping("/pub/wxapp/user/info")
@RequiredArgsConstructor
@RestController("wxappUserController")
public class OpenUserController {

    private final OpenUserService openUserInfoService;

    /**
     * 分页多条件查询用户信息
     */
    @GetMapping("/page")
    public Page<OpenUserInfo> findByQuery(OpenUserInfoRequestForm requestForm) {
        return openUserInfoService.findByQuery(requestForm);
    }

}
