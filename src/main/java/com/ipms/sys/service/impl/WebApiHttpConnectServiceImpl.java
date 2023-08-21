package com.ipms.sys.service.impl;

import com.ipms.sys.model.dto.UserView;
import com.ipms.sys.service.HttpConnectService;
import org.springframework.stereotype.Service;

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





    private UserView testAccount() {
        return new UserView().setAccount("abtadmin")
                .setName("abtname")
                .setMobile("17391120673")
                .setPassword("abtpwd")
                .setSex(0)
                .setId("ddc056b0e7b1");
    }
}
