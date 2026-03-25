package com.abt.wxapp.user.client.repository;

import com.abt.wxapp.user.client.entity.OpenUserClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 用户模块综合数据访问层
 */
@Repository
public interface UserClientRepository extends JpaRepository<OpenUserClient, String> {

    List<OpenUserClient> findClientsByUserIdOrderByCreateDateDesc(String userId);

    OpenUserClient findClientById(String id);

    @Modifying
    @Transactional
    @Query("UPDATE OpenUserClient c SET c.isDefault = (CASE WHEN c.id = :id THEN true ELSE false END) WHERE c.userId = :userId")
    void updateDefaultStatus(String userId, String id);
}