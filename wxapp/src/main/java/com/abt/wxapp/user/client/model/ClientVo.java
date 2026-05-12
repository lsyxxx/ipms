package com.abt.wxapp.user.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 委托人展示对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientVo {

    private String id;

    private String userId;

    private String clientName;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private boolean isDefault;
}
