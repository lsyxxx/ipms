package com.abt.wf;

import com.abt.common.model.User;
import com.abt.sys.model.entity.SystemSetting;
import com.abt.sys.repository.SystemSettingRepository;
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

    private final SystemSettingRepository systemSettingRepository;

    public WorkFlowConfig(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    @Bean
    @Lazy
    public Map<String, List<String>> SystemSettingList() {
        List<SystemSetting> all = systemSettingRepository.findAll();
        return all.stream()
                .collect(groupingBy(SystemSetting::getKey
                        , mapping(SystemSetting::getValue, toList())));
    }

    /**
     * 默认审批用户
     */
    @Bean(name = "flowDefaultAuditorMap")
    public Map<String, User> defaultAuditor() {
        List<SystemSetting> list = systemSettingRepository.findByTypeOrderByCreateDate(DEFAULT_AUDITOR);
        return list.stream()
                .collect(toMap(SystemSetting::getKey, i -> new User(i.getValue(), i.getRemark())));
    }

    /**
     * 获取OA审批权限
     */
    @Bean(name = "oaAuthList")
    public List<SystemSetting>  oaAuthList() {
        return systemSettingRepository.findByTypeOrderByCreateDate(OA_AUTH);
    }

    /**
     * 跳过基础审批（主管审批、技术审批）的管理人员Map：key=userid, value=User
     * @return
     */
    @Bean(name = "flowSkipManagerMap")
    public Map<String, User> flowSkipManager() {
        List<SystemSetting> list = systemSettingRepository.findByTypeOrderByCreateDate(DEFAULT_SKIP);
        return list.stream()
                .collect(toMap(SystemSetting::getValue, i -> new User(i.getValue(), i.getRemark())));
    }


}
