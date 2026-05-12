package com.abt.wxapp.user.invoice.model;

import com.abt.wxapp.user.invoice.entity.TitleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 发票抬头展示对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvTitleVo {

    private String id;

    private String userId;

    private TitleTypeEnum titleType;

    private String companyName;

    private String taxNo;

    private String companyAddress;

    private String phone;

    private String bank;

    private String account;

    private Boolean isDefault;
}
