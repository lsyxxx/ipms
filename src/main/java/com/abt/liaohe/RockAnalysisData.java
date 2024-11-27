package com.abt.liaohe;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 岩石分析
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tmp_rock_analysis")
public class RockAnalysisData extends RockBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 孔隙度%
     */
    private String porosity;

    /**
     * 渗透率-水平，没有特别说明就是水平
     */
    private String horizonPermeability;


    /**
     * 渗透率-垂直
     */
    private String verticalPermeability;

    /**
     * 岩石密度
     */
    private String rockDensity;


    public static RockAnalysisData create(List<RawData> row) {
        if (row == null) {
            return null;
        }
        if (row.isEmpty()) {
            return null;
        }

        RockAnalysisData data = new RockAnalysisData();
        row.stream().filter(i -> i.getTestName().contains("检测编号")).findAny().ifPresent(i -> {
            data.setTid(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("井号")).findAny().ifPresent(i -> {
            data.setWellNo(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("样品编号")).findAny().ifPresent(i -> {
            data.setSid(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("深度")).findAny().ifPresent(i -> {
            data.setDepth(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("井深")).findAny().ifPresent(i -> {
            data.setDepth(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("层位")).findAny().ifPresent(i -> {
            data.setLayer(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("岩性")).findAny().ifPresent(i -> {
            data.setRockName(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("孔隙度")).findAny().ifPresent(i -> {
            data.setPorosity(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("渗透率") && i.getTestName().contains("水平")).findAny().ifPresent(i -> {
            data.setHorizonPermeability(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("渗透率") && i.getTestName().contains("垂直")).findAny().ifPresent(i -> {
            data.setVerticalPermeability(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("岩石密度")).findAny().ifPresent(i -> {
            data.setRockDensity(i.getTestValue());
        });
        row.stream().filter(i -> i.getTestName().contains("备注")).findAny().ifPresent(i -> {
            data.setRemark(i.getTestValue());
        });

        //处理深度
        if (StringUtils.isNotBlank(data.getDepth())) {
            String depth = data.getDepth();
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
                data.setMdTop(split[0]);
                data.setMdBase(split[1]);
            } else {
                data.setMdTop(data.getDepth());
            }
        }

        //处理井号
        if (StringUtils.isNotBlank(data.getWellNo())) {
            data.setWellNo(data.getWellNo().replace("井", ""));
        }

        //处理日期
        final String startDate = row.get(0).getTestDateStart();
        final String endDate = row.get(0).getTestDateEnd();
        if (StringUtils.isNotBlank(startDate)) {
            LocalDate date1 = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            data.setTestDateStart(date1.format(DateTimeFormatter.ofPattern("yyyy/M/d")));
        }
        if (StringUtils.isNotBlank(endDate)) {
            LocalDate date2 = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            data.setTestDateEnd(date2.format(DateTimeFormatter.ofPattern("yyyy/M/d")));
        }


        //批号
        if (StringUtils.isNotBlank(startDate)) {
            if (StringUtils.isBlank(endDate)) {
                data.setSampleBatch(startDate + "-" + startDate);
            } else {
                data.setSampleBatch(startDate + "-" + endDate);
            }
        }

        return data;
    }


    /**
     * 对数据进行格式化
     */
    public void formatNumber() {
        this.setPorosity(Util.formatNumber4(this.porosity));;
        this.setHorizonPermeability(Util.formatNumber4(this.horizonPermeability));
        this.setVerticalPermeability(Util.formatNumber4(this.verticalPermeability));
        this.setRockDensity(Util.formatNumber4(this.rockDensity));
    }





}
