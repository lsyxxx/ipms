package com.abt.app.service.impl;

import com.abt.app.entity.PushRegister;
import com.abt.app.service.PushService;

import java.util.List;

/**
 * 使用unipush  推送
 */
public class UniPushServiceImpl implements PushService {
    public static final String appId = "vYIfEQzLRV6zMtDv8Uq5c7";
    public static final String appKey = "ci5KCUZoXy54ePpOlZpWb4";
    public static final String appSecret = "XJekIyNPxa5JCCsUzKDd72";
    public static final String masterSecret = "80KSHScDR19Mo1SjVF8Kb8";

    @Override
    public List<PushRegister> findByUserid(String userid) {
        return List.of();
    }

    @Override
    public void register(PushRegister pushRegister) {

    }

    @Override
    public void pushAndroid(String userid, String alert, String message, int badgeAddNum) {

    }


    /**
     * 针对个人单推
     * @param pushRegister 推送对象
     */
    public void singlePush(PushRegister pushRegister) {

    }
}
