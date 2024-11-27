package com.abt.liaohe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 更原始数据表，以key-value形式保存
 *
 */
@Entity
@Table(name = "tmp_raw_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String testName;

    private String testValue;

    /**
     * 报告名称
     */
    private String reportName;

    /**
     * 表中的行号
     */
    private int rowIdx;
    /**
     * 表中的列号
     */
    private int colIdx;

    /**
     * 检测日期
     */
    private String testDateStart;
    private String testDateEnd;

}
