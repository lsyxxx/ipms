package com.abt.salary.model;

import com.abt.salary.entity.SalarySlip;
import lombok.Data;

import java.util.List;

/**
 * 工资条
 */
@Data
public class SalarySlipBoard {

    private String mainId;

    /**
     * 工资条数据
     */
    private List<SalarySlip> slipList;

    /**
     * 异常数据
     */
    private int errorCount;

    /**
     * 未查看数量
     */
    private int unreadCount;

    /**
     * 已查看数量
     */
    private int readCount;

    /**
     * 未确认数量
     */
    private int uncheckCount;

    /**
     * 已确认数量
     */
    private int checkCount;

    /**
     * 已发送数量
     */
    private int sendCount;
}
