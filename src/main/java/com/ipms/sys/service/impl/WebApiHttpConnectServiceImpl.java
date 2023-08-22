package com.ipms.sys.service.impl;

import com.ipms.sys.model.dto.UserView;
import com.ipms.sys.model.dto.WebApiConnect;
import com.ipms.sys.service.HttpConnectService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * http连接webapi
 */

@Service
public class WebApiHttpConnectServiceImpl implements HttpConnectService {


    @Override
    public String createUrlWithToken(String tokenValue) {
        return null;
    }

    @Override
    public Object doConnect(String url) {
        //TODO: response返回是Json数据

        //http status(code) 异常
        //直接抛出异常
        //http status(code) 正常
        //组装json, 200正常
        //{"return": {userView},
        //  "message": "操作成功“
        //  "code": 200
        // }
        //返回UserView
        return testAccount();
    }

    @Bean
    public WebApiConnect webApiConnect() {
        return new WebApiConnect();
    }





    private UserView testAccount() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_FI"));
        return new UserView().setAccount("abtadmin")
                .setName("abtname")
                .setMobile("17391120673")
                .setPassword("abtpwd")
                .setSex(0)
                .setId("ddc056b0e7b1")
                .setAuthorities(authorities);
    }
}
