package com.abt.wxapp.user.invoice.repository;

import com.abt.wxapp.user.invoice.entity.OpenUserInvTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInvTitleRepository extends JpaRepository<OpenUserInvTitle, String> {

    // 查询指定用户的发票列表，按创建时间倒序
    List<OpenUserInvTitle> findByUserIdOrderByCreateDateDesc(String userId);

    @Modifying
    @Query("UPDATE OpenUserInvTitle c SET c.isDefault = (CASE WHEN c.id = :id THEN true ELSE false END) WHERE c.userId = :userId")
    void updateDefaultStatus(String userId, String id);
}