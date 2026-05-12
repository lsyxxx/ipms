package com.abt.wxapp.user.client.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.user.client.model.ClientRequest;
import com.abt.wxapp.user.client.model.ClientVo;
import com.abt.wxapp.user.client.service.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户委托人信息
 */
@RestController
@Slf4j
@RequestMapping("/user/client")
@RequiredArgsConstructor
@Validated
public class UserClientController {

    private final ClientService clientService;


    /**
     * 新增/更新委托人信息
     */
    @PostMapping("/save")
    public R<ClientVo> save(@Valid @RequestBody ClientRequest request) {
        return R.success(clientService.save(request));
    }

    /**
     * 获取指定用户的委托人列表
     */
    @GetMapping("/find-list")
    public R<List<ClientVo>> findList(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") String userId) {
        List<ClientVo> list = clientService.findList(userId);
        return R.success(list);
    }

    /**
     * 删除指定委托人信息
     */
    @GetMapping("/delete")
    public R<String> delete(@RequestParam("id") @NotBlank(message = "委托人ID不能为空") String id) {
        clientService.delete(id);
        return R.success("删除成功");
    }

    /**
     * 设置默认委托人信息
     */
    @GetMapping("/update-default")
    public R<String> updateDefault(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") String userId,
                                   @RequestParam("id") @NotBlank(message = "委托人ID不能为空") String id) {
        clientService.updateDefault(userId, id);
        return R.success("默认委托人设置成功");
    }

    @GetMapping("/find-by-id")
    public R<ClientVo> findById(@RequestParam("id") @NotBlank(message = "委托人ID不能为空") String id) {
        return R.success(clientService.findById(id));
    }
}