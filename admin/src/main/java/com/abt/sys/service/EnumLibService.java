package com.abt.sys.service;

import com.abt.testing.entity.EnumLib;

import java.util.List;
import java.util.Map;

public interface EnumLibService {
    void updateEnumLib(EnumLib enumLib);

    EnumLib newCertEnumLib();

    void createEnumLib(EnumLib enumLib);

    void deleteEnumLib(String id);

    List<EnumLib> findEnumLibsBy(String type);

    Map<String, List<EnumLib>> findAllEnum();
}
