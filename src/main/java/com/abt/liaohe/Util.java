package com.abt.liaohe;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 */
public class Util {


    /**
     * 保留3位有效数字
     */
    public static String formatNumber3(String number) {
        if (StringUtils.isBlank(number)) {
            return number;
        }
        if ("/".equals(number)) {
            return "";
        }
        BigDecimal bd = new BigDecimal(number);
        BigDecimal roundedNumber = bd.setScale(3, RoundingMode.HALF_UP);
        return roundedNumber.toString();
    }

    public static String formatNumber4(String number) {
        if (StringUtils.isBlank(number)) {
            return number;
        }
        if ("/".equals(number)) {
            return "";
        }
        try {
            BigDecimal bd = new BigDecimal(number);
            BigDecimal roundedNumber = bd.setScale(4, RoundingMode.HALF_UP);
            return roundedNumber.toString();
        } catch (Exception e) {
            //可能存在未处理的非数字字符，不处理，直接返回原值
            return number;
        }

    }



    // 移除所有空白字符，包括空格、回车、换行、制表符等
    public static String removeBlank(String string) {
        return string.replace("\\s+", "").replace("\n", "").replace("\r", "");
    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // 日期格式可以自行调整
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    /**
     * 寻找数据起始行，可能会存在问题，有的标题行包含项目编号AJCxxxx
     * 原理：找到包含AJC/JC检测编号的第一行就是数据的第一行
     * @return 数据起始行号(0-based)
     */
    public static int recognizeDataRow(Sheet sheet) {
        //只观察前10行
        int range = 10;
        final int lastRowNum = sheet.getLastRowNum();
        //防止超出
        if (range > lastRowNum) {
            range = lastRowNum;
        }
        for (int i = 0; i < range; i++) {
            Row row = sheet.getRow(i);
            for(Cell cell : row) {
                //
                String cellValue = getCellValueAsString(cell);
                if (cellValue.contains("AJC") || cellValue.contains("JC"))  {
                    //第一次找到数据行
                    return row.getRowNum();
                }
            }
        }
        return 0;
    }

}
