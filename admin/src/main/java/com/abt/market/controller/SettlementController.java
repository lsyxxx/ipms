package com.abt.market.controller;

import cn.idev.excel.FastExcel;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.market.entity.SettlementMain;
import com.abt.market.entity.SettlementSummary;
import com.abt.market.entity.StlmSmryTemp;
import com.abt.market.entity.StlmTestTemp;
import com.abt.market.model.*;
import com.abt.market.service.SettlementService;
import com.abt.market.service.SettlementStatService;
import com.abt.sys.model.dto.UserView;
import com.abt.testing.entity.SampleRegistCheckModeuleItem;
import com.abt.testing.service.SampleRegistCheckModeuleItemService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.wf.entity.InvoiceApply;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 结算
 */
@RestController
@Slf4j
@RequestMapping("/stlm")
public class SettlementController {
    private final SettlementService settlementService;
    private final SettlementStatService settlementStatService;
    private final SampleRegistCheckModeuleItemService sampleRegistCheckModeuleItemService;

    public SettlementController(SettlementService settlementService, SettlementStatService settlementStatService, SampleRegistCheckModeuleItemService sampleRegistCheckModeuleItemService) {
        this.settlementService = settlementService;
        this.settlementStatService = settlementStatService;
        this.sampleRegistCheckModeuleItemService = sampleRegistCheckModeuleItemService;
    }

    @PostMapping("/save/draft")
    public R<Object> saveDraft(@RequestBody SettlementMain settlementMain) {
        settlementMain.setSaveType(SaveType.TEMP);
        settlementService.save(settlementMain);
        return R.success("草稿保存成功");
    }

    @PostMapping("/save")
    public R<Object> save(@Validated({ValidateGroup.Save.class}) @RequestBody SettlementMain settlementMain) {
        settlementMain.setSaveType(SaveType.SAVE);
        settlementService.save(settlementMain);
        return R.success("保存成功");
    }

    @GetMapping("/find/page")
    public R<Page<SettlementMainListDTO>> findByQuery(@ModelAttribute SettlementRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<SettlementMainListDTO> page = settlementService.findMainOnlyByQuery(requestForm, pageable);
        return R.success(page);
    }

    @GetMapping("/detail/{id}")
    public R<SettlementMain> loadEntity(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("结算单id未传入");
        }
        final SettlementMain entity = settlementService.findSettlementMainWithAllItems(id);
        return R.success(entity, "加载成功");
    }

    /**
     * 删除结算单
     */
    @GetMapping("/delete")
    public R<Object> delete(String id) {
        final SettlementMain main = settlementService.findSettlementMainOnly(id);
        return R.success("删除成功");
    }

    /**
     * 获取所有结算客户
     */
    @GetMapping("/clients")
    public R<List<CustomerInfo>> getClients() {
        final List<CustomerInfo> clients = settlementService.getClients();
        return R.success(clients);
    }

    @GetMapping("/detail/export")
    public void exportAndDownload(String id, HttpServletResponse response) {
        try {
            // 获取结算单数据
            SettlementMain settlementMain = settlementService.findSettlementMainWithAllItems(id);
            
            // 构建文件名
            String fileName = "结算单_" + settlementMain.getClientName() + "_" + id + ".xlsx";
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
            
            settlementService.createSettlementExcel(settlementMain, response.getOutputStream());
            
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("下载结算单失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 结算单作废
     */
    @GetMapping("/invalid")
    public R<Object> invalid(String id, String reason) {
        final SettlementMain main = settlementService.findSettlementMainOnly(id);
        //1. 只能自己作废
        final UserView user = TokenUtil.getUserFromAuthToken();
        if (!user.getId().equals(main.getCreateUserid())) {
            throw new BusinessException(String.format("只有创建人(%s)可以作废该结算单(%s)", main.getCreateUsername(), main.getId()));
        }
        main.setInvalidReason(reason);
        settlementService.invalid(main);
        return R.success("作废成功");
    }


    /**
     * 结算单作废前校验
     */
    @GetMapping("/invalid/prepare")
    public R<List<String>> invalidPrepare(String id) {
        // 是否存在已开票、进行中的流程
        final List<InvoiceApply> runInvs = settlementService.hasRunningOrPassInvoice(id);
        if (!CollectionUtils.isEmpty(runInvs)) {
            List<String> ids = runInvs.stream().map(InvoiceApply::getId).toList();
            return R.success(ids, "");
        }
        return R.success(List.of(), "");
    }

    /**
     * 保存关联
     */
    @PostMapping("/rel/update")
    public R<Object> saveRelations(@RequestBody RelationRequest relationRequest) {
        settlementService.updateRelations(relationRequest);
        return R.success("更新成功!");
    }

    /**
     * 按样品导入
     * @param file 导入文件
     */
    @PostMapping("/import/bysample")
    public R<Object> importBySamples(@RequestParam("file") MultipartFile file) throws IOException {
        List<ImportSample> list = FastExcel.read(file.getInputStream())
                .head(ImportSample.class)
                .sheet()  // 默认读取第一个sheet
                .doReadSync();
        final String tempMid = settlementService.importBySamples(list);
        return R.success(tempMid, "导入成功");
    }

    @GetMapping("/testTemp/delAll")
    public R<Object> deleteAllTestItemTemp(String tempMid) {
        settlementService.deleteTempByTempMid(tempMid);
        return R.success("删除成功");
    }


    /**
     * 获取stlm_test_temp数据（分页）
     */
    @GetMapping("/testTemp/query")
    public R<Page<StlmTestTemp>> findTempTestByQuery(@ModelAttribute TestTempRequestForm  requestForm) {
        final Page<StlmTestTemp> page = settlementService.findTestTempByQuery(requestForm);
        return R.success(page, "查询成功");
    }

    /**
     * 根据临时数据生成summaryData
     */
    @GetMapping("/testTemp/createSummaryTable")
    public R<List<SettlementSummary>> createSummaryDataByTestTemp(String tempMid) {
        final List<SettlementSummary> summary = settlementService.createSummaryTableByTestTemp(tempMid);
        return R.success(summary, "生成成功");
    }

    /**
     * 导出委托单样品
     * @param entrustIds 委托单ID列表字符串，用,分隔
     * @param response HTTP响应
     */
    @GetMapping("/export/samples")
    public void exportEntrustSamples(@RequestParam String entrustIds, HttpServletResponse response) {
        if (StringUtils.isBlank(entrustIds)) {
            throw new BusinessException("项目编号不能为空!");
        }
        try {
            List<String> ids = List.of(entrustIds.split(","));
            final List<SampleRegistCheckModeuleItem> samples = sampleRegistCheckModeuleItemService.findByEntrustIds(ids, null);
            // 构建文件名
            String fileName = "样品清单_" + System.currentTimeMillis() + ".xlsx";
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
            // 导出Excel
            settlementService.exportTestTempToExcel(samples, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("导出委托单样品失败", e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 导入汇总数据
     *
     * @param file: 导入文件
     */
    @PostMapping("/import/bysummary")
    public R<String> importSummaryData(@RequestParam("file") MultipartFile file) throws Exception {
        final String tempId = settlementService.importBySummaryData(file);
        return R.success(tempId, "导入成功");
    }


    @GetMapping("/find/smrytemp")
    public R<List<StlmSmryTemp>> getTempSummaryData(String tempMid) {
        final List<StlmSmryTemp> list = settlementService.getTempSummaryData(tempMid);
        return R.success(list, "查询成功");
    }


    @GetMapping("/find/stlmAgr")
    public R<List<SettlementAgreementDTO>> findSettlementAgreementDTOList(String contractNo) {
        final List<SettlementAgreementDTO> list = settlementService.findSettlementsByContractNo(contractNo);
        return R.success(list);
    }

    /**
     * 查询项目是否存在有效结算单。
     */
    @GetMapping("/stats/entrust/status")
    public R<SettlementStatDTO> findSettlementStatusByEntrust(String entrustId) {
        final SettlementStatDTO dto = settlementService.findSettlementStatusByEntrustId(entrustId);
        return R.success(dto);
    }

    /**
     * 按项目编号汇总结算情况。
     */
    @GetMapping("/stats/entrust")
    public R<Page<SettlementStatDTO>> findSettlementStatsByEntrust(@ModelAttribute SettlementStatRequestForm requestForm) {
        final Page<SettlementStatDTO> page = settlementService.findSettlementStatsByEntrust(requestForm);
        return R.success(page);
    }

    /**
     * 按客户分页查询结算情况，支持空条件查全部和客户名称模糊查询。
     */
    @GetMapping("/stats/client")
    public R<Page<SettlementStatDTO>> findSettlementStatsByClient(@ModelAttribute SettlementStatRequestForm requestForm) {
        final Page<SettlementStatDTO> page = settlementService.findSettlementStatsByClient(requestForm);
        return R.success(page);
    }

    /**
     * 按合同分页查询结算情况，支持空条件查全部和合同编号/名称模糊查询。
     */
    @GetMapping("/stats/contract")
    public R<Page<SettlementStatDTO>> findSettlementStatsByContract(@ModelAttribute SettlementStatRequestForm requestForm) {
        final Page<SettlementStatDTO> page = settlementService.findSettlementStatsByContract(requestForm);
        return R.success(page);
    }

    /**
     * 按项目编号查询关联结算单分页列表。
     */
    @GetMapping("/find/byEntrust")
    public R<Page<SettlementAgreementDTO>> findSettlementsByEntrust(@ModelAttribute SettlementDetailRequestForm requestForm) {
        final Page<SettlementAgreementDTO> page = settlementService.findSettlementsByEntrustId(requestForm);
        return R.success(page);
    }

    /**
     * 查询指定客户关联的结算单分页列表。
     */
    @GetMapping("/detail/page/client")
    public R<Page<SettlementDetailDTO>> findSettlementDetailsByClient(@ModelAttribute SettlementDetailRequestForm requestForm) {
        final Page<SettlementDetailDTO> page = settlementService.findSettlementDetailsByClientId(requestForm);
        return R.success(page);
    }

    /**
     * 按项目分页查询结算状态。
     * 参数：
     * entrustId：项目编号，支持模糊查询
     * projectName：项目名称，支持模糊查询
     * clientName：客户名称，支持模糊查询
     * settlementStatus：SETTLED / PARTIALLY_SETTLED / UNSETTLED
     * startDate / endDate：自定义日期范围，包含结束日期
     * datePreset：THIS_MONTH / THIS_QUARTER / THIS_YEAR / CUSTOM
     * year：兼容旧用法；当未传 startDate/endDate 且未传 CUSTOM 时，可传 year 表示整年范围
     * page / limit：分页参数
     */
    @GetMapping("/stat/entrust/page")
    public R<Page<EntrustSettlementStatDTO>> findEntrustStatPage(@ModelAttribute SettlementStatRequestForm requestForm) {
        final Page<EntrustSettlementStatDTO> page = settlementStatService.findEntrustStatsPage(requestForm);
        return R.success(page);
    }

    /**
     * 查询单个项目的结算汇总状态。
     * 参数：
     * entrustId：项目编号，必填
     */
    @GetMapping("/stat/entrust/summary")
    public R<EntrustSettlementStatDTO> findEntrustSummary(String entrustId) {
        final EntrustSettlementStatDTO dto = settlementStatService.findEntrustSummary(entrustId);
        return R.success(dto);
    }

    /**
     * 查询单个项目的结算差异明细。
     * 参数：
     * entrustId：项目编号，必填
     * 返回：
     * 1. MATCHED：源数据与结算明细都存在
     * 2. UNSETTLED：源数据存在，但未进入结算
     * 3. EXTRA_SETTLED：结算明细存在，但源数据中不存在
     */
    @GetMapping("/stat/entrust/diff")
    public R<EntrustSettlementDiffDTO> findEntrustDiff(String entrustId) {
        final EntrustSettlementDiffDTO dto = settlementStatService.findEntrustDiff(entrustId);
        return R.success(dto);
    }

    /**
     * 查询日期范围结算汇总。
     * 参数：
     * startDate / endDate：自定义日期范围，包含结束日期
     * datePreset：THIS_MONTH / THIS_QUARTER / THIS_YEAR / CUSTOM
     * year：兼容旧用法；当未传 startDate/endDate 时，可传 year 表示整年范围
     */
    @GetMapping("/stat/summary")
    public R<SettlementYearSummaryDTO> findSummary(@ModelAttribute SettlementStatRequestForm requestForm) {
        final SettlementYearSummaryDTO dto = settlementStatService.findSummary(requestForm);
        return R.success(dto);
    }

    /**
     * 兼容旧路径，内部仍按日期范围统计。
     */
    @GetMapping("/stat/year/summary")
    public R<SettlementYearSummaryDTO> findSummaryCompat(@ModelAttribute SettlementStatRequestForm requestForm) {
        final SettlementYearSummaryDTO dto = settlementStatService.findSummary(requestForm);
        return R.success(dto);
    }

    /**
     * 导出指定年份的所有结算单据。
     * 字段：
     * 结算单号、结算单位、客户、结算金额、创建人、创建日期、附件名称、开票状态
     */
    @GetMapping("/stat/export/settlements")
    public void exportSettlementsByYear(@RequestParam Integer year, HttpServletResponse response) {
        exportExcel(response, "结算单据_" + year + ".xlsx", outputStream ->
                settlementStatService.exportSettlementsByYear(year, outputStream));
    }

    /**
     * 导出指定年份的项目及结算情况。
     * 年份口径：Entrust.htBianHao 包含该年份，例如 AJC2025001Y001 表示 2025 年。
     */
    @GetMapping("/stat/export/entrusts")
    public void exportEntrustStatsByYear(@RequestParam Integer year, HttpServletResponse response) {
        exportExcel(response, "项目结算情况_" + year + ".xlsx", outputStream ->
                settlementStatService.exportEntrustStatsByYear(year, outputStream));
    }

    /**
     * 导出指定年份所有项目的样品和检测项目结算情况。
     * 年份口径：Entrust.htBianHao 包含该年份。
     */
    @GetMapping("/stat/export/entrust-items")
    public void exportEntrustItemsByYear(@RequestParam Integer year, HttpServletResponse response) {
        exportExcel(response, "项目样品检测结算情况_" + year + ".xlsx", outputStream ->
                settlementStatService.exportEntrustSampleSettlementsByYear(year, outputStream));
    }

    @FunctionalInterface
    private interface ExportAction {
        void write(OutputStream outputStream) throws Exception;
    }

    private void exportExcel(HttpServletResponse response, String fileName, ExportAction exportAction) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
            exportAction.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("导出Excel失败", e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }
}
