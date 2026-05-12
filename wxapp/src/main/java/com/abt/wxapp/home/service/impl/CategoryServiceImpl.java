package com.abt.wxapp.home.service.impl;

import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.repository.CheckUnitRepository;
import com.abt.wxapp.home.model.CategoryOptionVo;
import com.abt.wxapp.home.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 检测分类（小程序）
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CheckUnitRepository checkUnitRepository;

    @Override
    public List<CategoryOptionVo> findList() {
        List<CategoryOptionVo> rows = new java.util.ArrayList<>();
        rows.add(new CategoryOptionVo("all", "全部"));
        List<CheckUnit> units = checkUnitRepository.findByUseChannel(ChannelEnum.WECHAT);
        units.stream()
                .filter(CheckUnit::isEnabled)
                .map(u -> new CategoryOptionVo(u.getCode(), u.getName()))
                .forEach(rows::add);
        return rows;
    }
}
