package com.abt.wxapp.user.favor.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.user.favor.model.UserFavorDTO;
import com.abt.wxapp.user.favor.service.UserFavorService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wxapp/user/favor")
@RequiredArgsConstructor
@Validated
public class UserFavorController {

    private final UserFavorService userFavorService;

    @PostMapping("/save")
    public R<String> save(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") String userId,
                          @RequestParam("checkModuleId") @NotBlank(message = "检测项目ID不能为空") String checkModuleId) {
        userFavorService.save(userId, checkModuleId);
        return R.success("收藏成功");
    }

    @GetMapping("/delete")
    public R<String> delete(@RequestParam("favorId") @NotBlank(message = "收藏ID不能为空") String favorId) {
        userFavorService.delete(favorId);
        return R.success("取消收藏成功");
    }

    @GetMapping("/find-list")
    public R<List<UserFavorDTO>> findList(@RequestParam("userId") @NotBlank(message = "用户ID不能为空") String userId) {
        List<UserFavorDTO> list = userFavorService.findList(userId);
        return R.success(list);
    }
}