package com.abt.wxapp.user.client.controller;

import com.abt.wxapp.common.model.R; // 🚨 这里引用了你们项目里的统一返回包装类
import com.abt.wxapp.user.client.service.ClientService;
import com.abt.wxapp.user.userInfo.entity.OpenUserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 新增委托人信息
     */
    @PostMapping("/insert")
    public R<OpenUserClient> insertClient(@RequestBody OpenUserClient client) {
        OpenUserClient savedClient = clientService.insertClient(client);
        return R.success(savedClient);
    }

    /**
     * 更新委托人信息
     */
    @PostMapping("/update")
    public R<OpenUserClient> updateClient(@RequestBody OpenUserClient client) {
        OpenUserClient updatedClient = clientService.updateClient(client);
        return R.success(updatedClient);
    }

    /**
     * 获取指定用户的委托人列表
     */
    @GetMapping("/find")
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
}