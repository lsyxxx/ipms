package com.abt.wxapp.sys.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 系统配置展示对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingVo {

    private String id;

    private String name;

    private String value;
}
