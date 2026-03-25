package com.abt.wxapp.user.client.service.Impl;

import com.abt.wxapp.exception.BusinessException;
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
     * 新增委托人信息
     */
    @Override
    public OpenUserClient insertClient(OpenUserClient openUserClient) {
        return userClientRepository.save(openUserClient);
    }

    /**
     * 更新委托人信息
     */
    @Override
    public OpenUserClient updateClient(OpenUserClient updateInfo) {
        OpenUserClient existingClient = userClientRepository.findById(updateInfo.getId())
                .orElseThrow(() -> new BusinessException("操作失败：未找到编号为 " + updateInfo.getId() + " 的委托人信息"));
        org.springframework.beans.BeanUtils.copyProperties(updateInfo, existingClient, "id");
        return userClientRepository.save(existingClient);
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

}
