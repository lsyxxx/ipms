package com.abt.wf.listener;

import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.merge.AbstractMergeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.List;

/**
 * 支付excel合并name单元格
 */
@Slf4j
public class PurchaseNameMergeHandler extends AbstractMergeStrategy {
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        //相对于起始行的行号，0等于起始行
        if (relativeRowIndex == null || relativeRowIndex == 0) {
            return;
        }
        //根据上一行的单元格是否合并
        //从rowIndex=3开始读取。sheet.getRow(2)结果是null;
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();
        sheet = cell.getSheet();
        Row preRow = sheet.getRow(rowIndex - 1);
        Cell preCell = preRow.getCell(colIndex);
        List<CellRangeAddress> list = sheet.getMergedRegions();
        for (CellRangeAddress cra : list) {
            //包含上一行的单元格
            if (cra.containsRow(preCell.getRowIndex()) && cra.containsColumn(preCell.getColumnIndex())) {
                //新的合并单元格
                CellRangeAddress newCra = new CellRangeAddress(rowIndex, rowIndex, cra.getFirstColumn(), cra.getLastColumn());
                sheet.addMergedRegion(newCra);
                //复制样式
                final CellStyle cellStyle = preCell.getCellStyle();
                CellStyle newCs = sheet.getWorkbook().createCellStyle();
                newCs.cloneStyleFrom(cellStyle);
            }
        }
    }
}
