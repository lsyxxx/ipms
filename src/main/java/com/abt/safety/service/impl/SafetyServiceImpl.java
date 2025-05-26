package com.abt.safety.service.impl;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import com.abt.common.util.QueryUtil;
import com.abt.safety.entity.SafetyForm;
import com.abt.safety.entity.SafetyFormItem;
import com.abt.safety.model.SafetyFormRequestForm;
import com.abt.safety.repository.SafetyFormItemRepository;
import com.abt.safety.repository.SafetyFormRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.abt.safety.service.SafetyService;
import com.abt.sys.exception.BusinessException;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import com.abt.safety.entity.SafetyItem;
import com.abt.safety.model.SafeItemRequestForm;
import com.abt.safety.repository.SafetyItemRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SafetyServiceImpl implements SafetyService {

    private final SafetyItemRepository safetyItemRepository;
    private final SafetyFormRepository safetyFormRepository;
    private final SafetyFormItemRepository safetyFormItemRepository;

    public SafetyServiceImpl(SafetyItemRepository safetyItemRepository, SafetyFormRepository safetyFormRepository, SafetyFormItemRepository safetyFormItemRepository) {
        this.safetyItemRepository = safetyItemRepository;
        this.safetyFormRepository = safetyFormRepository;
        this.safetyFormItemRepository = safetyFormItemRepository;
    }

    @Override
    public Page<SafetyItem> getSafetyItemConfigList(SafeItemRequestForm requestForm) {
        PageRequest pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Direction.ASC, "sortNo"));
        return safetyItemRepository.findByQueryPageable(requestForm.getItemEnabled(), requestForm.getQuery(), pageable);
    }

    @Override
    public boolean validateDuplicateSafetyItemName(String name) {
        Optional<SafetyItem> item = safetyItemRepository.findEnabledByName(name);
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
        if (StringUtils.isNotBlank(form.getId())) {
            safetyFormItemRepository.deleteByFormId(form.getId());
        }
        if (form.getItems() != null && !form.getItems().isEmpty()) {
//            for (int i = 0; i < form.getItems().size(); i++) {
//                final SafetyFormItem formItem = form.getItems().get(i);
//                formItem.setFormId(form.getId());
//                formItem.setSortNo(i);
//            }
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
        return safetyFormRepository.findWithItemsById(id).orElseThrow(() -> new BusinessException("未查询到安全检查表单(id=" + id + ")"));
    }

    @Override
    public SafetyForm loadSafetyForm(String id) {
        return safetyFormRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到安全检查表单(id=" + id + ")"));
    }

    @Override
    public Page<SafetyForm> findByQueryPageable(SafetyFormRequestForm requestForm) {
        PageRequest pageRequest = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), 
                                               Sort.by(Sort.Direction.ASC, "location"));
        
        String query = requestForm.getQuery();
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

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return safetyFormRepository.findAll(spec, pageRequest);
    }
    @Override
    public boolean checkSafetyFormLocationExists(String location, String id) {
        if (StringUtils.isBlank(location)) {
            throw new BusinessException("安全检查表单地点不能为空");
        }
        
        return safetyFormRepository.checkLocationExists(location, id);
    }

    @Override
    public void logicDeleteSafetyForm(String id) {
        safetyFormRepository.logicDeleteById(id);
    }

}
