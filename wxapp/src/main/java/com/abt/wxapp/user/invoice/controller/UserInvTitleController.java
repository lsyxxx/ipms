package com.abt.wxapp.user.invoice.controller;

import com.abt.common.config.ValidateGroup;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.user.invoice.entity.OpenUserInvTitle;
import com.abt.wxapp.user.invoice.service.InvTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/invoiceTitle")
@RequiredArgsConstructor
public class UserInvTitleController {

    private final InvTitleService invTitleService;

    /**
     * 更新或保存发票抬头
     */
    @PostMapping("/save")
    public R<OpenUserInvTitle> saveInvTitle(@Validated({ValidateGroup.Save.class}) @RequestBody OpenUserInvTitle invTitle) {
        return R.success(invTitleService.saveInvTitle(invTitle));
    }

    /**
     * 获取用户的发票抬头列表
     */
    @GetMapping("/find/titleList")
    public R<List<OpenUserInvTitle>> findListByUserId(String userId) {
        return R.success(invTitleService.findListByUserId(userId));
    }

    /**
     * 删除发票抬头
     */
    @GetMapping("/delete")
    public R<String> deleteInvTitle(String id) {
        invTitleService.deleteInvTitle(id);
        return R.success("删除成功");
    }

    /**
     * 设置默认发票抬头
     */
    @GetMapping("/setDefault")
    public R<String> setDefaultInvTitle(String userId, String id) {
        invTitleService.setDefaultInvTitle(userId, id);
        return R.success("设置默认抬头成功");
    }

    /**
     * 获取单个发票抬头详情
     */
    @GetMapping("/find/titleById")
    public R<OpenUserInvTitle> findTitleById(String id) {
        return R.success(invTitleService.findTitleById(id));
    }
}