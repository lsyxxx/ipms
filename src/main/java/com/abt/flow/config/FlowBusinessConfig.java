package com.abt.flow.config;

import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.model.entity.FlowSetting;
import com.abt.flow.repository.FlowSettingRepository;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

/**
 *
 */
@Component
public class FlowBusinessConfig {

    private final FlowSettingRepository flowSettingRepository;

    /**
     * 一般审批人默认分类
     */
    public static final String DEFAULT_AUDITOR = "defaultAuditor";

    public FlowBusinessConfig(FlowSettingRepository flowSettingRepository) {
        this.flowSettingRepository = flowSettingRepository;
    }

    @Bean
    @Lazy
    public Example<FlowCategory> enabledExample() {
        FlowCategory prop = new FlowCategory();
        prop.setEnable(true);
        prop.setDeleted(false);
        return Example.of(prop);
    }


    @Bean
    @Lazy
    public Map<String, List<String>> flowSettingList() {
        List<FlowSetting> all = flowSettingRepository.findAll();
        Map<String, List<String>> collect = all.stream()
                .collect(groupingBy(FlowSetting::getKey
                        , mapping(FlowSetting::getValue, toList())));
        return collect;
    }

    /**
     * 默认审批用户
     * @return
     */
    @Bean(name = "flowDefaultAuditorMap")
    @Lazy
    public Map<String, String> defaultAuditor() {
        List<FlowSetting> list = flowSettingRepository.findByTypeOrderBycAndCreateDate(DEFAULT_AUDITOR);
        Map<String, String> collect = list.stream()
                .collect(toMap(FlowSetting::getKey, FlowSetting::getValue));
        return collect;
    }

}
