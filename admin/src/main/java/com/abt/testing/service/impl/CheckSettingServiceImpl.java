package com.abt.testing.service.impl;

import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.repository.CheckModuleRepository;
import com.abt.chkmodule.repository.CheckUnitRepository;
import com.abt.testing.entity.TCheckmodule;
import com.abt.testing.entity.TCheckunit;
import com.abt.testing.model.CheckModuleSyncError;
import com.abt.testing.model.CheckModuleSyncResult;
import com.abt.testing.repository.TCheckmoduleRepository;
import com.abt.testing.repository.TCheckunitRepository;
import com.abt.testing.service.CheckSettingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 检测配置
 */
@Slf4j
@Service
public class CheckSettingServiceImpl implements CheckSettingService {

    private final CheckModuleRepository checkModuleRepository;
    private final TCheckmoduleRepository tCheckmoduleRepository;
    private final TCheckunitRepository tCheckunitRepository;
    private final CheckUnitRepository checkUnitRepository;


    @PersistenceContext
    private EntityManager entityManager;

    public CheckSettingServiceImpl(CheckModuleRepository checkModuleRepository, TCheckmoduleRepository tCheckmoduleRepository, TCheckunitRepository tCheckunitRepository, CheckUnitRepository checkUnitRepository) {
        this.checkModuleRepository = checkModuleRepository;
        this.tCheckmoduleRepository = tCheckmoduleRepository;
        this.tCheckunitRepository = tCheckunitRepository;
        this.checkUnitRepository = checkUnitRepository;
    }


    @Override
    public void synchronizeTcheckunitDB(ChannelEnum channel) {
        // 仅查询启用的
        final List<TCheckunit> all = tCheckunitRepository.findByIsActive("1");
        final List<CheckUnit> newCu = checkUnitRepository.findByUseChannel(channel);
        List<CheckUnit> add = new ArrayList<>();
        for (TCheckunit tc : all) {
            //是否存在
            final Optional<CheckUnit> first = newCu.stream().filter(i -> i.getName().equals(tc.getFname()))
                    .findFirst();
            if (first.isEmpty()) {
                add.add(copyCheckUnit(tc, channel));
            }
        }
        checkUnitRepository.saveAll(add);
    }

    public CheckUnit copyCheckUnit(TCheckunit tCheckunit, ChannelEnum channel) {
        CheckUnit c = new CheckUnit();
        c.setName(tCheckunit.getFname());
        c.setCode(tCheckunit.getFid());
        c.setEnabled(false);
        c.setUseChannel(channel);
        return c;
    }


    @Override
    public CheckModuleSyncResult synchronizeTcheckmoduleDB(ChannelEnum channel) {
        CheckModuleSyncResult result = new CheckModuleSyncResult();
        List<CheckModuleSyncError> error = new ArrayList<>();
        int size = 100;
        int page = 0;
        boolean hasNext;
        List<CheckModule> insertList = new ArrayList<>();

        final List<CheckUnit> checkUnits = checkUnitRepository.findByUseChannel(channel);

        do {
            PageRequest pr = PageRequest.of(page, size, Sort.by("id"));
            //仅查询已启用的
            final Page<TCheckmodule> data = tCheckmoduleRepository.findByIsActive("1", pr);
            result.setAllCount(data.getTotalElements());
            for (TCheckmodule tc : data.getContent()) {
                final CheckModule cm = copyCheckModule(tc, channel);
                //1. 是否有checkunit
                //新的checkUnit
                final Optional<CheckUnit> optionalCheckUnit = filterCheckUnit(checkUnits, tc.getCheckunitId(), channel);
                if (optionalCheckUnit.isPresent()) {
                    CheckUnit checkUnit = optionalCheckUnit.get();
                    cm.setCheckUnitId(checkUnit.getId());
                } else {
                    CheckModuleSyncError e = new CheckModuleSyncError(tc.getFname(), tc.getFid(), "未查询到检测分类(checkUnitId: " + tc.getCheckunitId() + ")");
                    error.add(e);
                    continue;
                }

                //2. 是否有重复的checkmodule
                final boolean isDup = isDuplicatedCheckModule(tc, channel, cm.getCheckUnitId());
                if (isDup) {
                    CheckModuleSyncError e = new CheckModuleSyncError(tc.getFname(), tc.getFid(), "存在重复名称");
                    error.add(e);
                    continue;
                }

                insertList.add(cm);
            }
            hasNext = data.hasNext();
            page = page + 1;
        } while (hasNext);

        result.setSyncCount(insertList.size());
        checkModuleRepository.saveAll(insertList);

        result.setError(error);
        return result;
    }


    private Optional<CheckUnit> filterCheckUnit(List<CheckUnit> list, String code, ChannelEnum channel) {
        Objects.requireNonNull(code, "检测分类编号不能为空");
        Objects.requireNonNull(channel, "检测分类渠道不能为空");
        return list.stream().filter(i -> i.getCode().equals(code) && i.getUseChannel().equals(channel)).findFirst();
    }

    private CheckModule copyCheckModule(TCheckmodule tc, ChannelEnum channel) {
        CheckModule cm = new CheckModule();
        cm.setStatusTemp();
        cm.setCode(tc.getFid());
        cm.setName(tc.getFname());
        cm.setCheckUnitId(tc.getCheckunitId());
        cm.setNote(tc.getNote());
        cm.setAliasNames(tc.getBreName());
        cm.setUseChannel(channel);
        return cm;
    }

    private boolean isDuplicatedCheckModuleName(String fname, ChannelEnum channel, String checkUnitId) {
        return checkModuleRepository.existsByNameAndUseChannelAndCheckUnitId(fname, channel, checkUnitId);
    }


    /**
     * 是否存在重复的checkmodule
     * 1. 名称一样
     *
     * @param tc t_checkModule数据
     */
    private boolean isDuplicatedCheckModule(TCheckmodule tc, ChannelEnum channel, String checkUnitId) {
        return isDuplicatedCheckModuleName(tc.getFname(), channel,  checkUnitId);

    }
}
