package com.abt.wxapp.user.favor.service.impl;

import com.abt.wxapp.user.favor.entity.OpenUserFavor;
import com.abt.wxapp.user.favor.model.UserFavorDTO;
import com.abt.wxapp.user.favor.repository.OpenUserFavorRepository;
import com.abt.wxapp.user.favor.service.UserFavorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 用户收藏业务实现
 */
@Service
@RequiredArgsConstructor
public class UserFavorServiceImpl implements UserFavorService {

    private final OpenUserFavorRepository favorRepository;

    @Override
    @Transactional
    public void save(String userId, String checkModuleId) {
        OpenUserFavor favor = new OpenUserFavor();
        favor.setUserId(userId);
        favor.setCheckModuleId(checkModuleId);
        favorRepository.save(favor);
    }

    @Override
    @Transactional
    public void delete(String favorId) {
        favorRepository.deleteById(favorId);
    }

    @Override
    public List<UserFavorDTO> findList(String userId) {
        return favorRepository.findListByUserId(userId);
    }
}