package com.abt.salary.entity;

import com.abt.common.model.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "sl_cell")
public class SalaryCell extends AuditInfo {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 列名
     */
    @Size(max = 255)
    @Column(name = "col_name")
    private String columnName;

    @Size(max = 255)
    @Column(name = "val")
    private String value;

    /**
     * 行号：从0开始
     */
    @Column(name = "row_idx", nullable = false)
    private Integer rowIndex;

    /**
     * 列号：从0开始
     */
    @NotNull
    @Column(name = "col_idx", nullable = false)
    private Integer columnIndex;

    /**
     * 关联salary main id
     */
    @Size(max = 255)
    @Column(name = "mid", nullable = false)
    private String mid;

    /**
     * 关联用户工号
     */
    @Size(max = 255)
    @Column(name = "job_num", nullable = false)
    private String jobNumber;

    /**
     * 数据类型
     */
    @Column(name = "val_type")
    private String valueType;


    public static SalaryCell createTemp(String columnName, String value, Integer rowIndex, Integer columnIndex) {
        SalaryCell salaryCell = new SalaryCell();
        salaryCell.id = UUID.randomUUID().toString();
        salaryCell.columnName = columnName;
        salaryCell.value = value;
        salaryCell.rowIndex = rowIndex;
        salaryCell.columnIndex = columnIndex;
        return salaryCell;
    }

    public static SalaryCell createTemp(String columnName, String value, Integer rowIndex, Integer columnIndex, String mid, String jobNumber) {
        SalaryCell salaryCell = createTemp(columnName, value, rowIndex, columnIndex);
        salaryCell.setJobNumber(jobNumber);
        salaryCell.setMid(mid);
        return salaryCell;
    }

}