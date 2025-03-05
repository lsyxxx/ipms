package com.abt.app.controller;

import com.abt.app.entity.PushRegister;
import com.abt.app.service.PushService;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/app/push")
public class PushController {
    private final PushService pushService;

    public PushController(PushService pushService) {
        this.pushService = pushService;
    }

    @PostMapping("/reg")
    public R<Object> register(@Validated({ValidateGroup.All.class}) @RequestBody PushRegister jpushRegister) {
        pushService.register(jpushRegister);
        return R.success("用户" + jpushRegister.getUserid() + "注册成功!");
    }



}
