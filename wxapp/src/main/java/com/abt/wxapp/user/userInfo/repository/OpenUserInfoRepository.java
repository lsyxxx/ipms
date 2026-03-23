package com.abt.wxapp.user.userInfo.repository;

import com.abt.wxapp.user.userInfo.entity.OpenUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpenUserInfoRepository extends JpaRepository<OpenUserInfo, String> {

    Optional<OpenUserInfo> findByOpenId(String openId);

}