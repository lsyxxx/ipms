package com.abt.salary.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import static com.abt.salary.Constants.NETPAID_COLNAME;

/**
 * 工资表主体，及配置
 */
@Getter
@Setter
@Entity
@Table(name = "sl_main", indexes = {
        @Index(name = "idx_year_month", columnList = "year_mon"),
        @Index(name = "idx_group", columnList = "group_"),
        @Index(name = "idx_group_year_month", columnList = "group_, year_mon"),
})
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryMain extends AuditInfo {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    /**
     * 标题: 默认yyyy年mm月工资条
     */
    @NotNull(message = "标题不能为空")
    @Column(name="title_", columnDefinition="VARCHAR(128)")
    private String title;

    /**
     * 工资年月: yyyy-MM
     */
    @NotNull(message = "工资发放年月不能为空")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "选择工资发放年月必须是yyyy-MM格式")
    @Column(name="year_mon", columnDefinition = "VARCHAR(32)")
    private String yearMonth;

    /**
     * 是否显示空（或数值为0）的薪资项，默认不显示
     */
    @Column(name="show_empty", columnDefinition="BIT")
    private boolean showEmptyColumn = false;

    /**
     * 保存的上传文件地址
     * 多次上传覆盖原有的
     */
    @Column(name="file_path", columnDefinition="VARCHAR(255)")
    private String filePath;

    /**
     * 上传excel的实际文件名
     */
    @Column(name="file_name")
    private String fileName;
    /**
     * 选择的sheet页名称
     */
    @Column(name="sheet_name", columnDefinition="VARCHAR(128)")
    private String sheetName;

    /**
     * 导入的sheet 序号 0-based
     */
    @Column(name="sheet_no", columnDefinition="TINYINT")
    private int sheetNo;

    /**
     * 工资条显示的字段,多个字段用逗号分隔
     */
    @Column(name="show_columns", columnDefinition="VARCHAR(1000)")
    private String showColumns;

    /**
     * 实发工资在excel中的列名
     */
    @Column(name="paid_col_name")
    private String netPaidColumnName = NETPAID_COLNAME;

    /**
     * 实发工资列号 0-based
     */
    @NotNull
    @Column(name="col_idx_netpaid", columnDefinition="TINYINT", nullable = false)
    private Integer netPaidColumnIndex;

    /**
     * 工号列号 0-based
     */
    @NotNull
    @Column(name="col_idx_jobnum", columnDefinition="TINYINT", nullable = false)
    private Integer jobNumberColumnIndex;

    /**
     * 姓名列号 0-based
     */
    @NotNull
    @Column(name="col_idx_name", length = 32, nullable = false)
    private Integer name;

    /**
     * 是否显示温馨提示
     */
    @Column(name="show_tip", columnDefinition="BIT")
    private boolean showTip;

    /**
     * 温馨提示文本
     */
    @Column(name="tip_", columnDefinition="VARCHAR(1000)")
    private String tip;

    /**
     * 工资组,目前ABT/GRD/DC
     */
    @NotNull(message = "必须选择一个工资组")
    @Column(name = "group_", length = 32, nullable = false)
    private String group;

    /**
     * 状态
     */
    @Column(name="state_", columnDefinition="TINYINT")
    private int state = STATE_TEMP;

    /**
     * 数据存在异常
     */
    public static final int STATE_ERROR = 1;


    /**
     * 草稿
     */
    public static final int STATE_TEMP = 0;

    /**
     * 已保存数据库
     */
    public static final int STATE_PERSIST = 2;

    @Transient
    private Integer sendCount;

    @Transient
    private Integer readCount;

    @Transient
    private Integer checkCount;

    @Transient
    private Integer feedBackCount;

    /**
     * 导入数据存在异常
     */
    public void salaryImportError() {
        this.setState(STATE_ERROR);
    }

    public SalaryMain() {
        this.id = TimeUtil.idGenerator();
    }

}
