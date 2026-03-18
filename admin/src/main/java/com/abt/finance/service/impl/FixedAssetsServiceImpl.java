package com.abt.finance.service.impl;

import com.abt.finance.entity.FixedAsset;
import com.abt.finance.model.FixedAssetRequestForm;
import com.abt.finance.repository.FixedAssetRepository;
import com.abt.finance.service.FixedAssetsService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.util.WithQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 *
 */
@Service
@Slf4j
public class FixedAssetsServiceImpl implements FixedAssetsService {

    private final FixedAssetRepository fixedAssetRepository;

    public FixedAssetsServiceImpl(FixedAssetRepository fixedAssetRepository) {
        this.fixedAssetRepository = fixedAssetRepository;
    }

    @Override
    public Page<FixedAsset> findListByQuery(FixedAssetRequestForm form) {
        Pageable pageable = PageRequest.of(form.jpaPage(), form.getSize());
        final Page<FixedAsset> byQuery = fixedAssetRepository.findByQuery(form.getQuery(), form.getAssetType(), form.getDept(), pageable);
        WithQueryUtil.build(byQuery.getContent());
        return byQuery;
    }

    @Override
    public List<FixedAsset> findAll() {
        return WithQueryUtil.build(fixedAssetRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate")));
    }

    @Override
    public void save(FixedAsset fixedAsset) {
        this.validateCode(fixedAsset.getCode(), fixedAsset.getId());
        final String deptStr = generateDeptStr(fixedAsset.getUsageDeptList());
        fixedAsset.setUsageDept(deptStr);
        fixedAssetRepository.save(fixedAsset);
    }

    private String generateDeptStr(List<String> deptList) {
        if (CollectionUtils.isEmpty(deptList)) {
            return null;
        }
        return String.join(",", deptList);
    }

    private void validateCode(String code, String id) {
        final FixedAsset byCode = fixedAssetRepository.findByCode(code);
        if (byCode != null && Objects.equals(byCode.getId(), id)) {
            throw new BusinessException("资产编号已存在!");
        }
    }

    @Override
    public void delete(String id) {
        fixedAssetRepository.deleteById(id);
    }

    /**
     * 生成资产编号
     */
    @Override
    public String codeGenerator() {
        final long count = fixedAssetRepository.count();
        return String.format("%05d", (count+1));
    }


}
