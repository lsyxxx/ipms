package com.ipms.sys.mapper;


import com.ipms.sys.model.dto.Function;

import java.util.List;

/**
 * 功能权限
 */
public interface FunctionMapper extends BaseMapper<Function, Function>{

    @Override
    List<Function> findAll();
}
