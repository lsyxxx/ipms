package com.abt.safety.service.impl;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import com.abt.common.util.QueryUtil;
import com.abt.safety.entity.SafetyForm;
import com.abt.safety.entity.SafetyFormItem;
import com.abt.safety.model.CheckType;
import com.abt.safety.model.LocationType;
import com.abt.safety.model.SafetyFormRequestForm;
import com.abt.safety.repository.SafetyFormItemRepository;
import com.abt.safety.repository.SafetyFormRepository;
import com.abt.safety.service.SafetyConfigService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.abt.sys.exception.BusinessException;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import com.abt.safety.entity.SafetyItem;
import com.abt.safety.model.SafeItemRequestForm;
import com.abt.safety.repository.SafetyItemRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SafetyConfigServiceImpl implements SafetyConfigService {

    private final SafetyItemRepository safetyItemRepository;
    private final SafetyFormRepository safetyFormRepository;
    private final SafetyFormItemRepository safetyFormItemRepository;

    public SafetyConfigServiceImpl(SafetyItemRepository safetyItemRepository, SafetyFormRepository safetyFormRepository, SafetyFormItemRepository safetyFormItemRepository) {
        this.safetyItemRepository = safetyItemRepository;
        this.safetyFormRepository = safetyFormRepository;
        this.safetyFormItemRepository = safetyFormItemRepository;
    }


    @Override
    public Page<SafetyItem> getSafetyItemConfigPage(SafeItemRequestForm requestForm) {
        PageRequest pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.ASC, "sortNo"));
        
        // 将字符串类型的checkType转换为枚举类型
        CheckType checkType = null;
        if (requestForm.getCheckType() != null && !requestForm.getCheckType().trim().isEmpty()) {
            try {
                checkType = CheckType.fromString(requestForm.getCheckType());
            } catch (IllegalArgumentException e) {
                // 如果转换失败，使用null值，不过滤checkType
                log.warn("无效的CheckType值: {}", requestForm.getCheckType());
            }
        }
        
        return safetyItemRepository.findByQueryPageable(requestForm.getItemEnabled(), requestForm.getQuery(), checkType, pageable);
    }

    @Override
    public boolean validateDuplicateSafetyItemName(String name, CheckType checkType) {
        Optional<SafetyItem> item = safetyItemRepository.findEnabledByName(name, checkType);
        return item.isPresent();
    }

    @Override
    public void validateBeforAdd(SafetyItem safetyItem) {
        if (safetyItem.getName() == null || safetyItem.getName().isEmpty()) {
            throw new BusinessException("名称不能为空");
        }
        if (safetyItem.getInputType() == null || safetyItem.getInputType().isEmpty()) {
            throw new BusinessException("类型不能为空");
        }

    }

    @Override
    public SafetyItem saveSafetyItemConfig(SafetyItem safetyItem) {
        return safetyItemRepository.save(safetyItem);
    }

    @Override
    public void updateSafetyFormEnabled(String id, boolean enabled) {
        safetyFormRepository.updateEnabled(id, enabled);
    }

    @Override
    public void updateSafetyItemEnabled(String id, boolean enabled) {
        safetyItemRepository.updateEnabled(id, enabled);
    }

    @Override
    public void logicDeleteSafetyItemConfig(String id) {
        safetyItemRepository.logicDeleteById(id);
    }

    @Override
    public SafetyItem getSafetyItem(String id) {
        return safetyItemRepository.findById(id).orElseThrow(() -> new BusinessException("安全检查配置项目不存在(id=" + id + ")"));
    }

    @Override
    public Integer getNextSortNo() {
        // 查询未删除的项目数量
        long count = safetyItemRepository.countByIsDeletedFalse();
        // 返回数量 + 1 作为新的排序号
        return (int) count + 1;
    }

    @Transactional
    @Override
    public SafetyForm saveForm(SafetyForm form) {
        SafetyForm savedForm = safetyFormRepository.save(form);
        if (form.getId() != null) {
            safetyFormItemRepository.deleteByFormId(form.getId());
        }
        if (form.getItems() != null && !form.getItems().isEmpty()) {
            List<SafetyFormItem> itemsToSave = new ArrayList<>();
            for (SafetyFormItem item : form.getItems()) {
                item.setFormId(savedForm.getId());
                itemsToSave.add(item);
            }
            safetyFormItemRepository.saveAll(itemsToSave);
        }
        return savedForm;
    }

    @Override
    public SafetyForm loadSafetyFormWithItems(String id) {
        return safetyFormRepository.findWithItemsById(id)
            .orElseThrow(() -> new BusinessException("未查询到安全检查表单(id=" + id + ")"));
    }

    @Override
    public SafetyForm loadSafetyForm(String id) {
        return safetyFormRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到安全检查表单(id=" + id + ")"));
    }

    @Override
    public Page<SafetyForm> findByQueryPageable(SafetyFormRequestForm requestForm) {
        LocationType locationType = null;
        CheckType checkType = null;
        
        // 安全转换locationType
        if (requestForm.getLocationType() != null && !requestForm.getLocationType().trim().isEmpty()) {
            locationType = LocationType.fromString(requestForm.getLocationType().toUpperCase());
        }
        
        // 安全转换checkType
        if (requestForm.getCheckType() != null && !requestForm.getCheckType().trim().isEmpty()) {
            checkType = CheckType.fromString(requestForm.getCheckType().toUpperCase());
        }

        PageRequest pageRequest = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), 
                                               Sort.by(Sort.Direction.ASC, "sortNo"));
        
        String query = requestForm.getQuery();
        final LocationType finalLocationType = locationType;
        final CheckType finalCheckType = checkType;
        
        Specification<SafetyForm> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 添加未删除条件
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            
            // 添加模糊查询条件
            if (query != null && !query.trim().isEmpty()) {
                String pattern = QueryUtil.like(query.toLowerCase());
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("responsibleJno")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("responsibleName")), pattern)
                ));
            }
            
            // 添加启用状态条件
            if (requestForm.getFormEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), requestForm.getFormEnabled()));
            }
            
            // 添加地点类型条件 - 仅当locationType不为null或空时才添加此条件
            if (finalLocationType != null) {
                predicates.add(criteriaBuilder.equal(root.get("locationType"), finalLocationType));
            }
            
            // 添加检查类型条件 - 仅当checkType不为null或空时才添加此条件
            if (finalCheckType != null) {
                predicates.add(criteriaBuilder.equal(root.get("checkType"), finalCheckType));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return safetyFormRepository.findAll(spec, pageRequest);
    }
    @Override
    public boolean checkSafetyFormLocationExists(String location, Long id, LocationType locationType) {
        if (StringUtils.isBlank(location)) {
            throw new BusinessException("环境安全检查表单地点不能为空");
        }
        
        return safetyFormRepository.checkLocationExists(location, id, locationType);
    }

    @Override
    public void logicDeleteSafetyForm(String id) {
        safetyFormRepository.logicDeleteById(id);
    }

}
