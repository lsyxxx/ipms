package com.abt.salary.entity;

import com.abt.common.CommonConstants;
import com.abt.common.model.AuditInfo;
import com.abt.sys.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
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
    private String label;

    @Size(max = 255)
    @Column(name = "val")
    private String value = "";

    /**
     * 行唯一id
     */
    @Column(name = "rid", length = 128, nullable = false)
    private String rowId;

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
    private String jobNumber = "";

    @Size(max = 32)
    @Column(name = "name_", columnDefinition = "VARCHAR(32)")
    private String name = "";

    /**
     * 数据类型
     */
    @Column(name = "val_type")
    private String valueType;

    /**
     * cell类型
     * 空：数据
     * check_error: 错误信息标志cell
     */
    @Transient
    private String cellType;

    /**
     * 错误信息
     */
    @Transient
    private List<String> error;

    /**
     * 状态
     */
    @Transient
    private String rowState = CommonConstants.SUCCESS;

    @Transient
    private boolean isRowError = false;

    public static final String CELL_TYPE_ERROR = "check_error";

    public void setRowSuccess() {
        this.rowState = CommonConstants.SUCCESS;
    }

    public boolean isRowError() {
        return "error".equals(rowState);
    }


    public static SalaryCell createTemp(String columnName, String value, Integer rowIndex, Integer columnIndex) {
        SalaryCell salaryCell = new SalaryCell();
        salaryCell.id = UUID.randomUUID().toString();
        salaryCell.label = columnName;
        salaryCell.value = value;
        salaryCell.rowIndex = rowIndex;
        salaryCell.columnIndex = columnIndex;
        return salaryCell;
    }

    public static SalaryCell createTemp(String columnName, String value, Integer rowIndex, Integer columnIndex, String mid, String jobNumber, String rowId) {
        SalaryCell salaryCell = createTemp(columnName, value, rowIndex, columnIndex);
        salaryCell.setJobNumber(jobNumber);
        salaryCell.setMid(mid);
        salaryCell.setRowId(rowId);
        return salaryCell;
    }

    /**
     * 标记行存在错误
     */
    public void doError() {
        this.setRowError(true);
        this.setRowState(CommonConstants.ERROR);
    }

    public static SalaryCell createRowCell(int rowIndex, String jobNumber, String name, String mid) {
        SalaryCell cell = createEmpty(rowIndex, jobNumber, name, mid);
        cell.setRowId(UUID.randomUUID().toString());
        return cell;
    }

    public static SalaryCell createEmpty(int rowIndex, String jobNumber, String name, String mid) {
        SalaryCell cell = new SalaryCell();
        cell.setId(UUID.randomUUID().toString());
        cell.setRowIndex(rowIndex);
        cell.setColumnIndex(0);
        cell.setJobNumber(jobNumber);
        cell.setMid(mid);
        cell.setName(name);
        return cell;
    }

    @Override
    public String toString() {
        return "SalaryCell{" +
                "columnName='" + label + '\'' +
                ", value='" + value + '\'' +
                ", rowIndex=" + rowIndex +
                ", columnIndex=" + columnIndex +
                ", jobNumber='" + jobNumber + '\'' +
                ", valueType='" + valueType + '\'' +
                "} ";
    }
}