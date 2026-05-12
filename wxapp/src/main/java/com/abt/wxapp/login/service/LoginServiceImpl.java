package com.abt.wxapp.login.service;

import com.abt.chkmodule.model.ChannelEnum;
import com.abt.openuser.entity.OpenUserInfo;
import com.abt.openuser.repository.OpenUserRepository;
import com.abt.wxapp.security.JwtUtil;
import com.abt.wxapp.security.WxUserDetails;
import com.abt.wxapp.wxapi.service.WxApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信登录：openid 映射到 {@link OpenUserInfo}，保证 JWT 中 userId 非空
 */
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final WxApiService wxApiService;
    private final OpenUserRepository openUserRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public String wxLogin(String code) {
        String openid = wxApiService.findUserOpenid(code);
        OpenUserInfo user = openUserRepository
                .findByOpenIdAndChannel(openid, ChannelEnum.WECHAT)
                .orElseGet(() -> {
                    OpenUserInfo u = new OpenUserInfo();
                    u.setOpenId(openid);
                    u.setChannel(ChannelEnum.WECHAT);
                    u.setName("微信用户");
                    return openUserRepository.save(u);
                });
        return jwtUtil.generateToken(new WxUserDetails(user.getId(), user.getOpenId(), user.getName()));
    }
}
