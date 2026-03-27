package com.abt.wxapp.checkmodule.service;


import com.abt.wxapp.checkmodule.entity.DynamicScheme;

import java.util.List;

public interface DynamicSchemeService {
    void save(DynamicScheme form);

    DynamicScheme findNewestByCheckModuleId(String checkModuleId, String checkModuleName);

    List<DynamicScheme> findAllCheckModuleDynamicForm();

    void findOneBy(Long id);
}
