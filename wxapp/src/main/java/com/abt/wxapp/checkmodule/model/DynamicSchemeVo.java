package com.abt.wxapp.checkmodule.model;

import com.abt.chkmodule.entity.CheckComponent;
import com.abt.common.model.SaveMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 动态表单 Schema 展示对象
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DynamicSchemeVo {

    private Long id;

    private String checkModuleId;

    private String checkModuleName;

    private String title;

    private String description;

    private List<CheckComponent> components;

    private SaveMode status;
}
