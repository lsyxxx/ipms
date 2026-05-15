package com.abt.wxapp.checkmodule.service.impl;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.entity.DynamicScheme;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.repository.*;
import com.abt.chkmodule.service.CheckModuleService;
import com.abt.chkmodule.service.DynamicSchemeService;
import com.abt.chkmodule.service.impl.CheckModuleServiceImpl;
import com.abt.wxapp.checkmodule.model.CheckModuleDetailVo;
import com.abt.wxapp.checkmodule.model.DynamicSchemeVo;
import com.abt.wxapp.checkmodule.model.ProjectHomeItemVo;
import com.abt.wxapp.checkmodule.model.ProjectListItemVo;
import com.abt.wxapp.checkmodule.service.CheckModuleWxService;
import com.abt.wxapp.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 小程序检测项目查询
 */
@Service
public class CheckModuleWxServiceImpl  implements CheckModuleWxService {

    private final CheckModuleService checkModuleService;

    public CheckModuleWxServiceImpl(CheckModuleService checkModuleService) {
        this.checkModuleService = checkModuleService;
    }



}
