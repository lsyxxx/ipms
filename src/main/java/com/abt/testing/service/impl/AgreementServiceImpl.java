package com.abt.testing.service.impl;

import com.abt.common.config.CommonSpecification;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.testing.entity.Agreement;
import com.abt.testing.entity.Entrust;
import com.abt.testing.entity.EnumLib;
import com.abt.testing.model.AgreementRequestForm;
import com.abt.testing.repository.AgreementRepository;
import com.abt.testing.repository.EntrustRepository;
import com.abt.testing.service.AgreementService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.abt.common.util.QueryUtil.like;

/**
 * 合同相关
 */
@Service
@Slf4j
public class AgreementServiceImpl implements AgreementService {

    private final AgreementRepository agreementRepository;
    private final CustomerInfo abtCompany;
    private final CustomerInfo grdCompany;
    private final EntrustRepository entrustRepository;

    private final Map<String, CustomerInfo> companyMap;
    private final Map<String, EnumLib> agreementTypeEnumMap;
    private final List<EnumLib> agreementTypeEnumList;

    public AgreementServiceImpl(AgreementRepository agreementRepository, CustomerInfo abtCompany, CustomerInfo grdCompany, EntrustRepository entrustRepository,
                                Map<String, CustomerInfo> companyMap, @Qualifier("agreementTypeEnumMap") Map<String, EnumLib> agreementTypeEnumMap,
                                @Qualifier("agreementTypeEnumList") List<EnumLib> agreementTypeEnumList) {
        this.agreementRepository = agreementRepository;
        this.abtCompany = abtCompany;
        this.grdCompany = grdCompany;
        this.entrustRepository = entrustRepository;
        this.companyMap = companyMap;
        this.agreementTypeEnumMap = agreementTypeEnumMap;
        this.agreementTypeEnumList = agreementTypeEnumList;
    }

    public void validateAgreement(Agreement agreement) {
        final boolean dup = doValidateDuplicateAgreementCode(agreement);
        if (dup) {
            throw new BusinessException("合同编号已存在(" + agreement.getAgreementCode() + ")!");
        }

        String companyId = agreement.getYCompanyId();
        if (abtCompany.getId().equals(companyId)) {
            final boolean abtCode = doValidateABTAgreementCodeRule(agreement.getAgreementCode());
            if (!abtCode) {
                throw new BusinessException("合同编号不符合阿伯塔编号规则(AJC+年份+3位编号)");
            }
        } else if (grdCompany.getId().equals(companyId)) {
            final boolean grdCode = doValidateGRDAgreementCodeRule(agreement.getAgreementCode());
            if (!grdCode) {
                throw new BusinessException("合同编号不符合吉瑞达编号规则(JC+年份+3位编号)");
            }
        } else {
            throw new BusinessException("缺少合同乙方信息");
        }
    }

    /**
     * 校验合同编号是否重复
     */
    public boolean doValidateDuplicateAgreementCode(Agreement agreement) {
        if (StringUtils.isBlank(agreement.getId())) {
            return agreementRepository.existsByAgreementCode(agreement.getAgreementCode());
        }
        final Agreement found = agreementRepository.findByAgreementCode(agreement.getAgreementCode());
        //id不同但code同
        return found != null && !found.getId().equals(agreement.getId());
    }

    /**
     * 校验阿伯塔合同编号规则
     */
    public boolean doValidateABTAgreementCodeRule(String agreementCode) {
        String abtReg = "^AJC\\d{4}\\d{3}$";
        Pattern pattern = Pattern.compile(abtReg);
        Matcher matcher = pattern.matcher(agreementCode);
        return matcher.matches();
    }

    /**
     * 校验吉瑞达
     */
    public boolean doValidateGRDAgreementCodeRule(String agreementCode) {
        String jrdReg = "^JC\\d{4}\\d{3}$";
        Pattern pattern = Pattern.compile(jrdReg);
        Matcher matcher = pattern.matcher(agreementCode);
        return matcher.matches();
    }


    @Override
    public void savePreAgreement(Agreement agreement) {
        validateAgreement(agreement);
        build(agreement);
        agreementRepository.save(agreement);
    }


    @Override
    public void deletePreAgreement(String entityId) {
        final Agreement entity = findEntity(entityId);
        validateBeforePreAgreementDelete(entity.getAgreementCode());
        agreementRepository.deleteById(entityId);
    }

    public Agreement findEntity(String entityId) {
        return agreementRepository.findById(entityId).orElseThrow(() -> new BusinessException("未查询到预登记合同(id:" + entityId + ")"));
    }


    @Override
    public Agreement load(String id) {
        Agreement entity = findEntity(id);
        build(entity);
        return entity;
    }

    public Agreement build(Agreement entity) {
        //合同分类
        entity.setAgreementTypeName(agreementTypeEnumMap.get(entity.getAgreementType()).getFname());
        //company
        if (entity.getJCompany() != null) {
            entity.setJCompanyName(entity.getJCompany().getCustomerName());
        }
        if (entity.getYCompany() != null) {
            entity.setYCompanyName(entity.getYCompany().getCustomerName());
        }
        return entity;
    }


    /**
     * 删除合同前验证
     */
    public void validateBeforePreAgreementDelete(String agreementCode) {
        final List<Entrust> itemList = entrustRepository.findByhtBianHao(agreementCode);
        if (itemList != null && !itemList.isEmpty()) {
            //存在
            String itemIds = itemList.stream().map(Entrust::getId).collect(Collectors.joining(", "));
            throw new BusinessException("合同已关联业务(" + itemIds + "), 无法删除。请联系管理员");
        }
    }


    @Override
    public Page<Agreement> findAgreementsPagedBy(AgreementRequestForm requestForm) {
        //1. 模糊查询: 根据“合同编号”“合同名称”“甲方”模糊搜索合同
        //2. 合同类型(agreementType), 乙方(abt/grd, yCompanyId)
        //3. 分页
        requestForm.forcePaged();
        AgreementSpecifications specifications = new AgreementSpecifications();
        Specification<Agreement> criteria = Specification.where(
                        Specification.anyOf(specifications.agreementCodeLike(requestForm.getQuery())
                                , specifications.hasAgreementNameLike(requestForm.getQuery())
                                , specifications.hasJCustomerNameLike(requestForm.getQuery())))
                .and(specifications.yCompanyIdEqual(requestForm.getYCompanyId()))
                .and(specifications.typeEqual(requestForm, "agreementType"));
        Pageable paged = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("agreementCode")));
        Page<Agreement> all = agreementRepository.findAll(criteria, paged);
        all.getContent().forEach(this::build);
        return all;
    }

    @Override
    public List<EnumLib> findAgreementEnumTypes() {
        return this.agreementTypeEnumList;
    }

    static class AgreementSpecifications extends CommonSpecification<AgreementRequestForm, Agreement> {
        public Specification<Agreement> agreementCodeLike(String search) {
            return (root, query, builder) -> {
                if (search == null || search.isEmpty()) {
                    return null;
                }
                return builder.like(root.get("agreementCode"), like(search));
            };
        }


        public Specification<Agreement> hasAgreementNameLike(String search) {
            return (root, query, builder) -> {
                if (search == null || search.isEmpty()) {
                    return null;
                }
                return builder.like(root.get("agreementName"), like(search));
            };
        }

        public Specification<Agreement> hasJCustomerNameLike(String search) {
            return (root, query, builder) -> {
                if (search == null || search.isEmpty()) {
                    return null;
                }
                Join<Agreement, CustomerInfo> customerJoin = root.join("jCompany", JoinType.LEFT);
                return builder.like(customerJoin.get("customerName"), like(search));
            };
        }

        public Specification<Agreement> yCompanyIdEqual(String search) {
            return (root, query, builder) -> {
                if (search == null || search.isEmpty()) {
                    return null;
                }
                return builder.equal(root.get("yCompanyId"), search);
            };
        }
    }


}
