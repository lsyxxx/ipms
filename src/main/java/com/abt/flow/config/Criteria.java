package com.abt.flow.config;

import com.abt.flow.model.entity.FlowCategory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class Criteria {

    @Bean
    @Lazy
    public Example<FlowCategory> enabledExample() {
        FlowCategory prop = new FlowCategory();
        prop.setEnable(true);
        prop.setDeleted(false);
        return Example.of(prop);
    }
}
