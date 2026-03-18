package com.abt.finance.service;

import com.abt.finance.entity.FixedAsset;
import com.abt.finance.model.FixedAssetRequestForm;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FixedAssetsService {
    /**
     * 根据条件查询
     *
     * @param form 查询条件
     */
    Page<FixedAsset> findListByQuery(FixedAssetRequestForm form);

    List<FixedAsset> findAll();

    void save(FixedAsset fixedAsset);

    void delete(String id);

    String codeGenerator();
}
