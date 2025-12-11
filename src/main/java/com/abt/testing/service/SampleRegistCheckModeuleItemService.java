package com.abt.testing.service;

import com.abt.testing.entity.SampleRegistCheckModeuleItem;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SampleRegistCheckModeuleItemService {
    /**
     * 根据委托单ID列表查询
     * @param entrustIds 委托单ID列表
     * @param sort 排序方式，如果为null则使用默认排序（entrustId升序，createDate降序）
     * @return 样品检测项目列表
     */
    List<SampleRegistCheckModeuleItem> findByEntrustIds(List<String> entrustIds, Sort sort);
}
