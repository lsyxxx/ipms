package com.abt.wxapp.user.client.service.impl;

import com.abt.wxapp.exception.BusinessException;
import com.abt.wxapp.user.client.entity.OpenUserClient;
import com.abt.wxapp.user.client.model.ClientRequest;
import com.abt.wxapp.user.client.model.ClientVo;
import com.abt.wxapp.user.client.repository.UserClientRepository;
import com.abt.wxapp.user.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 委托人业务实现
 */
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final UserClientRepository userClientRepository;

    /**
     * 新增/修改委托人信息
     */
    @Override
    @Transactional
    public ClientVo save(ClientRequest request) {
        OpenUserClient client = new OpenUserClient();
        client.setId(request.getId());
        client.setUserId(request.getUserId());
        client.setClientName(request.getClientName());
        client.setContactName(request.getContactName());
        client.setContactPhone(request.getContactPhone());
        client.setContactEmail(request.getContactEmail());
        if (request.getIsDefault() != null) {
            client.setDefault(request.getIsDefault());
        }
        return toVo(userClientRepository.save(client));
    }

    /**
     * 查询指定用户的委托人列表
     */
    @Override
    public List<ClientVo> findList(String userId) {
        return userClientRepository.findClientsByUserIdOrderByCreateDateDesc(userId).stream()
                .map(this::toVo)
                .toList();
    }

    /**
     * 删除指定委托人信息
     */
    @Override
    @Transactional
    public void delete(String id) {
        userClientRepository.deleteById(id);
    }

    /**
    * 设置默认委托人信息
    */
    @Override
    @Transactional
    public void updateDefault(String userId, String id) {
        userClientRepository.updateDefaultStatus(userId, id);
    }

    @Override
    public ClientVo findById(String id) {
        OpenUserClient client = userClientRepository.findById(id)
                .orElseThrow(() -> new BusinessException("委托人信息不存在"));
        return toVo(client);
    }

    private ClientVo toVo(OpenUserClient client) {
        return new ClientVo(
                client.getId(),
                client.getUserId(),
                client.getClientName(),
                client.getContactName(),
                client.getContactPhone(),
                client.getContactEmail(),
                client.isDefault()
        );
    }
}
