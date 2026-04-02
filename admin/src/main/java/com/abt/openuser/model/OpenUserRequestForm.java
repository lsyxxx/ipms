package com.abt.openuser.model;

import com.abt.chkmodule.model.ChannelEnum;
import com.abt.common.model.RequestForm;
import com.abt.openuser.entity.OpenUserInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 对外用户
 */
@Getter
@Setter
public class OpenUserRequestForm extends RequestForm {


    /**
     * 注册渠道 (精确查询)
     */
    private ChannelEnum channel;

}
