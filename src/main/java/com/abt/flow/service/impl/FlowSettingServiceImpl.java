package com.abt.flow.service.impl;

import com.abt.common.model.User;
import com.abt.flow.model.entity.FlowSetting;
import com.abt.flow.repository.FlowSettingRepository;
import com.abt.flow.service.FlowSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.abt.flow.config.FlowBusinessConfig.DEFAULT_AUDITOR;
import static com.abt.flow.config.FlowBusinessConfig.DEFAULT_SKIP;

/**
 *
 */
@Service
@Slf4j
public class FlowSettingServiceImpl implements FlowSettingService {

    private final FlowSettingRepository flowSettingRepository;

    public FlowSettingServiceImpl(FlowSettingRepository flowSettingRepository) {
        this.flowSettingRepository = flowSettingRepository;
    }

    @Override
    public List<FlowSetting> load() {
        return flowSettingRepository.findAll();
    }

    @Override
    public void addOne(FlowSetting setting) {
        flowSettingRepository.save(setting);
    }

    /**
     * 初始报销默认审批人
     */
    @Override
    public void addReimburseAuditor() {
        List<FlowSetting> list = new ArrayList<>();
        String str = """
                    ceo|U20230406002|何爱平
                    fiManager|U20230406013|冯雅琴
                    taxOfficer|U20230406007|叶冼婷
                    accountancy|U20230406007|叶冼婷
                    cashier|U20230406007|叶冼婷
                    """;
        str.lines().forEach(i -> {
            i = i.trim();
            String[] split = i.split("\\|");
            FlowSetting s = new FlowSetting(split[0], split[1]);
            s.setRemark(split[2]);
            s.setType(DEFAULT_AUDITOR);
            list.add(s);
        });
        flowSettingRepository.saveAll(list);

    }

    @Override
    public void addDefaultFlowSkip() {
        List<FlowSetting> list = new ArrayList<>();
        String str = """
                flowSkipManager|U20230406026|耿丽珍
                flowSkipManager|U20230406020|张亚芹
                flowSkipManager|U20230406016|刘启
                flowSkipManager|U20230406011|闫旭光
                flowSkipManager|U20230406007|叶冼婷
                flowSkipManager|U20230406013|冯雅琴
                flowSkipManager|20230406002|何爱平
                flowSkipManager|U20230406006|何爱生
                flowSkipManager|U20230406034|李佳璐
                """;
        str.lines().forEach(i -> {
            i = i.trim();
            String[] split = i.split("\\|");
            FlowSetting s = new FlowSetting(split[0], split[1]);
            s.setRemark(split[2]);
            s.setType(DEFAULT_SKIP);
            list.add(s);
        });
        flowSettingRepository.saveAll(list);
    }

    @Override
    public boolean isManager(User user) {
        return false;
    }


}
