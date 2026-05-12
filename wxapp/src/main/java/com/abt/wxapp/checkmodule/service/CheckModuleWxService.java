package com.abt.wxapp.checkmodule.service;

import com.abt.wxapp.checkmodule.model.CheckModuleDetailVo;
import com.abt.wxapp.checkmodule.model.DynamicSchemeVo;
import com.abt.wxapp.checkmodule.model.ProjectHomeItemVo;
import com.abt.wxapp.checkmodule.model.ProjectListItemVo;

import java.util.List;

/**
 * 小程序检测项目业务接口
 */
public interface CheckModuleWxService {

    List<ProjectListItemVo> findList();

    CheckModuleDetailVo findById(String id);

    List<ProjectHomeItemVo> findHomeList();

    DynamicSchemeVo findFormSchemaById(Long formSchemaId);
}
