package com.abt.wxapp.user.favor.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.user.favor.model.UserFavorDTO;
import com.abt.wxapp.user.favor.service.UserFavorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wxapp/user/favor")
@RequiredArgsConstructor
public class UserFavorController {

    private final UserFavorService userFavorService;

    @PostMapping("/save")
    public R<String> insertUserFavor(String userId, String checkModuleId) {
        userFavorService.insertUserFavor(userId, checkModuleId);
        return R.success("收藏成功");
    }

    @GetMapping("/del")
    public R<Object> deleteUserFavor(String favorId) {
        userFavorService.deleteUserFavor(favorId);
        return R.success("取消收藏成功");
    }

    @GetMapping("/find")
    public R<List<UserFavorDTO>> findUserFavorList(String userId) {
        List<UserFavorDTO> list = userFavorService.findUserFavorList(userId);
        return R.success(list);
    }
}