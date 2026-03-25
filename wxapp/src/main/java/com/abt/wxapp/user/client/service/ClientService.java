package com.abt.wxapp.user.client.service;

import com.abt.wxapp.user.userInfo.entity.OpenUserClient;

import java.util.List;

public interface ClientService {

    OpenUserClient insertClient(OpenUserClient openUserClient);

    OpenUserClient updateClient(OpenUserClient openUserClient);

    void deleteClient(String id);

    List<OpenUserClient> findClientsByUserId(String userId);
}
