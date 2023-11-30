package com.abt.wf;

import com.abt.common.model.User;
import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.repository.FlowSettingRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;

/**
 * 流程配置相关
 */
@Component
public class WorkFlowConfig {

    /**
     * 一般审批人默认分类
     */
    public static final String DEFAULT_AUDITOR = "defaultAuditor";
    public static final String DEFAULT_SKIP = "flowSkipManager";
    public static final String OA_AUTH = "oaAuth";

    private final FlowSettingRepository FlowSettingRepository;

    public WorkFlowConfig(FlowSettingRepository FlowSettingRepository) {
        this.FlowSettingRepository = FlowSettingRepository;
    }

    @Bean
    @Lazy
    public Map<String, List<String>> FlowSettingList() {
        List<FlowSetting> all = FlowSettingRepository.findAll();
        return all.stream().collect(groupingBy(FlowSetting::getKey, mapping(FlowSetting::getValue, toList())));
    }

    /**
     * 默认审批用户
     */
    @Bean(name = "flowDefaultAuditorMap")
    public Map<String, User> defaultAuditor() {
        List<FlowSetting> list = FlowSettingRepository.findByTypeOrderByCreateDate(DEFAULT_AUDITOR);
        return list.stream()
                .collect(toMap(FlowSetting::getKey, i -> new User(i.getValue(), i.getRemark())));
    }

    /**
     * 获取OA审批权限
     */
    @Bean(name = "oaAuthList")
    public List<FlowSetting>  oaAuthList() {
        return FlowSettingRepository.findByTypeOrderByCreateDate(OA_AUTH);
    }

    /**
     * 跳过基础审批（主管审批、技术审批）的管理人员Map：key=userid, value=User
     * @return
     */
    @Bean(name = "flowSkipManagerMap")
    public Map<String, User> flowSkipManager() {
        List<FlowSetting> list = FlowSettingRepository.findByTypeOrderByCreateDate(DEFAULT_SKIP);
        return list.stream()
                .collect(toMap(FlowSetting::getValue, i -> new User(i.getValue(), i.getRemark())));
    }


}
