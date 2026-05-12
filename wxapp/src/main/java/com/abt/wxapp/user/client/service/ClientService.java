package com.abt.wxapp.user.client.service;

import com.abt.wxapp.user.client.model.ClientRequest;
import com.abt.wxapp.user.client.model.ClientVo;

import java.util.List;

public interface ClientService {

    ClientVo save(ClientRequest request);

    void delete(String id);

    List<ClientVo> findList(String userId);

    void updateDefault(String userId, String id);

    ClientVo findById(String id);
}
