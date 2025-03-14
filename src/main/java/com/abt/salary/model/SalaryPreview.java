package com.abt.salary.model;


import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryHeader;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.sys.model.entity.SystemFile;
import com.abt.wf.entity.UserSignature;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.*;

/**
  *  预览工资数据及错误信息
  *  表头统一用SalaryHeader
  */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryPreview {

    private String group;
    private String yearMonth;

    private SalaryMain salaryMain;

    /**
     * 上传的文件
     */
    private SystemFile uploadFile;

    private String filePath;

    /**
     * 工资表原始数据
     */
    private List<List<SalaryCell>> rawTable = new ArrayList<>();

    private Map<Integer, Map<Integer, String>> rawHeader = new HashMap<>();

    /**
     * 处理后的仅无问题，可以发放工资条的数据
     */
    private List<List<SalaryCell>> slipTable = new ArrayList<>();

    /**
     * 表头数据
     */
    private List<SalaryHeader> header = new ArrayList<>();

    /**
     * 多行表头合并成一行的
     */
    private Map<Integer, String> mergedHeader = new HashMap<>();

    /**
     * 严重错误，不允许进行下一步的
     */
    private List<String> fatalError = new ArrayList<>();

    private Map<String, List<List<SalaryCell>>> typedErrorMap = new HashMap<>();

    private List<SalarySlip> slips = new ArrayList<>();

    /**
     * 审核人签名
     */
    private UserSignature leaderSig;

    private int jobNumberColumnIndex;
    private int nameColumnIndex;
    private int netPaidColumnIndex;

    public boolean hasFatalError() {
        return fatalError.isEmpty();
    }

    public boolean hasTypedError() {
       for (Map.Entry<String, List<List<SalaryCell>>> entry : typedErrorMap.entrySet()) {
           if (!entry.getValue().isEmpty()) {
               return true;
           }
       }
       return false;
    }


    public void addFatalError(String error) {
        fatalError.add(error);
    }

    public void addTypedErrorRow(String typeName, List<SalaryCell> row) {
        List<List<SalaryCell>> errList = this.typedErrorMap.computeIfAbsent(typeName, k -> new ArrayList<>());
        errList.add(row);
        this.typedErrorMap.put(typeName, errList);
    }

    public void addTypedErrorAll(String typeName, List<List<SalaryCell>> rows) {
        List<List<SalaryCell>> errList = this.typedErrorMap.computeIfAbsent(typeName, k -> new ArrayList<>());
        errList.addAll(rows);
        this.typedErrorMap.put(typeName, errList);
    }

//    public void buildHeader() {
//        this.mergedHeader.forEach((k, v) -> {
//            SalaryCell cell = SalaryCell.createTemp(v, v, 0, k);
//            this.header.add(cell);
//        });
//    }

    //生成sl_header
    public void buildHeader(Map<Integer, Map<Integer, String>> rawHeader, String mid) {
        this.header = new ArrayList<>();
        rawHeader.forEach((r, row) -> {
            if (r != 0) {
                row.forEach((c, cell) -> {
                    SalaryHeader header = new SalaryHeader();
                    header.setMid(mid);
                    header.setName(cell);
                    header.setStartRow(r);
                    header.setStartColumn(c);
                    this.header.add(header);
                });
            }
        });

    }

}
