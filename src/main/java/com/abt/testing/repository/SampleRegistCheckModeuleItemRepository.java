package com.abt.testing.repository;

import com.abt.testing.entity.Entrust;
import com.abt.testing.entity.SampleRegistCheckModeuleItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * 样品关联检测项目
 */
public interface SampleRegistCheckModeuleItemRepository  extends JpaRepository<SampleRegistCheckModeuleItem, String> {


    List<SampleRegistCheckModeuleItem> findBySampleRegistIdIn(Collection<String> sampleRegistIds);

    List<SampleRegistCheckModeuleItem> findByCheckModeuleIdIn(Collection<String> checkModeuleIds);

    List<SampleRegistCheckModeuleItem> findAllByEntrustIdIn(Collection<String> entrustIds);
    
    /**
     * 根据委托单ID列表查询，支持排序
     */
    List<SampleRegistCheckModeuleItem> findAllByEntrustIdIn(Collection<String> entrustIds, Sort sort);
}
