package com.abt.salary.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 表头
 * 将合并单元和拆分并填充后保存
 */
@Getter
@Setter
@Entity
@Table(name = "sl_header", indexes = {
        @Index(name = "idx_mid", columnList = "mid"),
        @Index(name = "idx_mid_row", columnList = "mid,row"),
})
@NoArgsConstructor
@AllArgsConstructor
public class SalaryHeader {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 关联salaryMainId
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "mid", nullable = false)
    private String mid;

    @Size(max = 255)
    @Column(name = "name_")
    private String name;
    //起始单元格
    /**
     * 行号
     */
    @Column(name="start_row")
    private int startRow;
    /**
     * 列号
     */
    @Column(name="start_col")
    private int startColumn;

    //结束单元格(如果是合并的）
    @Column(name = "end_row")
    private int endRow;
    @Column(name="end_col")
    private int endColumn;



}