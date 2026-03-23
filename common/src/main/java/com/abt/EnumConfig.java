package com.abt;

import com.abt.testing.entity.EnumLib;
import com.abt.testing.repository.EnumLibRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 从t_enumlib中读取枚举类
 */
@Component
@Slf4j
@Configuration
public class EnumConfig {

    private final EnumLibRepository enumLibRepository;

    public EnumConfig(EnumLibRepository enumLibRepository) {
        this.enumLibRepository = enumLibRepository;
    }

    //设备管理的t_enumlib ftypeid
    public static final String FTYPEID_INSTRUMENT = "EnumlibshebeiType";


    /**
     * 设备分类
     */
    @Bean("agreementTypeEnumMap")
    public Map<String, EnumLib> getInstrumentType() {
        log.info("init agreementTypeEnum...");
        return enumLibRepository.findAllByFtypeidOrderByFid(FTYPEID_INSTRUMENT).stream()
                .collect(Collectors.toMap(EnumLib::getFid, item -> item));
    }
}
