package com.abt.material.service.impl;

import com.abt.material.entity.Inventory;
import com.abt.material.entity.Stock;
import com.abt.material.model.MaterialDetailDTO;
import com.abt.sys.exception.BusinessException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 库存导出
 */
@Slf4j
@AllArgsConstructor
public class InventoryExcel {

    /**
     * 原始出入库数据
     */
    private final List<Stock> stockList;
    /**
     * 当前库存数据
     */
    private final List<Inventory> inventoryList;

    /**
     * 生成导出excel
     */
    public void createInventoryExportExcel() {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOutputStream = new FileOutputStream("inventory_export.xlsx")) {

            //创建原始数据sheet
            createRawDataSheet(workbook);
            
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 生成原始数据sheet
     * 方便用户自己整理数据，非必须
     */
    public void createRawDataSheet(Workbook workbook) {
        //生成sheet页
        Sheet sheet = workbook.createSheet("原始数据");
        
        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "单据日期", "仓库名称", "仓库地址", "出入库类型", "业务类型", 
            "分类名称", "物品名称", "规格型号", "数量", "单位", 
            "单价", "总价", "经办人", "部门", "用途", "备注"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 写入数据行
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int rowIndex = 1;
        for (Stock stock : stockList) {
            Row dataRow = sheet.createRow(rowIndex++);
            
            // 单据日期
            dataRow.createCell(0).setCellValue(
                stock.getOrderDate() != null ? stock.getOrderDate().format(dateFormatter) : ""
            );
            // 仓库名称
            dataRow.createCell(1).setCellValue(stock.getWarehouseName() != null ? stock.getWarehouseName() : "");
            // 仓库地址
            dataRow.createCell(2).setCellValue(stock.getWarehouseAddress() != null ? stock.getWarehouseAddress() : "");
            // 出入库类型
            dataRow.createCell(3).setCellValue(getStockTypeText(stock.getStockType()));
            // 业务类型
            dataRow.createCell(4).setCellValue(stock.getBizType() != null ? stock.getBizType() : "");
            // 分类名称
            dataRow.createCell(5).setCellValue(stock.getMaterialTypeName() != null ? stock.getMaterialTypeName() : "");
            // 物品名称
            dataRow.createCell(6).setCellValue(stock.getMaterialName());
            // 规格型号
            dataRow.createCell(7).setCellValue(stock.getSpecification() != null ? stock.getSpecification() : "");
            // 数量
            dataRow.createCell(8).setCellValue(stock.getNum() != null ? stock.getNum() : 0);
            // 单位
            dataRow.createCell(9).setCellValue(stock.getUnit() != null ? stock.getUnit() : "");
            // 单价
            dataRow.createCell(10).setCellValue(
                stock.getPrice() != null ? stock.getPrice().doubleValue() : 0
            );
            // 总价
            dataRow.createCell(11).setCellValue(
                stock.getTotalPrice() != null ? stock.getTotalPrice().doubleValue() : 0
            );
            // 经办人
            dataRow.createCell(12).setCellValue(stock.getUsername() != null ? stock.getUsername() : "");
            // 部门
            dataRow.createCell(13).setCellValue(stock.getDeptName() != null ? stock.getDeptName() : "");
            // 用途
            dataRow.createCell(14).setCellValue(stock.getUsage() != null ? stock.getUsage() : "");
            // 备注
            dataRow.createCell(15).setCellValue(stock.getRemark() != null ? stock.getRemark() : "");
        }
        
        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * 获取出入库类型文本
     */
    private String getStockTypeText(int stockType) {
        switch (stockType) {
            case 1: return "入库";
            case 2: return "出库";
            default: return "未知";
        }
    }

    /**
     * 生成对比sheet页
     * //TODO：数据
     */
    public void createCompareSheet(Workbook workbook) {

    }

}
