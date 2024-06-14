package com.abt.salary.model;


import com.abt.salary.entity.SalaryCell;
import com.abt.sys.model.entity.SystemFile;
import lombok.Data;
import com.abt.common.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
  *  预览工资数据及错误信息
  */
@Data
public class SalaryPreview {

    /**
     * 上传的文件
     */
    private SystemFile uploadFile;

    /**
     * 工资表数据
     */
    private List<List<SalaryCell>> salaryTable;
    /**
     * 工资表数据 map版
     */
    private List<Map<Integer, String>> rowList;

    /**
     * 表头数据
     */
    private List<SalaryCell> header;

    /**
     * 严重错误，不允许进行下一步的
     */
    private List<String> fatalError = new ArrayList<>();

    private Map<String, List<List<SalaryCell>>> typedErrorMap = new HashMap<>();

    private int jobNumberColumnIndex;
    private int nameColumnIndex;
    private int netPaidColumnIndex;

    public boolean hasFatalError() {
        return !fatalError.isEmpty();
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
        this.typedErrorMap.getOrDefault(typeName, new ArrayList<>()).add(row);
    }

    public void addTypedErrorAll(String typeName, List<List<SalaryCell>> rows) {
        this.typedErrorMap.getOrDefault(typeName, new ArrayList<>()).addAll(rows);
    }
}
