package com.abt.salary.service;

import com.abt.common.model.ValidationResult;
import com.abt.salary.entity.SalaryDetail;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.util.ListUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
@Getter
@Setter
public class SalaryExcelReadListener extends AnalysisEventListener<SalaryDetail> {

    private SalaryService salaryService;
    private String mainId;
    /**
     * 临时表
     */
    private List<SalaryDetail> tempSalaryDetails = new ArrayList<>();

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    private List<SalaryDetail> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private Map<SalaryDetail, ValidationResult> errorDetailMap = new HashMap<>();
    private Map<Integer, Map<Integer, String>> headMap = new HashMap<>();
    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public SalaryExcelReadListener(SalaryService salaryService, String mainId) {
        this.salaryService = salaryService;
        this.mainId = mainId;
    }

    @Override
    public void invoke(SalaryDetail salaryDetail, AnalysisContext analysisContext) {
        salaryDetail.setMainId(this.mainId);
        tempSalaryDetails.add(salaryDetail);
        //校验
        final ValidationResult result = salaryService.salaryDetailRowCheck(salaryDetail);
        if (!result.isPass()) {
            errorDetailMap.put(salaryDetail, result);
        }
//        if (result.isPass()) {
//            cachedDataList.add(salaryDetail);
//            // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
//            if (cachedDataList.size() >= BATCH_COUNT) {
//                saveData();
//                // 存储完成清理 list
//                cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
//            }
//        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("所有数据解析完成！");
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        final Integer rowIndex = context.readRowHolder().getRowIndex();
        this.headMap.put(rowIndex, headMap);
        log.info("读取表头数据: rowIndex: {}, {}", rowIndex, headMap);
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        final Integer firstColumnIndex = extra.getFirstColumnIndex();
        final Integer firstRowIndex = extra.getFirstRowIndex();
        final String text = extra.getText();
//        log.info("读取合并单元格数据: [col,row]: [{},{}], text: {}", firstRowIndex, firstColumnIndex, text);
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败!", exception);
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        salaryService.saveSalaryDetailBatch(cachedDataList);
        log.info("存储数据库成功！");
    }
}
