package com.abt.material.service.impl;

import com.abt.material.entity.Inventory;
import com.abt.material.model.MonthlyStockStatsDTO;
import com.abt.sys.exception.BusinessException;
import com.aspose.cells.*;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.model.Styles;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.*;
import java.util.Comparator;

/**
 * 库存导出
 */
@Slf4j
@RequiredArgsConstructor
public class InventoryExcel {
    /**
     * 库存数据
     */
    private final List<Inventory> inventoryList;

    /**
     * 礼品使用-月统计数据1-对应year1
     */
    private final List<MonthlyStockStatsDTO> monthlyStat1;

    /**
     * 礼品使用-月统计数据2-对应year2
     * 以此数据进行排序
     */
    private final List<MonthlyStockStatsDTO> monthlyStat2;

    /**
     * 对比年份1，作为对比
     */
    private final int year1;
    /**
     * 对比年份2，作为主体
     */
    private final int year2;

    /**
     * 比较的月份
     */
    private final List<Integer> monthList;

    /**
     * 根据物品id去重后的数据，用于遍历
     */
    private List<MonthlyStockStatsDTO> distinctList;


    /**
     * 校验数据是否合法
     */
    public void validate() {
        //1. monthlyStat要一致
        if (monthlyStat1 == null) {
            throw new BusinessException("月统计数据1不能为null");
        }
        if (monthlyStat2 == null) {
            throw new BusinessException("月统计数据2不能为null");
        }
        if (inventoryList == null) {
            throw new BusinessException("当前库存列表不能为null");
        }
    }


    /**
     * 生成Excel到输出流（不生成服务器文件）
     *
     * @param outputStream 输出流
     * @throws Exception 异常
     */
    public void createInventoryExportExcel(OutputStream outputStream) throws Exception {
        validate();
        Workbook workbook = new Workbook();
        
        // 设置整个工作簿的默认字体为宋体
        Style defaultStyle = workbook.getDefaultStyle();
        defaultStyle.getFont().setName("SimSun");
        defaultStyle.getFont().setSize(11);
        defaultStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        defaultStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        workbook.setDefaultStyle(defaultStyle);

        // 创建对比数据sheet
        createCompareSheet(workbook);
        createStockValueSheet(workbook);

        // 直接保存到输出流
         workbook.save(outputStream, SaveFormat.XLSX);
    }

    /**
     * 处理对比数据
     */
    public void compareDataProcess() {
        //1. 合并stat1, stat2
        List<MonthlyStockStatsDTO> all = new ArrayList<>();
        all.addAll(monthlyStat1);
        all.addAll(monthlyStat2);
        //2. 获取物品顺序
        // 根据 materialId 去重并保持原始顺序
        distinctList = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        for (MonthlyStockStatsDTO dto : all) {
            if (seenIds.add(dto.getMaterialId())) {
                distinctList.add(dto);
            }
        }

    }

    public String genkey(String key1, Integer key2) {
        return key1 + "|" + key2;
    }

    private Style createHeaderStyle(Workbook workbook) {
        Style headerStyle = workbook.createStyle();
        Font headerFont = headerStyle.getFont();
        headerFont.setBold(true);
        headerFont.setColor(Color.getBlack());
        headerFont.setName("SimSun");
        headerFont.setSize(11);
        headerStyle.setPattern(BackgroundType.SOLID);
        headerStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        headerStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        headerStyle.setBackgroundColor(Color.getGray());
        headerStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        headerStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        headerStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        headerStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
        headerStyle.setBackgroundColor(Color.getLightGray());
        return headerStyle;
    }

    /**
     * 生成对比sheet页
     */
    public void createCompareSheet(Workbook workbook) {
        // 添加新的worksheet
        String sheetName = year1 + "和" + year2 + "礼品用量对比";
        workbook.getWorksheets().get(0).setName(sheetName);
        Worksheet sheet = workbook.getWorksheets().get(sheetName);
        // 创建表头样式
        final Style headerStyle = createHeaderStyle(sheet.getWorkbook());

        //处理数据
        compareDataProcess();

        // 表头
        String[] headers = {
                "品类", "品名", "使用月份", year1 + "使用量", year2 + "使用量", "对比"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = sheet.getCells().get(0, i);
            cell.setValue(headers[i]);
            cell.setStyle(headerStyle);
        }
        sheet.getCells().getRows().get(0).setHeight(25);
        //2. 写入对比数据
        Style cellStyle = createCellStyle(sheet.getWorkbook());
        Style redCell = createCellStyle(sheet.getWorkbook());
        redCell.getFont().setColor(Color.getRed());
        Style greenCell = createCellStyle(sheet.getWorkbook());
        greenCell.getFont().setColor(Color.getSeaGreen());
        Style boldCell =  createCellStyle(sheet.getWorkbook());
        boldCell.getFont().setBold(true);
        Style nameCell = createCellStyle(sheet.getWorkbook());
        nameCell.getFont().setBold(true);
        nameCell.getFont().setSize(12);
        Style typeCell = createCellStyle(sheet.getWorkbook());
        typeCell.getFont().setBold(true);
        typeCell.getFont().setSize(16);
        //ASPOSE无法竖排文字


        Map<String, MonthlyStockStatsDTO> map1 = monthlyStat1.stream()
                .collect(Collectors.toMap(i -> genkey(i.getMaterialId(), i.getMonth()), i -> i));
        Map<String, MonthlyStockStatsDTO> map2 = monthlyStat2.stream()
                .collect(Collectors.toMap(i -> genkey(i.getMaterialId(), i.getMonth()), i -> i));

        int rowIndex = 1;
        for (MonthlyStockStatsDTO dto : distinctList) {
            //单个物品所有月份合计使用量
            BigDecimal totalNum1 = BigDecimal.ZERO;
            BigDecimal totalNum2 = BigDecimal.ZERO;

            int midStart = rowIndex; //同一物品起始行

            sheet.getCells().insertRow(rowIndex);
            
            
            for (Integer month : monthList) {

                //col0: 类别
                sheet.getCells().get(rowIndex, 0).putValue(dto.getMaterialTypeName());
                sheet.getCells().get(rowIndex, 0).setStyle(typeCell);
                //col1:组合name
                String name = dto.getMaterialName();
                if (StringUtils.isNotBlank(dto.getUnit())) {
                    name = name + "-" + dto.getUnit();
                }
                if (dto.getPrice() != null) {
                    name = name + "\n" + dto.getPrice().setScale(2, RoundingMode.HALF_UP) + "元";
                }
                sheet.getCells().get(rowIndex, 1).putValue(name);
                sheet.getCells().get(rowIndex, 1).setStyle(nameCell);

                String key = genkey(dto.getMaterialId(), month);
                MonthlyStockStatsDTO dto1 = map1.getOrDefault(key, new MonthlyStockStatsDTO());
                MonthlyStockStatsDTO dto2 = map2.getOrDefault(key, new MonthlyStockStatsDTO());

                sheet.getCells().get(rowIndex, 2).putValue(month + "月");
                sheet.getCells().get(rowIndex, 2).setStyle(cellStyle);
                sheet.getCells().get(rowIndex, 3).putValue(dto1.getTotalQuantity());
                sheet.getCells().get(rowIndex, 3).setStyle(cellStyle);
                sheet.getCells().get(rowIndex, 4).putValue(dto2.getTotalQuantity());
                sheet.getCells().get(rowIndex, 4).setStyle(cellStyle);
                //对比
                double diff = dto2.getTotalQuantity() - dto1.getTotalQuantity();
                if (diff > 0) {
                    sheet.getCells().get(rowIndex, 5).putValue("↑" + diff);
                    sheet.getCells().get(rowIndex, 5).setStyle(redCell);
                } else if (diff < 0) {
                    sheet.getCells().get(rowIndex, 5).putValue("↓" + diff);
                    sheet.getCells().get(rowIndex, 5).setStyle(greenCell);
                } else {
                    sheet.getCells().get(rowIndex, 5).putValue("0");
                    sheet.getCells().get(rowIndex, 5).setStyle(cellStyle);
                }

                totalNum1 = totalNum1.add(BigDecimal.valueOf(dto1.getTotalQuantity()));
                totalNum2 = totalNum2.add(BigDecimal.valueOf(dto2.getTotalQuantity()));
                sheet.getCells().setRowHeight(rowIndex, 20);
                rowIndex++;
            }
            //col0: 类别
            sheet.getCells().get(rowIndex, 0).putValue(dto.getMaterialTypeName());
            sheet.getCells().get(rowIndex, 0).setStyle(typeCell);

            //col1:组合name
            String name = dto.getMaterialName();
            if (StringUtils.isNotBlank(dto.getUnit())) {
                name = name + "-" + dto.getUnit();
            }
            if (dto.getPrice() != null) {
                name = name + "\n" + dto.getPrice().setScale(2, RoundingMode.HALF_UP);
            }

            sheet.getCells().get(rowIndex, 1).putValue(name);
            sheet.getCells().get(rowIndex, 1).setStyle(nameCell);

            //合计行
            sheet.getCells().get(rowIndex, 2).putValue("合计数量：");
            sheet.getCells().get(rowIndex, 2).setStyle(boldCell);
            sheet.getCells().get(rowIndex, 3).putValue(totalNum1.setScale(2, RoundingMode.HALF_UP));
            sheet.getCells().get(rowIndex, 3).setStyle(boldCell);
            sheet.getCells().get(rowIndex, 4).putValue("合计数量：");
            sheet.getCells().get(rowIndex, 4).setStyle(boldCell);
            sheet.getCells().get(rowIndex, 5).putValue(totalNum2.setScale(2, RoundingMode.HALF_UP));
            sheet.getCells().get(rowIndex, 5).setStyle(boldCell);
            sheet.getCells().setRowHeight(rowIndex, 20);
            rowIndex++;
            //合并相同物品名称
            sheet.getCells().merge(midStart, 1, rowIndex - midStart, 1);
        }
        //合并类别
        int typeStart = 1;
        final Map<String, Long> typeCount = distinctList.stream()
                .collect(Collectors.groupingBy(
                        MonthlyStockStatsDTO::getMaterialTypeId,
                        LinkedHashMap::new,  // 使用 LinkedHashMap 保持插入顺序
                        Collectors.counting()));
        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            int totalRows = (int) (entry.getValue() * (monthList.size() + 1));
            sheet.getCells().merge(typeStart, 0, totalRows, 1);
            typeStart = typeStart + totalRows;
        }

        //设置列宽
        sheet.getCells().setColumnWidth(0, 15);
        sheet.getCells().setColumnWidth(1, 25);
        sheet.getCells().setColumnWidth(2, 15);
        sheet.getCells().setColumnWidth(3, 15);
        sheet.getCells().setColumnWidth(4, 15);
        sheet.getCells().setColumnWidth(5, 15);
    }

    private Style createCellStyle(Workbook workbook) {
        Style style = workbook.createStyle();
        style.getFont().setColor(Color.getBlack());
        style.getFont().setSize(11);
        style.getFont().setName("SimSun");
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setTextWrapped(true);
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
        return style;
    }

    /**
     * 合计当前库存总价值
     */
    private BigDecimal calculateTotalValue() {
        return inventoryList.stream()
                .filter(inventory -> inventory.getUnitPrice() != null && inventory.getQuantity() != null)
                .map(inventory -> inventory.getUnitPrice().multiply(BigDecimal.valueOf(inventory.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 对inventoryList排序，优先类别（礼品类-烟、礼品类-酒、礼品类-茶、其他类别），其次是单价,同一物品根据仓库优先级排序
     */
    private List<Inventory> sortInventoryList() {
        List<Inventory> sortedList = new ArrayList<>(inventoryList);
        sortedList.sort(
            // 1. 按品类优先级排序
            Comparator.comparing((Inventory inv) -> getTypePriority(inv.getMaterialType()), Comparator.reverseOrder())
            // 2. 其他类别按类别名称排序
            .thenComparing(inv -> inv.getMaterialType() == null ? "" : inv.getMaterialType())
            // 3. 按单价倒序排序（高价在前，null值在后）
            .thenComparing(inv -> inv.getUnitPrice() == null ? BigDecimal.valueOf(-1) : inv.getUnitPrice(), 
                          Comparator.reverseOrder())
            // 4. 按仓库优先级排序
            .thenComparing(inv -> getWarehousePriority(inv.getWarehouseName()))
        );
        return sortedList;
    }

    /**
     * 库存及价值sheet页
     * @param workbook workbook
     */
    public void createStockValueSheet(Workbook workbook) {
        workbook.getWorksheets().add("礼品库存及价值");
        Worksheet sheet = workbook.getWorksheets().get("礼品库存及价值");
        Cells cells = sheet.getCells();
        //第一行合计：
        cells.insertRow(0);
        BigDecimal totalPrice = calculateTotalValue();
        //当前库存合计总价
        cells.get(0, 0).putValue(String.format("礼品类-烟、酒、茶库存剩余价值合计%s元，明细如下：", totalPrice.setScale(2, RoundingMode.HALF_UP)));
        Style totalPriceStyle = workbook.createStyle();
        totalPriceStyle.getFont().setSize(14);
        totalPriceStyle.getFont().setBold(true);
        totalPriceStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
        cells.get(0, 0).setStyle(totalPriceStyle);
        //标题行
        Style headerStyle = createHeaderStyle(workbook);
        String[] header = new String[]{"品类", "品名", "单位", "单价/元", "现存仓库", "库存数量", "库存价值/元"};
        for (int i = 0; i < header.length; i++) {
            Cell cell = cells.get(1, i);
            cell.putValue(header[i]);
            cell.setStyle(headerStyle);
        }
        cells.setRowHeight(1, 30);
        //数据行
        List<Inventory> sortedList = sortInventoryList();
        //对inventoryList根据品类分组
        Map<String, List<Inventory>> groupedInventory = sortedList.stream()
                .collect(Collectors.groupingBy(Inventory::getMaterialType));
        int rowIndex = 2;
        Style cellStyle = createCellStyle(workbook);
        Style valueStyle = createValueStyle(workbook);
        Style typeStyle = createCellStyle(workbook);
        typeStyle.getFont().setSize(12);
        typeStyle.getFont().setBold(true);
        int typeCol = 0;
        int nameCol = 1;
        int unitCol = 2;
        int priceCol = 3;
        int warehouseCol = 4;
        int quantityCol = 5;
        //总价值
        int valueCol = 6;
        for (Map.Entry<String, List<Inventory>> entry : groupedInventory.entrySet()) {
            String materialType = entry.getKey();
            List<Inventory> inventoryList = entry.getValue();
            int typeStartRow = rowIndex; // 记录当前品类开始行
            // 按物品名称分组，用于合并同一物品的品名、单位、单价
            Map<String, List<Inventory>> groupedByName = inventoryList.stream()
                    .collect(Collectors.groupingBy(Inventory::getMaterialName, LinkedHashMap::new, Collectors.toList()));
            
            for (Map.Entry<String, List<Inventory>> nameEntry : groupedByName.entrySet()) {
                List<Inventory> sameNameInventories = nameEntry.getValue();
                
                int nameStartRow = rowIndex; // 记录当前物品名称开始行
                
                for (Inventory inventory : sameNameInventories) {
                    cells.insertRow(rowIndex);
                    cells.setRowHeight(rowIndex, 30);
                    
                    // 品类
                    cells.get(rowIndex, typeCol).putValue(materialType);
                    cells.get(rowIndex, typeCol).setStyle(cellStyle);

                    // 品名
                    cells.get(rowIndex, nameCol).putValue(inventory.getMaterialName());
                    cells.get(rowIndex, nameCol).setStyle(cellStyle);

                    // 单位
                    cells.get(rowIndex, unitCol).putValue(inventory.getMaterialUnit());
                    cells.get(rowIndex, unitCol).setStyle(cellStyle);

                    // 单价
                    cells.get(rowIndex, priceCol).putValue(inventory.getUnitPrice() == null ? "" : inventory.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
                    cells.get(rowIndex, priceCol).setStyle(valueStyle);

                    // 现存仓库
                    cells.get(rowIndex, warehouseCol).putValue(inventory.getWarehouseName());
                    cells.get(rowIndex, warehouseCol).setStyle(cellStyle);

                    // 库存数量
                    cells.get(rowIndex, quantityCol).putValue(formatQuantity(inventory.getQuantity()));
                    cells.get(rowIndex, quantityCol).setStyle(cellStyle);
                    
                    // 使用Excel公式计算库存价值 (单价 * 数量)
                    String formula = String.format("=IFERROR(D%d*F%d, 0.00)", rowIndex + 1, rowIndex + 1);
                    cells.get(rowIndex, valueCol).setFormula(formula);
                    cells.get(rowIndex, valueCol).setStyle(valueStyle);

                    rowIndex++;
                }
                
                // 合并同一物品的品名、单位、单价（如果有多行）
                if (sameNameInventories.size() > 1) {
                    // 合并品名列
                    cells.merge(nameStartRow, nameCol, sameNameInventories.size(), 1);
                    // 合并单位列
                    cells.merge(nameStartRow, unitCol, sameNameInventories.size(), 1);
//                    // 合并单价列
//                    cells.merge(nameStartRow, priceCol, sameNameInventories.size(), 1);
                }
            }
            
            // 合并同一品类的第一列
            if (inventoryList.size() > 1) {
                cells.merge(typeStartRow, typeCol, inventoryList.size(), 1);
                cells.get(typeStartRow, typeCol).setStyle(typeStyle);
            }
            
            // 类别合计行
            cells.insertRow(rowIndex);
            cells.get(rowIndex, 0).putValue("小计: " + materialType);
            // 计算当前品类的合计公式
            String sumFormula = String.format("=SUM(G%d:G%d)", typeStartRow + 1, rowIndex);
            cells.get(rowIndex, valueCol).setFormula(sumFormula);
            cells.get(rowIndex, valueCol).setStyle(valueStyle);
            
            // 合并小计行的前几列
            cells.merge(rowIndex, 0, 1, valueCol);
            cells.get(rowIndex, 0).setStyle(typeStyle);
            rowIndex++;
        }

        //设置列宽
        sheet.getCells().setColumnWidth(typeCol, 15);
        sheet.getCells().setColumnWidth(nameCol, 25);
        sheet.getCells().setColumnWidth(unitCol, 15);
        sheet.getCells().setColumnWidth(priceCol, 15);
        sheet.getCells().setColumnWidth(warehouseCol, 20);
        sheet.getCells().setColumnWidth(quantityCol, 15);
        sheet.getCells().setColumnWidth(valueCol, 20);

    }

    /**
     * 获取物品类型的优先级
     * @param materialType 物品类型
     * @return 优先级（数字越小优先级越高）
     */
    private int getTypePriority(String materialType) {
        if (materialType == null) {
            return 5; // null值优先级最低
        }
        return switch (materialType) {
            case "礼品类-烟" -> 1;
            case "礼品类-酒" -> 2;
            case "礼品类-茶" -> 3;
            default -> 4; // 其他类别
        };
    }

    private int getWarehousePriority(String warehouseName) {
        if (warehouseName == null) {
            return 5;
        }
        if (warehouseName.contains("西安")) {
            return 1;
        } else if (warehouseName.contains("延安")) {
            return 2; 
        } else if (warehouseName.contains("成都")) {
            return 3; 
        }
        return 4;
    }

    /**
     * 格式化数量显示
     * 如果是整数，显示整数；如果是小数，显示2位小数
     * @param quantity 数量
     * @return 格式化后的字符串
     */
    private String formatQuantity(Double quantity) {
        if (quantity == null) {
            return "0";
        }
        
        // 检查是否为整数
        if (quantity == Math.floor(quantity)) {
            return String.valueOf(quantity.intValue());
        } else {
            return String.format("%.2f", quantity);
        }
    }

    /**
     * 创建价值单元格样式（2位小数）
     */
    private Style createValueStyle(Workbook workbook) {
        Style style = createCellStyle(workbook);
        style.setCustom("#,##0.00"); // 自定义格式：千分位分隔符 + 2位小数
        return style;
    }

}
