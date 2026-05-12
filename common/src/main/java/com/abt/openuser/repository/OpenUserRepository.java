package com.abt.openuser.repository;

import com.abt.chkmodule.model.ChannelEnum;
import com.abt.openuser.entity.OpenUserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OpenUserRepository extends JpaRepository<OpenUserInfo, String> {

    Optional<OpenUserInfo> findByOpenIdAndChannel(String openId, ChannelEnum channel);

    @Query("SELECT u FROM OpenUserInfo u WHERE " +
            "(:query IS NULL OR :query = '' " +
            "   OR u.name LIKE %:query% " +
            "   OR u.phone LIKE %:query% " +
            "   OR u.address LIKE %:query% " +
            "   OR u.openId LIKE %:query%) " +
            "AND (:channel IS NULL OR u.channel = :channel)")
    Page<OpenUserInfo> findByQuery(String query,
                                   ChannelEnum channel,
                                   Pageable pageable
    );
}