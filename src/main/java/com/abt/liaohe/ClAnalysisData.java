package com.abt.liaohe;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 氯盐
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tmp_rock_cl")
public class ClAnalysisData extends RockBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 氯盐含量
     */
    @Column(name="cl_content")
    @ExcelProperty("氯盐含量")
    private String clContent;

    public ClAnalysisData(List<RawData> row) {
        super(row);
        row.stream().filter(i -> i.getTestName().contains("碳酸盐")).findAny().ifPresent(i -> {
            this.clContent = i.getTestValue();
        });
    }

    public void handleNumber() {
        if (StringUtils.isNotBlank(clContent)) {
            BigDecimal bg = new BigDecimal(this.clContent).setScale(4, RoundingMode.HALF_UP);
            this.clContent = bg.toString();
        }
    }





}
