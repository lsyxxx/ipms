package com.abt.wf.config;

import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.repository.FlowSettingRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
public class BusinessConfig {

    private final FlowSettingRepository flowSettingRepository;

    public static final String RBS_TYPE = "rbsType";

    public BusinessConfig(FlowSettingRepository flowSettingRepository) {
        this.flowSettingRepository = flowSettingRepository;
    }

    /**
     * 查询报销类型
     */
    @Bean
    public List<FlowSetting> queryReimburseType() {
        return flowSettingRepository.findByTypeOrderByCreateDate(RBS_TYPE);
    }

}
