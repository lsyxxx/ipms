package com.abt.wxapp.user.client.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.wxapp.common.model.R;
import com.abt.wxapp.user.client.service.ClientService;
import com.abt.wxapp.user.client.entity.OpenUserClient;
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
public class UserClientController {

    private final ClientService clientService;


    /**
     * 新增/更新委托人信息
     */
    @PostMapping("/save")
    public R<String> saveClient(@Validated({ValidateGroup.Save.class}) @RequestBody OpenUserClient client) {
        clientService.saveClient(client);
        return R.success("保存委托人信息成功");
    }

    /**
     * 获取指定用户的委托人列表
     */
    @GetMapping("/find/Clients")
    public R<List<OpenUserClient>> findClientsByUserId(String userId) {
        List<OpenUserClient> list = clientService.findClientsByUserId(userId);
        return R.success(list);
    }

    /**
     * 删除指定委托人信息
     */
    @GetMapping("/delete")
    public R<String> deleteClient(String id) {
        clientService.deleteClient(id);
        return R.success("删除成功");
    }

    /**
     * 设置默认委托人信息
     */
    @GetMapping("/setDefault")
    public R<String> setDefaultClient(String userId, String id) {
        clientService.setDefaultClient(userId, id);
        return R.success("默认委托人设置成功");
    }

    @GetMapping("/find/Client")
    public R<OpenUserClient> findClientById(String id) {
        return R.success(clientService.findClientById(id));
    }
}