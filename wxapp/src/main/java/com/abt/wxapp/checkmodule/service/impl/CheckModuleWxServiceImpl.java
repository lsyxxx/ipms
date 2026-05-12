package com.abt.wxapp.checkmodule.service.impl;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.entity.CheckModule;
import com.abt.chkmodule.entity.CheckStandard;
import com.abt.chkmodule.entity.CheckUnit;
import com.abt.chkmodule.entity.DynamicScheme;
import com.abt.chkmodule.model.ChannelEnum;
import com.abt.chkmodule.repository.CheckItemRepository;
import com.abt.chkmodule.repository.CheckModuleRepository;
import com.abt.chkmodule.repository.CheckUnitRepository;
import com.abt.chkmodule.service.DynamicSchemeService;
import com.abt.wxapp.checkmodule.model.CheckModuleDetailVo;
import com.abt.wxapp.checkmodule.model.DynamicSchemeVo;
import com.abt.wxapp.checkmodule.model.ProjectHomeItemVo;
import com.abt.wxapp.checkmodule.model.ProjectListItemVo;
import com.abt.wxapp.checkmodule.service.CheckModuleWxService;
import com.abt.wxapp.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 小程序检测项目查询
 */
@Service
@RequiredArgsConstructor
public class CheckModuleWxServiceImpl implements CheckModuleWxService {

    private final CheckModuleRepository checkModuleRepository;
    private final CheckUnitRepository checkUnitRepository;
    private final CheckItemRepository checkItemRepository;
    private final DynamicSchemeService dynamicSchemeService;

    @Override
    public List<ProjectListItemVo> findList() {
        List<CheckModule> modules = checkModuleRepository
                .findByUseChannelAndEnabledTrueAndStatusOrderByNameAsc(ChannelEnum.WECHAT, CheckModule.STATUS_PUBLISHED);
        if (modules.isEmpty()) {
            return List.of();
        }
        Set<String> unitIds = modules.stream().map(CheckModule::getCheckUnitId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, CheckUnit> unitMap = checkUnitRepository.findAllById(unitIds).stream()
                .collect(Collectors.toMap(CheckUnit::getId, u -> u, (a, b) -> a));
        List<String> moduleIds = modules.stream().map(CheckModule::getId).toList();
        Map<String, List<CheckItem>> itemsByModule = checkItemRepository.findByCheckModuleIdIn(moduleIds).stream()
                .collect(Collectors.groupingBy(CheckItem::getCheckModuleId));

        List<ProjectListItemVo> out = new ArrayList<>();
        for (CheckModule m : modules) {
            CheckUnit u = unitMap.get(m.getCheckUnitId());
            String cat = u != null ? u.getCode() : "all";
            List<CheckItem> items = itemsByModule.getOrDefault(m.getId(), List.of());
            List<String> certs = buildCertificates(items);
            String itemsBrief = items.stream()
                    .map(CheckItem::getName)
                    .filter(Objects::nonNull)
                    .limit(8)
                    .collect(Collectors.joining("、"));
            out.add(new ProjectListItemVo(
                    m.getId(),
                    m.getName(),
                    cat,
                    Optional.ofNullable(m.getNotice()).orElse(""),
                    Optional.ofNullable(m.getDuration()).orElse(""),
                    Optional.ofNullable(m.getPrice()).orElse(""),
                    Optional.ofNullable(m.getCoverImage()).orElse(""),
                    itemsBrief,
                    certs,
                    m.getStatus()
            ));
        }
        return out;
    }

    @Override
    public CheckModuleDetailVo findById(String id) {
        CheckModule m = checkModuleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("检测项目不存在"));
        List<CheckItem> items = checkItemRepository.findByModuleId(m.getId());
        List<String> certs = buildCertificates(items);

        CheckModuleDetailVo vo = new CheckModuleDetailVo();
        vo.setId(m.getId());
        vo.setName(m.getName());
        vo.setImage(Optional.ofNullable(m.getCoverImage()).orElse(""));
        vo.setDurationText(Optional.ofNullable(m.getDuration()).orElse("完成周期以实际安排为准"));
        vo.setIntro(Optional.ofNullable(m.getNote()).orElse(""));
        vo.setNotice(Optional.ofNullable(m.getNotice()).orElse(""));
        vo.setSampleRequirementText("");
        vo.setSampleRequirementImages(List.of());
        vo.setResultDisplayText(Optional.ofNullable(m.getResultDescription()).orElse(""));
        vo.setResultDisplayImages(List.of());
        vo.setInstruments(List.of());
        vo.setTestFlow(null);
        vo.setStandards(List.of());
        vo.setCertificate(certs);
        vo.setHasCMA(certs.contains(CheckModule.CERTIFICATE_CMA));
        vo.setHasCNAS(certs.contains(CheckModule.CERTIFICATE_CNAS));

        List<CheckModuleDetailVo.TestParameterVo> tps = new ArrayList<>();
        for (CheckItem ci : items) {
            if (!ci.isEnabled()) {
                continue;
            }
            CheckModuleDetailVo.TestParameterVo tp = new CheckModuleDetailVo.TestParameterVo();
            tp.setName(ci.getName());
            tp.setRestrict(Optional.ofNullable(ci.getRestrict()).orElse(""));
            List<String> stdCodes = Optional.ofNullable(ci.getStandards()).orElse(List.of()).stream()
                    .map(CheckStandard::getCode)
                    .filter(Objects::nonNull)
                    .toList();
            tp.setStandardCodes(stdCodes);
            tps.add(tp);
        }
        vo.setTestParameters(tps);
        return vo;
    }

    @Override
    public List<ProjectHomeItemVo> findHomeList() {
        return findList().stream()
                .limit(6)
                .map(p -> new ProjectHomeItemVo(
                        p.getName(),
                        p.getItems() != null && !p.getItems().isBlank() ? p.getItems() : p.getDesc()
                ))
                .toList();
    }

    @Override
    public DynamicSchemeVo findFormSchemaById(Long formSchemaId) {
        DynamicScheme scheme = dynamicSchemeService.findById(formSchemaId)
                .orElseThrow(() -> new BusinessException("未找到表单配置"));
        return new DynamicSchemeVo(
                scheme.getId(),
                scheme.getCheckModuleId(),
                scheme.getCheckModuleName(),
                scheme.getTitle(),
                scheme.getDescription(),
                scheme.getComponents(),
                scheme.getStatus()
        );
    }

    private static List<String> buildCertificates(List<CheckItem> items) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (CheckItem ci : items) {
            if (!ci.isEnabled()) {
                continue;
            }
            if (ci.isCma()) {
                set.add(CheckModule.CERTIFICATE_CMA);
            }
            if (ci.isCnas()) {
                set.add(CheckModule.CERTIFICATE_CNAS);
            }
        }
        return new ArrayList<>(set);
    }
}
