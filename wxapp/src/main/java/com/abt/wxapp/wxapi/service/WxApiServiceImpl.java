package com.abt.wxapp.wxapi.service;

import com.abt.wxapp.wxapi.model.WxUserSession;
import org.springframework.stereotype.Service;

@Service
public class WxApiServiceImpl implements WxApiService {


    /**
     * TODO: 请求wx官方code2Session接口
     * @param code wx.login中返回的用户code
     */
    private WxUserSession requestCode2Session(String code) {


        return new WxUserSession();
    }

    @Override
    public String getUserOpenid(String code) {
        return "TEST_OPENID";
    }
}
