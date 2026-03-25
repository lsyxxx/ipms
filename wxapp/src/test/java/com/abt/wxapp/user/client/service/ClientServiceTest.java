package com.abt.wxapp.user.client.service;

import com.abt.sys.exception.BusinessException;
import com.abt.wxapp.user.client.service.ClientService;
import com.abt.wxapp.user.userInfo.entity.OpenUserClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientServiceImpl 真实数据库测试 (带手动参数配置)
 */
@SpringBootTest
class ClientServiceImplIntegrationTest {

    @Autowired
    private ClientService clientService;

    // =====================================================================
    // 🎯 仪表盘 / 手动输入区
    // 每次点击测试前，在这里填入你想要测试的真实数据！
    // =====================================================================

    // 1. 你想修改或删除的【委托人主键 ID】 (对应数据库的 id 字段)
    private final String TARGET_CLIENT_ID = "36b18d8b-6723-4496-8769-b0c3fb95854b";

    // 2. 你想查询其列表的【用户 ID】 (对应数据库的 userid 字段)
    private final String TARGET_USER_ID = "123";

    // =====================================================================


    @Test
    @DisplayName("1. 真实插入一条新数据")
    @Rollback(false) // 绝不回滚
    void test1_InsertClient() {
        OpenUserClient client = new OpenUserClient();
        // 自动生成一个随机ID，防止主键冲突报错
        String newId = "TEST-" + UUID.randomUUID().toString().substring(0, 8);
        client.setId(newId);
        client.setUserId(TARGET_USER_ID); // 默认挂在上面配置的用户下面
        client.setClientName("手动测试企业");
        client.setContactName("测试员");
        client.setContactPhone("13800138000");

        OpenUserClient savedClient = clientService.insertClient(client);

        System.out.println("=========================================");
        System.out.println("✅ 数据已真实插入数据库！");
        System.out.println("💡 刚插入的委托人 ID 是: " + savedClient.getId());
        System.out.println("💡 你可以复制上面这个 ID，填到顶部的 TARGET_CLIENT_ID 里，用来测试接下来的修改和删除！");
        System.out.println("=========================================");
    }

    @Test
    @DisplayName("2. 修改指定 ID 的数据")
    @Rollback(false)
    void test2_UpdateClient() {
        if (TARGET_CLIENT_ID.contains("这里换成")) {
            System.err.println("❌ 兄弟，你还没在顶部填入你要修改的 TARGET_CLIENT_ID 呢！");
            return;
        }

        OpenUserClient updateInfo = new OpenUserClient();
        // 🎯 使用顶部你手动配置的 ID
        updateInfo.setId(TARGET_CLIENT_ID);
        updateInfo.setClientName("企业名称被更新了");
        updateInfo.setContactPhone("19999999999");

        try {
            OpenUserClient updatedClient = clientService.updateClient(updateInfo);
            System.out.println("✅ ID为 [" + TARGET_CLIENT_ID + "] 的数据已成功修改，去数据库刷新看看吧！");
            System.out.println("更新后的名称: " + updatedClient.getClientName());
        } catch (BusinessException e) {
            System.err.println("❌ 修改失败，原因：" + e.getMessage());
        }
    }

    @Test
    @DisplayName("3. 查询指定用户的所有委托人")
    void test3_FindClients() {
        if (TARGET_USER_ID.contains("这里换成")) {
            System.err.println("❌ 兄弟，你还没在顶部填入你要查询的 TARGET_USER_ID 呢！");
            return;
        }

        // 🎯 使用顶部你手动配置的用户 ID
        List<OpenUserClient> list = clientService.findClientsByUserId(TARGET_USER_ID);

        System.out.println("✅ 用户 [" + TARGET_USER_ID + "] 名下共有 " + list.size() + " 条委托人记录：");
        for (int i = 0; i < list.size(); i++) {
            OpenUserClient client = list.get(i);
            System.out.println("   " + (i + 1) + ". ID: " + client.getId() + " | 名称: " + client.getClientName() + " | 电话: " + client.getContactPhone());
        }
    }

    @Test
    @DisplayName("4. 删除指定 ID 的数据")
    @Rollback(false)
    void test4_DeleteClient() {
        if (TARGET_CLIENT_ID.contains("这里换成")) {
            System.err.println("❌ 兄弟，你还没在顶部填入你要删除的 TARGET_CLIENT_ID 呢！");
            return;
        }

        // 🎯 使用顶部你手动配置的委托人 ID 进行物理删除
        clientService.deleteClient(TARGET_CLIENT_ID);
        System.out.println("✅ 数据库中 ID为 [" + TARGET_CLIENT_ID + "] 的委托人已经被灰飞烟灭！");
    }
}