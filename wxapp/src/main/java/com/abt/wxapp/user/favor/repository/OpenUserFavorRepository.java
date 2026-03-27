package com.abt.wxapp.user.favor.repository;

import com.abt.wxapp.user.favor.entity.OpenUserFavor;
import com.abt.wxapp.user.favor.model.UserFavorDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenUserFavorRepository extends JpaRepository<OpenUserFavor, String> {

    @Query("SELECT new com.abt.wxapp.user.favor.model.UserFavorDTO(" +
            "f.id, " +
            "f.createDate, " +
            "f.checkModuleId, " +
            "m.name, " +
            "m.coverImage, " +
            "m.price, " +
            "m.duration, " +
           // "m.active," +
            "true, " +
            "(CASE WHEN EXISTS (SELECT 1 FROM CheckItem i WHERE i.checkModuleId = m.id AND i.isCma = true) THEN true ELSE false END), " +
            "(CASE WHEN EXISTS (SELECT 1 FROM CheckItem i WHERE i.checkModuleId = m.id AND i.isCnas = true) THEN true ELSE false END)) " +
            "FROM OpenUserFavor f " +
            "LEFT JOIN CheckModule m ON f.checkModuleId = m.id " +
            "WHERE f.userId = ?1 " +
            "ORDER BY f.createDate DESC")
    List<UserFavorDTO> findUserFavorList(String userId);

}