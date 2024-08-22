package com.abt.oa.service.impl;

import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 处理野外考勤表头样式
 */
@Slf4j
@AllArgsConstructor
public class FieldWorkExcelHeaderStyleHandler implements CellWriteHandler {

    public List<FieldWorkAttendanceSetting> settings;

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                  List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();
        log.info("Cell addr: {}, cellValue: {}", cell.getAddress().toString(), cell.getStringCellValue());
        String cellValue = cell.getStringCellValue();
        settings.stream().filter(i -> i.getName().equals(cellValue))
                .findAny()
                .ifPresent(i -> {
                    String backgroundColor = i.getBackgroundColor();
                    log.info("backgroundColor: {}", backgroundColor);
                });
    }
}
