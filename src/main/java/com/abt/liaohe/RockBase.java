package com.abt.liaohe;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 岩石分析基础
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class RockBase {

    @Transient
    @ExcelProperty("检测编号（置空）")
    private String emptyId;
    /**
     * 检测编号
     * test id
     */
    private String tid;

    /**
     * 样品编号
     * sample id
     */
    @ExcelProperty("原样号")
    private String sid;

    /**
     * 井号
     */
    @ExcelProperty("井号")
    private String wellNo;

    @ExcelIgnore
    private String depth;

    /**
     * 顶界深度
     */
    @ExcelProperty("顶界深度")
    private String mdTop;

    /**
     * 底界深度
     */
    @ExcelProperty("底界深度")
    private String mdBase;

    /**
     * 层位
     */
    @ExcelProperty("层位")
    private String layer;


    /**
     * 岩性
     */
    @ExcelProperty("岩性")
    private String rockName;



    /**
     * 检测日期
     */
    @ExcelProperty("检测开始日期")
    private String testDateStart;
    @ExcelProperty("检测结束日期")
    private String testDateEnd;


    /**
     * 检测单位（我方）
     */
    @ExcelProperty("检测单位")
    private String company = "西安阿伯塔资环分析测试技术有限公司";

    @ExcelProperty("备注")
    private String remark;

    /**
     * 报告名称
     */
    @ExcelProperty("报告名称")
    private String reportName;

    /**
     * 样品批次
     */
    @ExcelProperty("样品批号")
    private String sampleBatch;

    @Transient
    @ExcelProperty("样品编号（置空）")
    private String sampleNo;

    public RockBase(List<RawData> row) {
        row.stream().filter(i -> i.getTestName().contains("检测编号")).findAny().ifPresent(i -> {
            this.tid = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("井号")).findAny().ifPresent(i -> {
            this.wellNo = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("样品编号")).findAny().ifPresent(i -> {
            this.sid = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("深度")).findAny().ifPresent(i -> {
            this.depth = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("井深")).findAny().ifPresent(i -> {
            this.depth = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("层位")).findAny().ifPresent(i -> {
            this.layer = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("岩性")).findAny().ifPresent(i -> {
            this.rockName = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("备注")).findAny().ifPresent(i -> {
            this.remark = i.getTestValue();
        });

        //处理日期
        final String startDate = row.get(0).getTestDateStart();
        final String endDate = row.get(0).getTestDateEnd();
        if (StringUtils.isNotBlank(startDate)) {
            LocalDate date1 = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            this.testDateStart = date1.format(DateTimeFormatter.ofPattern("yyyy/M/d"));
        }
        if (StringUtils.isNotBlank(endDate)) {
            LocalDate date2 = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            this.testDateEnd = date2.format(DateTimeFormatter.ofPattern("yyyy/M/d"));
        }

    }

    public static void main(String[] args) {
        String depth = "1695.71";
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
            System.out.println("顶界深度: " +  split[0]);
            System.out.println("底界深度: " +  split[1]);
        } else {
            System.out.println("顶界深度: " +  depth);
        }
    }

    public void handleData() {
        //处理深度
        if (StringUtils.isNotBlank(this.depth)) {
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
                this.mdTop = split[0];
                this.mdBase = split[1];
            } else {
                this.mdTop = this.depth;
            }
        }

        //处理井号
        if (StringUtils.isNotBlank(this.wellNo)) {
            this.wellNo = this.wellNo.replace("井", "");
        }


        //批号
        if (StringUtils.isNotBlank(this.testDateStart)) {
            LocalDate startDate = LocalDate.parse(this.testDateStart, DateTimeFormatter.ofPattern("yyyy/M/d"));
            String startStr = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            if (StringUtils.isBlank(this.testDateEnd)) {
                this.sampleBatch = startStr + "-" + startStr;
            } else {
                LocalDate endDate = LocalDate.parse(this.testDateEnd, DateTimeFormatter.ofPattern("yyyy/M/d"));
                String endStr = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                this.sampleBatch = startStr + "-" + endStr;
            }
        }
    }


}
