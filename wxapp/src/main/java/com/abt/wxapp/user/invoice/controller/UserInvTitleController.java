package com.abt.wxapp.user.invoice.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.user.invoice.model.InvTitleRequest;
import com.abt.wxapp.user.invoice.model.InvTitleVo;
import com.abt.wxapp.user.invoice.service.InvTitleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/invoice-title")
@RequiredArgsConstructor
@Validated
public class UserInvTitleController {

    private final InvTitleService invTitleService;

    /**
     * 更新或保存发票抬头
     */
    @PostMapping("/save")
    public R<InvTitleVo> save(@Valid @RequestBody InvTitleRequest request) {
        return R.success(invTitleService.save(request));
    }

    /**
     * 获取用户的发票抬头列表
     */
    @GetMapping("/find-list")
    public R<List<InvTitleVo>> findList(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") String userId) {
        return R.success(invTitleService.findList(userId));
    }

    /**
     * 删除发票抬头
     */
    @GetMapping("/delete")
    public R<String> delete(@RequestParam("id") @NotBlank(message = "发票抬头ID不能为空") String id) {
        invTitleService.delete(id);
        return R.success("删除成功");
    }

    /**
     * 设置默认发票抬头
     */
    @GetMapping("/update-default")
    public R<String> updateDefault(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") String userId,
                                   @RequestParam("id") @NotBlank(message = "发票抬头ID不能为空") String id) {
        invTitleService.updateDefault(userId, id);
        return R.success("设置默认抬头成功");
    }

    /**
     * 获取单个发票抬头详情
     */
    @GetMapping("/find-by-id")
    public R<InvTitleVo> findById(@RequestParam("id") @NotBlank(message = "发票抬头ID不能为空") String id) {
        return R.success(invTitleService.findById(id));
    }
}