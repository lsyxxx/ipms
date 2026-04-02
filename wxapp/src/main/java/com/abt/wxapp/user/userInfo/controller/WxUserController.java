package com.abt.wxapp.user.userInfo.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.wxapp.common.model.R;
import com.abt.openuser.entity.OpenUserInfo;
import com.abt.wxapp.user.userInfo.service.WxUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 微信小程序用户信息 Controller
 */
@RequestMapping("/wxapp/user/info")
@RequiredArgsConstructor
@RestController
public class WxUserController {

    private final WxUserService wxUserService;


    /**
     * 编辑/保存客户信息
     */
    @PostMapping("/save")
    public R<String> save(@RequestBody @Validated(ValidateGroup.Save.class) OpenUserInfo openUserInfo) {
//        wxUserService.saveUser(openUserInfo);
        return R.success("保存成功");
    }
}
