package com.abt.salary;

import com.abt.salary.entity.SalaryCell;
import com.abt.salary.model.UserSalaryDetail;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.enums.CellExtraTypeEnum;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.metadata.CellExtra;
import cn.idev.excel.support.ExcelTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static com.abt.salary.Constants.*;

/**
 * 读取excel，暂存，不保存数据库
 * easyexcel col/row index 都是从0开始
 */
@Slf4j
@Getter
@Setter
public class SalaryExcelReadListener extends AnalysisEventListener<Map<Integer, String>> {

    private String mainId;

    private String yearMonth;

    /**
     * 标题行，从0开始
     */
    public static final int TITLE_ROW_IDX = 0;
    /**
     * 数据起始行
     */
    public static final int DATA_START_IDX = 3;

    /**
     * 一级标题 行号 0-based
     */
    public static final int HEADER_L1_RAW_IDX = 1;

    /**
     * 工号列号 0-based
     */
    private int jobNumberRawColumnIndex = EXCLUDE_IDX;
    /**
     * 实发工资 列号 0-based
     */
    private int netPaidRawColumnIndex = EXCLUDE_IDX;

    /**
     * 姓名 列号 0-based
     */
    private int nameRawColumnIndex = EXCLUDE_IDX;

    /**
     * 用工成本
     */
    private int empCostColumnIndex = EXCLUDE_IDX;

    /**
     * 部门
     */
    private int deptRawColumnIndex = 3;


    /**
     * 用于前端显示table
     */
    private List<List<SalaryCell>> tableList = new ArrayList<>();

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;


    /**
     * 组装好的header, 将所有标题合并为一行。key: 列号， value：列名
     * 合并规则：
     * 1.使用有值的
     * 2. 同一列，不同行都有值，使用最后一行的，如1，2，3行都有值，那么使用第三行的数据
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
     * 合并单元格
     */
    List<CellExtra> extraCellList = new ArrayList<>();

    //表格所有原始数据，包含title
    Map<Integer, Map<Integer, String>> rawTable = new HashMap<>();

    /**
     * 人数合计
     */
    private int sumEmp = 0;

    /**
     * 用工成本合计
     */
    private BigDecimal sumCost = BigDecimal.ZERO;

    /**
     * 本月实发工资合计
     */
    private BigDecimal sumNetPaid = BigDecimal.ZERO;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public SalaryExcelReadListener(String mainId, String yearMonth) {
        this.mainId = mainId;
        this.yearMonth = yearMonth;
        typedErrorMap.put(ERR_JOBNUM_NULL, new ArrayList<>());
    }

    //会读取超过预期的行，比如一整行单元格都有颜色，那么读取该行会认为所有有颜色的单元格
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext analysisContext) {
        //空行不读取
        int rowNum = analysisContext.readRowHolder().getRowIndex();
        rawTable.put(rowNum, data);
        List<SalaryCell> tableRow = new ArrayList<>();
        String jobNumber = data.getOrDefault(jobNumberRawColumnIndex , StringUtils.EMPTY);
        String name = data.getOrDefault(nameRawColumnIndex, StringUtils.EMPTY);
        String cost = data.getOrDefault(empCostColumnIndex, StringUtils.EMPTY);
        sumCost = add(cost, sumCost);
        String netPaid = data.getOrDefault(netPaidRawColumnIndex, StringUtils.EMPTY);
        sumNetPaid = add(netPaid, sumNetPaid);
        sumEmp = sumEmp + 1;
        //不能根据表头读取数据，因为表头mergeHeader()方法不会保存没有数据的表头列。若存在表头无值，但是数据行有值，那么导致缺少数据
        //所以先手动限制列数
        data.forEach((k, v) -> {
            //会读取多余的列，这里手动限制，防止无效数据太多
            if (k < 100) {
                //二级标题
                String header2 = this.mergedHeader.get(k);
                SalaryCell cell = SalaryCell.createTemp(header2, v, rowNum, k, mainId, jobNumber);
                cell.setName(name);
                cell.setYearMonth(yearMonth);
                tableRow.add(cell);
            }
        });

        tableList.add(tableRow);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("doAfterAllAnalysed...");
        this.extraCellList.forEach(i -> divideAndFulfilMergedCell(i, this.rawTable));
        fulfilParentLabel();
    }

    private BigDecimal add(String value, BigDecimal start) {
        try {
            BigDecimal v = new BigDecimal(value);
            return start.add(v);
        } catch (Exception e) {
            log.error("无法转为数字类型" + e.getMessage());
        }
        return start;
    }

    /**
     * salaryCell填充父标题
     */
    private void fulfilParentLabel() {
        this.tableList.forEach(row -> {
            row.forEach(cell -> {
                cell.setParentLabel(getHeader1(cell));
            });
        });
    }

    private String getHeader1(SalaryCell cell) {
        return this.rawHeader.get(HEADER_L1_RAW_IDX).get(cell.getColumnIndex());
    }

    //先读取表头再读取合并单元格
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        final Integer rowIndex = context.readRowHolder().getRowIndex();
        rawHeader.put(rowIndex, headMap);
        rawTable.put(rowIndex, headMap);
        this.mergeHeader(headMap);
    }

    /**
     * 合并表头
     * headMap: key=rowIndex(based 1), value = columnIndex(based 1), headerName
     * 第一行：表格标题
     * 第二行：一级标题
     * 第三行：二级标题
     */
    private void mergeHeader(Map<Integer, String> currentMap) {
        currentMap.forEach((k, v) -> {
            if (StringUtils.isNotBlank(v)) {
                //去掉空格/换行/制表符
                v = v.replaceAll("\\s+", "");
                this.mergedHeader.put(k, v);
            }
            if (NETPAID_COLNAME.equals(v)) {
                this.netPaidRawColumnIndex = k;
            } else if (JOBNUMBER_COLNAME.equals(v)) {
                this.jobNumberRawColumnIndex = k;
            } else if (NAME_COLNAME.equals(v)) {
                this.nameRawColumnIndex = k;
            } else if (EMP_COST_NAME.equals(v)) {
                this.empCostColumnIndex = k;
            }
        });
    }

    public boolean includeJobNumber() {
        return this.jobNumberRawColumnIndex > 0;
    }

    public boolean includeNetPaid() {
        return this.netPaidRawColumnIndex > 0;
    }

    public boolean includeName() {
        return this.nameRawColumnIndex > 0;
    }

    public boolean includeEmpCost() {
        return this.empCostColumnIndex > 0;
    }


    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
//        final Integer firstColumnIndex = extra.getFirstColumnIndex();
//        final Integer firstRowIndex = extra.getFirstRowIndex();
//        final Integer lastRowIndex = extra.getLastRowIndex();
//        final Integer lastColumnIndex = extra.getLastColumnIndex();
        extraCellList.add(extra);
//        log.info("读取合并单元格数据: First[row, col]: [{},{}], Last[row, col]: [{},{}]", firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex);
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("SalaryExcelReadMapListener - Failed!", exception);
    }

    /**
     * 拆分和填充合并单元格
     */
    private void divideAndFulfilMergedCell(CellExtra extra, Map<Integer, Map<Integer, String>> rawTable) {
        final Integer firstColumnIndex = extra.getFirstColumnIndex();
        final Integer firstRowIndex = extra.getFirstRowIndex();
        final Integer lastRowIndex = extra.getLastRowIndex();
        final Integer lastColumnIndex = extra.getLastColumnIndex();
        //填充值，(firstRowIndex, firstColumnIndex)
        String cellValue = rawTable.get(firstRowIndex).get(firstColumnIndex);
        if (cellValue == null) {
            cellValue = "";
        }
        cellValue = cellValue.replaceAll("\\s+", "");
        for (int r = firstRowIndex; r <= lastRowIndex; r++) {
            for (int c = firstColumnIndex; c <= lastColumnIndex; c++) {
                rawTable.get(r).put(c, cellValue);
            }
        }
    }

}
