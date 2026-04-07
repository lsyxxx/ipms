package com.abt.chkmodule.service;


import com.abt.chkmodule.entity.DynamicScheme;

import java.util.List;

public interface DynamicSchemeService {
    void save(DynamicScheme form);

    DynamicScheme findNewestByCheckModuleId(String checkModuleId, String checkModuleName);

    List<DynamicScheme> findAllCheckModuleDynamicForm();

    void findOneBy(Long id);
}
