package com.abt.wxapp.user.userInfo.model;

import com.abt.chkmodule.model.ChannelEnum;
import com.abt.wxapp.common.model.PageRequestForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 查询参数表单
 */
@Getter
@Setter
@NoArgsConstructor
public class OpenUserInfoRequestForm extends PageRequestForm {

    /**
     * 注册渠道 (精确查询)
     */
    private ChannelEnum channel;
}