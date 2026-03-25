package com.abt.wxapp.user.client.service;

import com.abt.wxapp.user.client.entity.OpenUserClient;

import java.util.List;

public interface ClientService {

    OpenUserClient saveClient(OpenUserClient client);

    void deleteClient(String id);

    List<OpenUserClient> findClientsByUserId(String userId);

    void setDefaultClient(String userId, String id);

    OpenUserClient findClientById(String id);
}
