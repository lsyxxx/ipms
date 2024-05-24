package com.abt.sys.config;

import com.abt.testing.entity.EnumLib;
import com.abt.testing.repository.EnumLibRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 枚举
 */
@Component
@Slf4j
@Configuration
public class EnumLibConfig {

    public static final String ENUMLIB_AGREEMENT_TYPE = "EnumlibAgreementType";
    public static final String ENUMLIB_CERT_TYPE = "EnumLibCertType";


    private final EnumLibRepository enumLibRepository;
    private final ApplicationContext applicationContext;

    public EnumLibConfig(EnumLibRepository enumLibRepository, ApplicationContext applicationContext) {
        this.enumLibRepository = enumLibRepository;
        this.applicationContext = applicationContext;
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


    @Bean("agreementTypeEnumList")
    public List<EnumLib> getAgreementTypeEnumList() {
        log.info("init agreementTypeEnumList...");
        return enumLibRepository.findAllByFtypeidOrderByFid(ENUMLIB_AGREEMENT_TYPE);
    }

    /**
     * 获取所有enum, key=分类
     */
//    @Bean("allEnumMap")
    public Map<String, List<EnumLib>> getAllEnum() {
        return enumLibRepository.findAll().stream().collect(Collectors.groupingBy(EnumLib::getFtypeid));
    }
}
