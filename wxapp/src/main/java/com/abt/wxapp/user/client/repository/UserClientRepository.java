package com.abt.wxapp.user.client.repository;

import com.abt.wxapp.user.userInfo.entity.OpenUserClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 用户模块综合数据访问层
 */
@Repository
public interface UserClientRepository extends JpaRepository<OpenUserClient, String> {

    List<OpenUserClient> findClientsByUserIdOrderByCreateDateDesc(String userId);

}