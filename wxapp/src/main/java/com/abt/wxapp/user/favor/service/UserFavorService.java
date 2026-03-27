package com.abt.wxapp.user.favor.service;

import com.abt.wxapp.user.favor.model.UserFavorDTO;
import java.util.List;

public interface UserFavorService {

    /**
     * 新增一条收藏数据
     */
    void insertUserFavor(String userId, String checkModuleId);

    /**
     * 删除收藏记录
     */
    void deleteUserFavor(String favorId);

    /**
     * 查询用户收藏列表
     */
    List<UserFavorDTO> findUserFavorList(String userId);
}