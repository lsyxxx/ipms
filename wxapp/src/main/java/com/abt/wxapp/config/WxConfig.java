package com.abt.wxapp.config;

import com.abt.wxapp.common.model.WxAppParam;
import com.abt.wxapp.sys.entity.SystemSetting;
import com.abt.wxapp.sys.service.SystemSettingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序相关配置
 */
@Configuration
public class WxConfig {

    public static final String KEY_WX_APPID = "wx_appid";
    public static final String KEY_WX_APPSECRET = "wx_app_secret";

    private final SystemSettingService systemSettingService;

    public WxConfig(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    /**
     * 启动时加载
     * @return WxAppParam wxapp配置参数对象
     */
    @Bean()
    public WxAppParam wxAppParam() {
        WxAppParam wxAppParam = new WxAppParam();
        final SystemSetting appId = systemSettingService.findById(KEY_WX_APPID);
        final SystemSetting secret = systemSettingService.findById(KEY_WX_APPSECRET);

        wxAppParam.setAppId(appId.getValue());
        wxAppParam.setAppSecret(secret.getValue());
        return wxAppParam;
    }



}
