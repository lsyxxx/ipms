package com.abt.chkmodule.entity;

import com.abt.chkmodule.model.ChannelEnum;

public interface UseChannel {

    ChannelEnum getChannel();

    default boolean isWechat() {
        return ChannelEnum.WECHAT == getChannel();
    };

    default boolean isWeb() {
        return  ChannelEnum.WECHAT == getChannel();
    }
}
