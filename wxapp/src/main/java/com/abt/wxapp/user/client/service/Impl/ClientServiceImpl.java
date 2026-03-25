package com.abt.wxapp.user.client.service.Impl;

import com.abt.wxapp.user.client.repository.UserClientRepository;
import com.abt.wxapp.user.client.service.ClientService;
import com.abt.wxapp.user.userInfo.entity.OpenUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final UserClientRepository userClientRepository;

    /**
     * 新增/修改委托人信息
     */
    @Override
    public OpenUserClient saveClient(OpenUserClient client) {
        return userClientRepository.save(client);
    }

    /**
     * 查询指定用户的委托人列表
     */
    @Override
    public List<OpenUserClient> findClientsByUserId(String userId) {
        return userClientRepository.findClientsByUserIdOrderByCreateDateDesc(userId);
    }

    /**
     * 删除指定委托人信息
     */
    @Override
    public void deleteClient(String id) {
        userClientRepository.deleteById(id);
    }

    /**
    * 设置默认委托人信息
    */
    @Override
    public void setDefaultClient(String userId, String id) {
        userClientRepository.updateDefaultStatus(userId, id);
    }

}
