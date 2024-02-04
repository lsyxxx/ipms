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

import java.util.*;
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

    private final StandardRepository standardRepository;

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    public BasicDataServiceImpl(TypeRepository typeRepository, CompanyRepository companyRepository, ProductRepository productRepository, MaterialRepository materialRepository, CompanyRelationRepository companyRelationRepository, PriceRepository priceRepository, ContactRepository contactRepository, StandardRepository standardRepository) {
        this.typeRepository = typeRepository;
        this.companyRepository = companyRepository;
        this.productRepository = productRepository;
        this.materialRepository = materialRepository;
        this.companyRelationRepository = companyRelationRepository;
        this.priceRepository = priceRepository;
        this.contactRepository = contactRepository;
        this.standardRepository = standardRepository;
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
    public Product saveProduct(Product form) {
        Product product = productRepository.save(form);
        form.setId(product.getId());

        final List<Material> materials = saveMaterials(form);
        List<Standard> standards = createStandards(form);
        standards = standardRepository.saveAllAndFlush(standards);

        final List<CompanyRelation> relations = createCompanyRelations(form);
        companyRelationRepository.saveAllAndFlush(relations);
        List<Company> company = companyRelationRepository.findCompanyByChemicalId(form.getId());

        List<Price> price = createPrice(form);
        price = priceRepository.saveAllAndFlush(price);

         List<Contact> contact = createContact(form);
        contact = contactRepository.saveAllAndFlush(contact);
        buildProduct(product, standards, materials, company, price, contact);

        return product;
    }

    private List<Material> saveMaterials(Product form) {
        List<Material> materials = new ArrayList<>();
        materials.addAll(form.getMainMaterial());
        materials.addAll(form.getAuxMaterial());
        materials.forEach(i -> i.setChemicalId(form.getId()));
        materialRepository.deleteByChemicalId(form.getId());
        return materialRepository.saveAllAndFlush(materials);
    }

    private void buildProduct(Product product, List<Standard> standard, List<Material> materials, List<Company> company, List<Price> price, List<Contact> contact) {
        String chemicalId = product.getId();
        product.setStandards(standard);
        product.setMainMaterial(materials.stream().filter(m -> Material.TYPE_MAIN.equals(m.getType())).collect(Collectors.toList()));
        product.setAuxMaterial(materials.stream().filter(m -> Material.TYPE_AUX.equals(m.getType())).collect(Collectors.toList()));

        buildCompany(company, price, contact, chemicalId);
        product.setProducers(company.stream().filter(c -> Company.TYPE_PRODUCER.equals(c.getType())).collect(Collectors.toList()));
        product.setBuyers((company.stream().filter(c -> Company.TYPE_BUYER.equals(c.getType())).collect(Collectors.toList())));
    }

    private void buildCompany(List<Company> company, List<Price> price, List<Contact> contact, String chemicalId) {
        company.forEach(i -> {
            i.setChemicalId(chemicalId);
            i.setContactList(contact.stream().filter(c -> c.getCompanyId().equals(i.getId())).collect(Collectors.toList()));
            i.setPriceList(price.stream().filter(p -> p.getCompanyId().equals(i.getId())).collect(Collectors.toList()));
        });
    }

    private List<Standard> createStandards(Product form) {
        ensureProductId(form.getId());
        List<Standard> list = new ArrayList<>();
        form.getStandards().forEach(s -> {
            list.add(Standard.of(s.getCode(), form.getId(), s.getId()));
        });
        return list;
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



    @Override
    public Product queryProductById(String id) {
        final Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new BusinessException("未查询到该化学品(id=" + id + ")");
        }
        Product product = optionalProduct.get();
        String chemicalId = product.getId();
        final List<Material> materials = materialRepository.findByChemicalId(chemicalId);
        final List<Standard> standards = standardRepository.findByChemicalIdOrderByNameAsc(chemicalId);
        List<Company> company = companyRelationRepository.findCompanyByChemicalId(chemicalId);
        final List<Price> prices = priceRepository.findByChemicalIdOrderByCompanyIdAsc(chemicalId);
        final List<Contact> contacts = contactRepository.findByChemicalIdOrderByCompanyIdAsc(chemicalId);

        buildProduct(product, standards, materials, company, prices, contacts);

        return product;
    }
}
