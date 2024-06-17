package com.abt.salary;

import com.abt.common.model.User;
import com.abt.salary.entity.SalaryCell;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.abt.salary.Constants.*;

/**
 * 读取excel，暂存，不保存数据库
 * easyexcel col/row index 都是从0开始
 * 业务上处理index=0 添加信息位，业务数据(excel数据)index=1开始
 */
@Slf4j
@Getter
@Setter
public class SalaryExcelReadListener extends AnalysisEventListener<Map<Integer, String>> {

    private String mainId;

    /**
     * 标题行，从0开始
     */
    public static final int TITLE_ROW_IDX = 0;
    /**
     * 数据起始行
     */
    public static final int DATA_START_IDX = 3;

    /**
     * 工号列号
     */
    private int jobNumberColumnIndex = EXCLUDE_IDX;
    /**
     * 实发工资 列号
     */
    private int netPaidColumnIndex = EXCLUDE_IDX;

    /**
     * 姓名 列号
     */
    private int nameColumnIndex = EXCLUDE_IDX;


    /**
     * 用于前端显示table
     * index=0 表示行信息位
     * index=1 开始是数据
     */
    private List<List<SalaryCell>> tableList = new ArrayList<>();

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;


    /**
     * 组装好的header,
     * key: 列号， value：列名
     */
    Map<Integer, String> mergedHeader = new HashMap<>();
    /**
     * 表头合并前的原始数据
     * key: rowNum, value: 行数据
     */
    Map<Integer, Map<Integer, String>> rawHeader = new HashMap<>();

    private Map<String, List<List<SalaryCell>>> typedErrorMap = new HashMap<>();

    /**
     * 按列存储table
     * key: 列号，value map：该列所有行的数据
     * value map：key:行号，value: 值
     */
    private Map<Integer, Map<Integer, String>> columnMap = new HashMap<>();

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public SalaryExcelReadListener(String mainId) {
        this.mainId = mainId;
        typedErrorMap.put(ERR_JOBNUM_NULL, new ArrayList<>());
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext analysisContext) {
        //空行不读取
        int rowNum = analysisContext.readRowHolder().getRowIndex();
        List<SalaryCell> tableRow = new ArrayList<>();
        String jobNumber = data.getOrDefault(jobNumberColumnIndex - 1, StringUtils.EMPTY);
        String name = data.getOrDefault(nameColumnIndex - 1, StringUtils.EMPTY);
        SalaryCell rowCell = SalaryCell.createRowCell(rowNum, jobNumber, name, mainId);
        tableRow.add(SL_ROW_INFO_IDX, rowCell);
        data.forEach((k, v) -> {
            int tableIndex = k + 1;
            SalaryCell cell = SalaryCell.createTemp(mergedHeader.get(tableIndex), v, rowNum, tableIndex, mainId, jobNumber, rowCell.getRowId());
            tableRow.add(tableIndex, cell);
        });

        tableList.add(tableRow);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成！");
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        final Integer rowIndex = context.readRowHolder().getRowIndex();
        rawHeader.put(rowIndex, headMap);
        this.mergeHeader(headMap);
    }

    /**
     * 合并表头
     * headMap: key=rowIndex(based 1), value = columnIndex(based 1), headerName
     * 合并规则：
     * 第一行是标题，忽略
     * 第二行 至 HEADER_ROW_NUM行：表头，多行是因为可能存在合并单元格情况
     * 同一列，优先取有值的；若一列存在多个值，则使用行号大的列的值，如2, 3行都有值，那么使用3行的值
     * 一般同一列多行有值表示同一类别下的多个分类
     */
    private void mergeHeader(Map<Integer, String> currentMap) {
        //存在infoCell,表头一致，信息位
        this.mergedHeader.put(0, "");
        currentMap.forEach((k, v) -> {
            Integer tableIndex = k + 1;
            if (StringUtils.isNotBlank(v)) {
                //去掉空格/换行/制表符
                v = v.replaceAll("\\s+", "");
                this.mergedHeader.put(tableIndex, v);
            }
            if (NETPAID_COLNAME.equals(v)) {
                this.netPaidColumnIndex = tableIndex;
            } else if (JOBNUMBER_COLNAME.equals(v)) {
                this.jobNumberColumnIndex = tableIndex;
            } else if (NAME_COLNAME.equals(v)) {
                this.nameColumnIndex = tableIndex;
            }
        });
    }

    public boolean includeJobNumber() {
        return this.jobNumberColumnIndex > 0;
    }

    public boolean includeNetPaid() {
        return this.netPaidColumnIndex > 0;
    }

    public boolean includeName() {
        return this.nameColumnIndex > 0;
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
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("SalaryExcelReadMapListener - Failed!", exception);
    }
}
