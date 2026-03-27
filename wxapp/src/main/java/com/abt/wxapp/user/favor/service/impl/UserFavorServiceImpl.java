package com.abt.wxapp.user.favor.service.impl;

import com.abt.wxapp.user.favor.entity.OpenUserFavor;
import com.abt.wxapp.user.favor.model.UserFavorDTO;
import com.abt.wxapp.user.favor.repository.OpenUserFavorRepository;
import com.abt.wxapp.user.favor.service.UserFavorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFavorServiceImpl implements UserFavorService {

    private final OpenUserFavorRepository favorRepository;

    @Override
    public void insertUserFavor(String userId, String checkModuleId) {
        OpenUserFavor favor = new OpenUserFavor();
        favor.setUserId(userId);
        favor.setCheckModuleId(checkModuleId);
        favorRepository.save(favor);
    }

    @Override
    public void deleteUserFavor(String favorId) {
        favorRepository.deleteById(favorId);
    }

    @Override
    public List<UserFavorDTO> findUserFavorList(String userId) {
        return favorRepository.findUserFavorList(userId);
    }
}