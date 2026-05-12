package com.abt.chkmodule.service;


import com.abt.chkmodule.entity.DynamicScheme;

import java.util.List;
import java.util.Optional;

public interface DynamicSchemeService {
    void publish(DynamicScheme form);

    /**
     * 暂存
     */
    void tempSave(DynamicScheme form);

    DynamicScheme findNewestByCheckModuleId(String checkModuleId, String checkModuleName);

    List<DynamicScheme> findAllCheckModuleDynamicForm();

    void findOneBy(Long id);

    Optional<DynamicScheme> findById(Long id);
}
