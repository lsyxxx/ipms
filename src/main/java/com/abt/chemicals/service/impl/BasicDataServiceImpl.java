package com.abt.chemicals.service.impl;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.entity.Company;
import com.abt.chemicals.repository.CompanyRepository;
import com.abt.chemicals.repository.TypeRepository;
import com.abt.chemicals.service.BasicDataService;
import com.abt.common.util.MessageUtil;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class BasicDataServiceImpl implements BasicDataService {

    private final TypeRepository typeRepository;
    private final CompanyRepository companyRepository;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public BasicDataServiceImpl(TypeRepository typeRepository, CompanyRepository companyRepository) {
        this.typeRepository = typeRepository;
        this.companyRepository = companyRepository;
    }


    @Override
    public List<ChemicalType> queryType(String name, int level, String type1Id) {
        ChemicalType condition = new ChemicalType();
        condition.setLevel(level);
        condition.setParentId(type1Id);
        if (StringUtils.isNotBlank(name)) {
            condition.setName(name);
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher::contains)
                .withIgnorePaths("enable", "sort");
        Example<ChemicalType> example = Example.of(condition, matcher);
        final List<ChemicalType> list = typeRepository.findAll(example, Sort.by("parentId", "sort").ascending());
        final List<ChemicalType> type1 = typeRepository.findByLevel(ChemicalType.LEVEL_1);
        final Map<String, ChemicalType> type1Map = type1.stream().collect(Collectors.toMap(ChemicalType::getId, chm -> chm));
        list.forEach(t2 -> {
            t2.setParent(type1Map.get(t2.getParentId()));
        });
        return list;
    }

    @Override
    public List<ChemicalType> queryAllTypes() {
        List<ChemicalType> type1 = typeRepository.findByLevel(ChemicalType.LEVEL_1);
        List<ChemicalType> type2 = typeRepository.findByLevel(ChemicalType.LEVEL_2);
        return buildTypeTree(type1, type2);
    }

    /**
     * 组装tree
     */
    public List<ChemicalType> buildTypeTree(List<ChemicalType> type1, List<ChemicalType> type2) {
        type1.forEach(i -> {
            i.setChildren(type2.stream().filter(t2 -> t2.getParentId().equals(i.getId())).collect(Collectors.toList()));
        });
        type2.forEach(i -> {
            type1.stream().filter(t1 -> t1.getId().equals(i.getParentId())).findFirst()
                    .ifPresent(i::setParent);
        });
        return type1;
    }

    @Override
    public long deleteType(String id) {
        final boolean exists = typeRepository.existsById(id);
        long deleted = 0L;
        if(exists) {
            typeRepository.deleteById(id);
            typeRepository.deleteByParentId(id);
        } else {
            log.warn(messages.getMessage("chm.type.notExists", new Object[]{id, ""}));
            throw new BusinessException(messages.getMessage("chm.type.notExists", new Object[]{id, ""}));
        }
        return deleted;
    }

    @Override
    public ChemicalType editType(ChemicalType form) {
        ensureLevel(form);
        ensureParentId(form);
        if (StringUtils.isNotBlank(form.getId())) {
            final boolean exists = typeRepository.existsById(form.getId());
            if (!exists) {
                throw new BusinessException(messages.getMessage("chm.type.notExists", new Object[]{form.getId(), form.getName()}));
            }
        }
        return typeRepository.save(form);
    }

    private void ensureParentId(ChemicalType form) {
        if (form.getLevel() == ChemicalType.LEVEL_2) {
            if (StringUtils.isBlank(form.getParentId())) {
                throw new BusinessException(messages.getMessage("chm.type.parentId.blank", new Object[]{form.getId(), form.getName()}));
            }
        }
    }
    private void ensureLevel(ChemicalType form) {
        if (form.getLevel() != ChemicalType.LEVEL_1 && form.getLevel() != ChemicalType.LEVEL_2) {
            throw new BusinessException(messages.getMessage("chm.type.level.error", new Object[]{form.getLevel()}));
        }
    }

    @Override
    public List<Company> queryAllCompanyByType(String type) {
        return companyRepository.findByType(type);
    }

    public List<Company> queryCompany(String type, String name) {
        Company condition = new Company();
        condition.setType(type);
        condition.setName(name);
        condition.setFullName(name);

        return null;

    }
}
