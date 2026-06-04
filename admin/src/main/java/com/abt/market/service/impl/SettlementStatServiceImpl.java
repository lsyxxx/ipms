package com.abt.market.service.impl;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import com.abt.market.model.*;
import com.abt.market.projection.EntrustSettlementDiffProjection;
import com.abt.market.projection.EntrustSettlementExportProjection;
import com.abt.market.projection.EntrustSampleSettlementExportProjection;
import com.abt.market.projection.EntrustSettlementStatProjection;
import com.abt.market.projection.SettlementYearSummaryProjection;
import com.abt.market.entity.SettlementMain;
import com.abt.market.repository.SettlementMainRepository;
import com.abt.market.repository.SettlementStatRepository;
import com.abt.market.service.SettlementStatService;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.exception.BusinessException;
import com.abt.testing.entity.Entrust;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.repository.InvoiceApplyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 结算统计服务实现。
 */
@Service
public class SettlementStatServiceImpl implements SettlementStatService {
    private static final int COMMON_EXPORT_SHEET_SIZE = 10_000;
    private static final int ENTRUST_ITEM_EXPORT_SHEET_SIZE = 10_000;

    private final SettlementStatRepository settlementStatRepository;
    private final SettlementMainRepository settlementMainRepository;
    private final InvoiceApplyRepository invoiceApplyRepository;

    public SettlementStatServiceImpl(SettlementStatRepository settlementStatRepository,
                                     SettlementMainRepository settlementMainRepository,
                                     InvoiceApplyRepository invoiceApplyRepository) {
        this.settlementStatRepository = settlementStatRepository;
        this.settlementMainRepository = settlementMainRepository;
        this.invoiceApplyRepository = invoiceApplyRepository;
    }

    @Override
    public Page<EntrustSettlementStatDTO> findEntrustStatsPage(SettlementStatRequestForm requestForm) {
        requestForm.forcePaged();
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit());
        return settlementStatRepository.findEntrustStatsPage(
                StringUtils.trim(requestForm.getEntrustId()),
                StringUtils.trim(requestForm.getProjectName()),
                StringUtils.trim(requestForm.getClientName()),
                resolveStartTime(requestForm),
                resolveEndTime(requestForm),
                normalizeStatus(requestForm.getSettlementStatus()),
                pageable
        ).map(this::toStatDto);
    }

    @Override
    public EntrustSettlementStatDTO findEntrustSummary(String entrustId) {
        if (StringUtils.isBlank(entrustId)) {
            throw new BusinessException("项目编号不能为空!");
        }
        EntrustSettlementStatProjection projection = settlementStatRepository.findEntrustStat(entrustId);
        if (projection != null) {
            return toStatDto(projection);
        }
        Entrust entrust = settlementStatRepository.findById(entrustId)
                .orElseThrow(() -> new BusinessException("未查询到项目(" + entrustId + ")"));
        EntrustSettlementStatDTO dto = new EntrustSettlementStatDTO();
        dto.setEntrustId(entrust.getId());
        dto.setProjectName(entrust.getProjectName());
        dto.setClientName(entrust.getJiaFangCompany());
        dto.setTotalCount(0L);
        dto.setSettledCount(0L);
        dto.setUnsettledCount(0L);
        dto.setExtraSettledCount(0L);
        dto.setSettledAmount(BigDecimal.ZERO);
        dto.setSettlementStatus(SettlementStatStatus.UNSETTLED);
        return dto;
    }

    @Override
    public EntrustSettlementDiffDTO findEntrustDiff(String entrustId) {
        EntrustSettlementStatDTO summary = findEntrustSummary(entrustId);
        EntrustSettlementDiffDTO dto = new EntrustSettlementDiffDTO();
        dto.setEntrustId(summary.getEntrustId());
        dto.setProjectName(summary.getProjectName());
        dto.setClientName(summary.getClientName());
        dto.setTotalCount(summary.getTotalCount());
        dto.setSettledCount(summary.getSettledCount());
        dto.setUnsettledCount(summary.getUnsettledCount());
        dto.setExtraSettledCount(summary.getExtraSettledCount());
        dto.setSettledAmount(summary.getSettledAmount());
        dto.setSettlementStatus(summary.getSettlementStatus());

        List<EntrustSettlementDiffItemDTO> items = settlementStatRepository.findEntrustDiffItems(entrustId).stream()
                .map(this::toDiffItemDto)
                .toList();
        dto.setItems(items);
        return dto;
    }

    @Override
    public SettlementYearSummaryDTO findSummary(SettlementStatRequestForm requestForm) {
        LocalDateTime startTime = resolveStartTime(requestForm);
        LocalDateTime endTime = resolveEndTime(requestForm);
        SettlementYearSummaryProjection projection = settlementStatRepository.findSummary(startTime, endTime);
        SettlementYearSummaryDTO dto = new SettlementYearSummaryDTO();
        dto.setYear(requestForm.getYear());
        dto.setSettledEntrustCount(projection == null || projection.getSettledEntrustCount() == null ? 0L : projection.getSettledEntrustCount());
        dto.setPartialSettledEntrustCount(projection == null || projection.getPartialSettledEntrustCount() == null ? 0L : projection.getPartialSettledEntrustCount());
        dto.setUnsettledEntrustCount(projection == null || projection.getUnsettledEntrustCount() == null ? 0L : projection.getUnsettledEntrustCount());
        dto.setSettledAmount(projection == null || projection.getSettledAmount() == null ? BigDecimal.ZERO : projection.getSettledAmount());
        return dto;
    }

    @Override
    public void exportSettlementsByYear(Integer year, OutputStream outputStream) {
        LocalDateTime startTime = yearStart(year);
        LocalDateTime endTime = startTime.plusYears(1);
        List<SettlementMain> settlements = settlementMainRepository.findExportSettlements(startTime, endTime);
        Map<String, List<InvoiceApply>> invoiceMap = loadInvoiceMap(settlements.stream().map(SettlementMain::getId).toList());
        List<SettlementDocumentExportRow> rows = settlements.stream()
                .map(main -> toSettlementDocumentExportRow(main, invoiceMap.getOrDefault(main.getId(), List.of())))
                .toList();
        writeMultiSheet(outputStream, SettlementDocumentExportRow.class, "结算单据", rows, COMMON_EXPORT_SHEET_SIZE);
    }

    @Override
    public void exportEntrustStatsByYear(Integer year, OutputStream outputStream) {
        String yearText = validateYear(year);
        List<EntrustSettlementExportRow> rows = settlementStatRepository.findEntrustExportRowsByContractYear(yearText).stream()
                .map(this::toEntrustSettlementExportRow)
                .toList();
        writeMultiSheet(outputStream, EntrustSettlementExportRow.class, "项目结算情况", rows, COMMON_EXPORT_SHEET_SIZE);
    }

    @Override
    public void exportEntrustSampleSettlementsByYear(Integer year, OutputStream outputStream) {
        String yearText = validateYear(year);
        List<EntrustSampleSettlementExportRow> rows = settlementStatRepository.findEntrustSampleSettlementRows(yearText).stream()
                .map(this::toEntrustSampleSettlementExportRow)
                .toList();
        writeMultiSheet(outputStream, EntrustSampleSettlementExportRow.class, "样品检测结算情况", rows, ENTRUST_ITEM_EXPORT_SHEET_SIZE);
    }

    private EntrustSettlementStatDTO toStatDto(EntrustSettlementStatProjection projection) {
        EntrustSettlementStatDTO dto = new EntrustSettlementStatDTO();
        dto.setEntrustId(projection.getEntrustId());
        dto.setProjectName(projection.getProjectName());
        dto.setClientName(projection.getClientName());
        dto.setTotalCount(defaultLong(projection.getTotalCount()));
        dto.setSettledCount(defaultLong(projection.getSettledCount()));
        dto.setUnsettledCount(defaultLong(projection.getUnsettledCount()));
        dto.setExtraSettledCount(defaultLong(projection.getExtraSettledCount()));
        dto.setSettledAmount(projection.getSettledAmount() == null ? BigDecimal.ZERO : projection.getSettledAmount());
        dto.setSettlementStatus(parseStatus(projection.getSettlementStatus()));
        return dto;
    }

    private EntrustSettlementDiffItemDTO toDiffItemDto(EntrustSettlementDiffProjection projection) {
        EntrustSettlementDiffItemDTO dto = new EntrustSettlementDiffItemDTO();
        dto.setDiffType(SettlementDiffType.valueOf(projection.getDiffType()));
        dto.setEntrustId(projection.getEntrustId());
        dto.setSampleRegistId(projection.getSampleRegistId());
        dto.setSampleNo(projection.getSampleNo());
        dto.setCheckModeuleId(projection.getCheckModeuleId());
        dto.setCheckModeuleName(projection.getCheckModeuleName());
        dto.setTestItemId(projection.getTestItemId());
        dto.setSettlementMainId(projection.getSettlementMainId());
        dto.setSettlementSaveType(projection.getSettlementSaveType());
        dto.setSettlementCreateDate(projection.getSettlementCreateDate());
        dto.setClientName(projection.getClientName());
        return dto;
    }

    private String normalizeStatus(String status) {
        return StringUtils.trimToEmpty(status).toUpperCase();
    }

    private SettlementStatStatus parseStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return SettlementStatStatus.UNSETTLED;
        }
        return SettlementStatStatus.valueOf(status);
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private LocalDateTime resolveStartTime(SettlementStatRequestForm requestForm) {
        applyDatePreset(requestForm);
        return requestForm.toLocalStartTime();
    }

    private LocalDateTime resolveEndTime(SettlementStatRequestForm requestForm) {
        applyDatePreset(requestForm);
        return requestForm.toLocalEndTime();
    }

    private void applyDatePreset(SettlementStatRequestForm requestForm) {
        SettlementStatDatePreset preset = parsePreset(requestForm.getDatePreset());
        if (preset == null || preset == SettlementStatDatePreset.CUSTOM) {
            if (requestForm.getYear() != null && requestForm.getLocalStartDate() == null && requestForm.getLocalEndDate() == null) {
                requestForm.setLocalStartDate(LocalDate.of(requestForm.getYear(), Month.JANUARY, 1));
                requestForm.setLocalEndDate(LocalDate.of(requestForm.getYear(), Month.DECEMBER, 31));
            }
            return;
        }
        LocalDate today = LocalDate.now();
        if (preset == SettlementStatDatePreset.THIS_MONTH) {
            requestForm.setLocalStartDate(today.withDayOfMonth(1));
            requestForm.setLocalEndDate(today.withDayOfMonth(today.lengthOfMonth()));
            return;
        }
        if (preset == SettlementStatDatePreset.THIS_QUARTER) {
            int startMonth = ((today.getMonthValue() - 1) / 3) * 3 + 1;
            LocalDate startDate = LocalDate.of(today.getYear(), startMonth, 1);
            requestForm.setLocalStartDate(startDate);
            requestForm.setLocalEndDate(startDate.plusMonths(3).minusDays(1));
            return;
        }
        if (preset == SettlementStatDatePreset.THIS_YEAR) {
            requestForm.setLocalStartDate(LocalDate.of(today.getYear(), Month.JANUARY, 1));
            requestForm.setLocalEndDate(LocalDate.of(today.getYear(), Month.DECEMBER, 31));
        }
    }

    private SettlementStatDatePreset parsePreset(String preset) {
        if (StringUtils.isBlank(preset)) {
            return null;
        }
        try {
            return SettlementStatDatePreset.valueOf(preset.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("不支持的日期快捷范围: " + preset);
        }
    }

    private SettlementDocumentExportRow toSettlementDocumentExportRow(SettlementMain main, List<InvoiceApply> invoices) {
        SettlementDocumentExportRow row = new SettlementDocumentExportRow();
        row.setSettlementId(main.getId());
        row.setSettlementUnit(main.getCompanyName());
        row.setClientName(main.getClientName());
        row.setTotalAmount(main.getTotalAmount());
        row.setCreateUsername(main.getCreateUsername());
        row.setCreateDate(main.getCreateDate());
        row.setAttachmentNames(joinAttachmentNames(main.getAttachments(), main.getSettlementDocs()));
        row.setInvoiceStatus(resolveInvoiceStatus(invoices));
        return row;
    }

    private String joinAttachmentNames(List<SystemFile> attachments, List<SystemFile> settlementDocs) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        addAttachmentNames(names, attachments);
        addAttachmentNames(names, settlementDocs);
        return String.join(",", names);
    }

    private void addAttachmentNames(LinkedHashSet<String> names, List<SystemFile> files) {
        if (files == null) {
            return;
        }
        files.stream()
                .filter(Objects::nonNull)
                .map(file -> StringUtils.defaultIfBlank(file.getOriginalName(), file.getName()))
                .filter(StringUtils::isNotBlank)
                .forEach(names::add);
    }

    private String resolveInvoiceStatus(List<InvoiceApply> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return "未开票";
        }
        boolean passed = invoices.stream()
                .filter(i -> !i.isDelete())
                .anyMatch(i -> "已通过".equals(i.getBusinessState()));
        if (passed) {
            return "已开票";
        }
        boolean running = invoices.stream()
                .filter(i -> !i.isDelete())
                .anyMatch(i -> "审批中".equals(i.getBusinessState()));
        return running ? "开票中" : "未开票";
    }

    private Map<String, List<InvoiceApply>> loadInvoiceMap(Collection<String> settlementIds) {
        if (settlementIds == null || settlementIds.isEmpty()) {
            return Map.of();
        }
        return invoiceApplyRepository.findBySettlementIdIn(settlementIds).stream()
                .collect(Collectors.groupingBy(InvoiceApply::getSettlementId));
    }

    private String validateYear(Integer year) {
        if (year == null || year < 2000 || year > 2999) {
            throw new BusinessException("年份不能为空且必须是4位有效年份");
        }
        return year.toString();
    }

    private LocalDateTime yearStart(Integer year) {
        validateYear(year);
        return LocalDate.of(year, 1, 1).atStartOfDay();
    }

    private String toSettlementStatusText(SettlementStatStatus status) {
        return switch (status) {
            case SETTLED -> "全部结算";
            case PARTIALLY_SETTLED -> "部分结算";
            case UNSETTLED -> "未结算";
        };
    }

    private EntrustSampleSettlementExportRow toEntrustSampleSettlementExportRow(EntrustSampleSettlementExportProjection projection) {
        EntrustSampleSettlementExportRow row = new EntrustSampleSettlementExportRow();
        row.setEntrustId(projection.getEntrustId());
        row.setContractNo(projection.getContractNo());
        row.setClientName(projection.getClientName());
        row.setProjectName(projection.getProjectName());
        row.setSampleRegistId(projection.getSampleRegistId());
        row.setCheckModeuleId(projection.getCheckModeuleId());
        row.setCheckModeuleName(projection.getCheckModeuleName());
        row.setSettledStatus(projection.getSettledStatus());
        row.setSettlementIds(projection.getSettlementIds());
        return row;
    }

    private EntrustSettlementExportRow toEntrustSettlementExportRow(EntrustSettlementExportProjection projection) {
        EntrustSettlementExportRow row = new EntrustSettlementExportRow();
        row.setEntrustId(projection.getEntrustId());
        row.setContractNo(projection.getContractNo());
        row.setClientName(projection.getClientName());
        row.setProjectName(projection.getProjectName());
        row.setSettlementStatus(toSettlementStatusText(resolveStatus(
                defaultLong(projection.getTotalCount()),
                defaultLong(projection.getSettledCount()),
                defaultLong(projection.getExtraSettledCount())
        )));
        row.setSettlementIds(projection.getSettlementIds());
        return row;
    }

    private SettlementStatStatus resolveStatus(long totalCount, long settledCount, long extraSettledCount) {
        if (totalCount > 0 && settledCount >= totalCount) {
            return SettlementStatStatus.SETTLED;
        }
        if (settledCount == 0 && extraSettledCount == 0) {
            return SettlementStatStatus.UNSETTLED;
        }
        return SettlementStatStatus.PARTIALLY_SETTLED;
    }

    private <T> void writeMultiSheet(OutputStream outputStream, Class<T> clazz, String sheetBaseName, List<T> rows, int sheetSize) {
        try (ExcelWriter excelWriter = EasyExcel.write(outputStream, clazz)
                .autoCloseStream(Boolean.FALSE)
                .build()) {
            if (rows == null || rows.isEmpty()) {
                WriteSheet emptySheet = EasyExcel.writerSheet(0, sheetBaseName + "_1").build();
                excelWriter.write(List.of(), emptySheet);
                return;
            }
            int sheetNo = 0;
            for (int start = 0; start < rows.size(); start += sheetSize) {
                int end = Math.min(start + sheetSize, rows.size());
                List<T> chunk = rows.subList(start, end);
                WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, sheetBaseName + "_" + (sheetNo + 1)).build();
                excelWriter.write(chunk, writeSheet);
                sheetNo++;
            }
        }
    }
}
