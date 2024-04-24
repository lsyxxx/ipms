package com.abt.sys.config;

import com.abt.testing.entity.EnumLib;
import com.abt.testing.repository.EnumLibRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 枚举
 */
@Component
@Slf4j
public class EnumLibConfig {

    public static final String ENUMLIB_AGREEMENT_TYPE = "EnumlibAgreementType";


    private final EnumLibRepository enumLibRepository;

    public EnumLibConfig(EnumLibRepository enumLibRepository) {
        this.enumLibRepository = enumLibRepository;
    }

    /**
     * 合同分类
     */
    @Bean("agreementTypeEnumMap")
    public Map<String, EnumLib> getAgreementTypeEnum() {
        log.info("init agreementTypeEnum...");
       return enumLibRepository.findAllByFtypeidOrderByFid(ENUMLIB_AGREEMENT_TYPE).stream()
                .collect(Collectors.toMap(EnumLib::getFid, item -> item));
    }
}
