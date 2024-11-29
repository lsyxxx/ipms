package com.abt.liaohe;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                return cell.getNumericCellValue() + "";
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

    /**
     * 验证list是否全部为空元素
     * @param header 要校验的list
     * @return true: 全空, false: 不全空
     */
    public static boolean validateEmpty(List<String> header) {
        if (header == null || header.isEmpty()) {
            return true;
        }
        return header.stream().allMatch(StringUtils::isBlank);
    }

    public static boolean validateEmptyRow(Row row) {
        for (Cell cell : row) {
            final String cellValue = getCellValueAsString(cell);
            if (StringUtils.isNotBlank(cellValue)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsIgnoreCase(String s1, String s2) {
        return s1.toUpperCase().contains(s2.toUpperCase());
    }

    /**
     * 从rawData中获取检测编号
     */
    public static String getTestNo(List<RawData> row) {
        final Optional<RawData> data = row.stream().filter(i -> i.getTestName().equals("检测编号")).findAny();
        if (data.isPresent()) {
            return data.get().getTestValue();
        } else {
            System.out.println("未找到《检测编号》列!");
            return "NOT_FOUND";
        }
    }

    /**
     * 从rawData中获取井号
     */
    public static String getWellNo(List<RawData> row) {
        final Optional<RawData> data = row.stream().filter(i -> i.getTestName().equals("井号")).findAny();
        if (data.isPresent()) {
            final String wellNo = data.get().getTestValue();
            //处理井号
            if (StringUtils.isNotBlank(wellNo)) {
                return wellNo.replace("井", "");
            }
        } else {
            System.out.println("未找到《井号》列!");
            return "NOT_FOUND";
        }
        return null;
    }

    /**
     * 从rawData中获取井深
     */
    public static String getDepth(List<RawData> row) {
        final Optional<RawData> data = row.stream().filter(i -> i.getTestName().equals("井深（m）")).findAny();
        if (data.isPresent()) {
            return data.get().getTestValue();
        } else {
            System.out.println("未找到《井深（m）》列!");
            return "NOT_FOUND";
        }
    }

    /**
     * 从rawData中获取井深
     */
    public static String getSampleNo(List<RawData> row) {
        final Optional<RawData> data = row.stream().filter(i -> i.getTestName().equals("样品编号")).findAny();
        if (data.isPresent()) {
            return data.get().getTestValue();
        } else {
            System.out.println("未找到《样品编号》列!");
            return "NOT_FOUND";
        }
    }

    /**
     * 从rawData中获取层位
     */
    public static String getLayer(List<RawData> row) {
        final Optional<RawData> data = row.stream().filter(i -> i.getTestName().equals("层位")).findAny();
        if (data.isPresent()) {
            return data.get().getTestValue();
        } else {
            System.out.println("未找到《层位》列!");
            return "NOT_FOUND";
        }
    }

    /**
     * 从rawData中获取岩性
     */
    public static String getRockName(List<RawData> row) {
        final Optional<RawData> data = row.stream().filter(i -> i.getTestName().equals("岩性")).findAny();
        if (data.isPresent()) {
            return data.get().getTestValue();
        } else {
            System.out.println("未找到《岩性》列!");
            return "NOT_FOUND";
        }
    }
    /**
     * 从rawData中获取备注
     */
    public static String getRemark(List<RawData> row) {
        final Optional<RawData> data = row.stream().filter(i -> i.getTestName().equals("备注")).findAny();
        if (data.isPresent()) {
            return data.get().getTestValue();
        } else {
            System.out.println("未找到《备注》列!");
            return "NOT_FOUND";
        }
    }

    public String[] handleDepth(String depth) {
        if (StringUtils.isNotBlank(depth)) {
            Pattern pattern = Pattern.compile("[^0-9.]");
            Matcher matcher = pattern.matcher(depth);
            // 提取并连接非数字和小数点的字符
            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                result.append(matcher.group());
            }
            if (result.length() == 1) {
                //正常的
                final String[] split = depth.split(result.toString());
                return new String[]{split[0], split[1]};
            } else {
                return new String[]{depth, ""};
            }
        }
        return null;
    }

    public static String handleTestDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy/M/dd"));
    }

    public static String handleSampleNo(LocalDate dateStart, LocalDate dateEnd) {
        if (dateStart == null) {
            return "";
        }
        String d1 = dateStart.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String d2 = "";
        if (dateEnd == null) {
            return d1 + "-" + d1;
        }
        d2 = dateEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return d1 + "-" + d2;
    }



}
