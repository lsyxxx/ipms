package com.abt.liaohe;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 岩石分析
 */
@Getter
@Setter
@NoArgsConstructor
public class RockAnalysisData extends RockBase{
    /**
     * 孔隙度%
     */
    private String porosity;

    /**
     * 渗透率-水平
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

    /**
     * 检测日期
     */
    private String testDate;

    /**
     * 检测单位（我方）
     */
    private String company = "西安阿伯塔资环分析测试技术有限公司";

    private String remark;


}
