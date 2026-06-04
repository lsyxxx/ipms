package com.abt.market.service;

import com.abt.market.model.EntrustSettlementDiffDTO;
import com.abt.market.model.EntrustSettlementStatDTO;
import com.abt.market.model.SettlementStatRequestForm;
import com.abt.market.model.SettlementYearSummaryDTO;
import org.springframework.data.domain.Page;

import java.io.OutputStream;

/**
 * 结算统计服务。
 */
public interface SettlementStatService {
    /**
     * 按项目分页查询结算状态。
     */
    Page<EntrustSettlementStatDTO> findEntrustStatsPage(SettlementStatRequestForm requestForm);

    /**
     * 查询单个项目的结算汇总。
     */
    EntrustSettlementStatDTO findEntrustSummary(String entrustId);

    /**
     * 查询单个项目的结算差异明细。
     */
    EntrustSettlementDiffDTO findEntrustDiff(String entrustId);

    /**
     * 查询指定日期范围的结算汇总。
     */
    SettlementYearSummaryDTO findSummary(SettlementStatRequestForm requestForm);

    /**
     * 导出指定年份的结算单据。
     */
    void exportSettlementsByYear(Integer year, OutputStream outputStream);

    /**
     * 导出指定年份的项目结算情况。
     */
    void exportEntrustStatsByYear(Integer year, OutputStream outputStream);

    /**
     * 导出指定年份的项目样品检测结算情况。
     */
    void exportEntrustSampleSettlementsByYear(Integer year, OutputStream outputStream);
}
