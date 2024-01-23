package com.abt.chemicals.service.impl;

import com.abt.chemicals.entity.*;
import com.abt.chemicals.repository.*;
import com.abt.chemicals.service.BasicDataService;
import com.abt.common.config.QueryConfig;
import com.abt.common.util.MessageUtil;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class BasicDataServiceImpl implements BasicDataService {

    private final TypeRepository typeRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final CompanyRelationRepository companyRelationRepository;
    private final PriceRepository priceRepository;
    private final ContactRepository contactRepository;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public BasicDataServiceImpl(TypeRepository typeRepository, CompanyRepository companyRepository, ProductRepository productRepository, MaterialRepository materialRepository, CompanyRelationRepository companyRelationRepository, PriceRepository priceRepository, ContactRepository contactRepository) {
        this.typeRepository = typeRepository;
        this.companyRepository = companyRepository;
        this.productRepository = productRepository;
        this.materialRepository = materialRepository;
        this.companyRelationRepository = companyRelationRepository;
        this.priceRepository = priceRepository;
        this.contactRepository = contactRepository;
    }


    @Override
    public List<ChemicalType> queryType(String name, int level, String type1Id) {
        ChemicalType condition = ChemicalType.condition();
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

    @Override
    public List<ChemicalType> queryAllTypesEnabled() {
        final List<ChemicalType> type1 = typeRepository.findByLevelAndEnableOrderBySortAsc(ChemicalType.LEVEL_1, true);
        final List<ChemicalType> type2 = typeRepository.findByLevelAndEnableOrderBySortAsc(ChemicalType.LEVEL_2, true);
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
    public ChemicalType saveType(ChemicalType form) {
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
        return companyRepository.findByTypeOrderBySortAsc(type);
    }

    @Override
    public List<Company> queryAllCompanyEnabled() {
        return companyRepository.findAllByEnableIsTrueOrderByTypeAsc(Sort.by("sort"));
    }

    @Override
    public List<Company> queryCompany(String type, String name) {
        return companyRepository.findByTypeAndNameContainingOrderBySortAsc(type, name);

    }


    @Override
    public Company saveCompany(Company form) {
        if (StringUtils.isBlank(form.getName())) {
            throw new BusinessException(messages.getMessage("chm.company.name.blank"));
        }
        return companyRepository.save(form.prepare());
    }


    @Override
    public void deleteCompany(String id) {
        companyRepository.deleteById(id);
        priceRepository.deleteByCompanyId(id);
        contactRepository.deleteByCompanyId(id);
    }


    @Override
    public void saveProduct(Product form) {
        Product product = productRepository.save(form);
        form.setId(product.getId());
        final List<Material> materials = createMaterials(form);
        materialRepository.saveAllAndFlush(materials);
        final List<CompanyRelation> relations = createCompanyRelations(form);
        companyRelationRepository.saveAllAndFlush(relations);
        final List<Price> price = createPrice(form);
        priceRepository.saveAllAndFlush(price);
        final List<Contact> contact = createContact(form);
        contactRepository.saveAllAndFlush(contact);
    }

    private List<Contact> createContact(Product form) {
        ensureProductId(form.getId());
        List<Contact> list = new ArrayList<>();
        form.getProducers().forEach(company -> {
            company.getContactList().forEach(i -> {
                i.setCompanyId(company.getId());
                i.setChemicalId(form.getId());
                list.add(i);
            });
        });
        form.getBuyers().forEach(company -> {
            company.getContactList().forEach(i -> {
                i.setCompanyId(company.getId());
                i.setChemicalId(form.getId());
                list.add(i);
            });
        });
        return list;
    }

    private List<Material> createMaterials(Product form) {
        ensureProductId(form.getId());
        List<Material> list = new ArrayList<>();
        if (!form.getMainMaterial().isEmpty()) {
            form.getMainMaterial().forEach(i -> {
                list.add(Material.of(i, Material.TYPE_MAIN, form.getId()));
            });
        }
        if (!form.getAuxMaterial().isEmpty()) {
            form.getAuxMaterial().forEach(i -> {
                list.add(Material.of(i, Material.TYPE_AUX, form.getId()));
            });
        }
        return list;
    }

    private List<Price> createPrice(Product form) {
        ensureProductId(form.getId());
        List<Price> list = new ArrayList<>();
        form.getProducers().forEach(company -> {
            company.getPriceList().forEach(p -> {
                p.setChemicalId(form.getId());
                p.setCompanyId(company.getId());
                list.add(p);
            });
        });
        form.getBuyers().forEach(company -> {
            company.getPriceList().forEach(p -> {
                p.setChemicalId(form.getId());
                p.setCompanyId(company.getId());
                list.add(p);
            });
        });
        return list;
    }

    private List<CompanyRelation> createCompanyRelations(Product form) {
        ensureProductId(form.getId());
        List<CompanyRelation> list = new ArrayList<>();
        if (!form.getProducers().isEmpty()) {
            form.getProducers().forEach(i -> {
                i.setType(Company.TYPE_PRODUCER);
                list.add(CompanyRelation.of(form.getId(), i));
            });
        }
        if (!form.getBuyers().isEmpty()) {
            form.getBuyers().forEach(i -> {
                i.setType(Company.TYPE_BUYER);
                list.add(CompanyRelation.of(form.getId(), i));
            });
        }
        return list;
    }

    private void ensureProductId(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("化学品Id不能为空!");
        }
    }

    @Override
    public Page<Company> dynamicCompanyQuery(String name, String type, Boolean enable, Integer page, Integer size) {
        Company condition = Company.condition();
        Page<Company> list;
        if (StringUtils.isNotBlank(name)) {
            condition.setName(name);
        }
        if (Company.validateType(type)) {
            condition.setType(type);
        }
        if (!Objects.isNull(enable)) {
            condition.setEnable(enable);
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatcher::contains);
        Example<Company> example = Example.of(condition, matcher);
        Sort sort = Sort.by("type", "sort");
        if (size != QueryConfig.SIZE_QUERY_ALL) {
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            return companyRepository.findAll(example, pageRequest);
        } else {
            final List<Company> all = companyRepository.findAll(example, sort);
            return new PageImpl<>(all);
        }
    }
}
