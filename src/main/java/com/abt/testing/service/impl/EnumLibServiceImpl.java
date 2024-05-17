package com.abt.testing.service.impl;

import com.abt.testing.entity.EnumLib;
import com.abt.testing.repository.EnumLibRepository;
import com.abt.testing.service.EnumLibService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class EnumLibServiceImpl implements EnumLibService {

    private final EnumLibRepository enumLibRepository;

    private final Map<String, List<EnumLib>> allEnumMap;

    public EnumLibServiceImpl(EnumLibRepository enumLibRepository, @Qualifier("allEnumMap") Map<String, List<EnumLib>> allEnumMap) {
        this.enumLibRepository = enumLibRepository;
        this.allEnumMap = allEnumMap;
    }

}
