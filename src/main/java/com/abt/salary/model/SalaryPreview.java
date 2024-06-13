package com.abt.salary.model;


import com.abt.salary.entity.SalaryCell;
import com.abt.sys.model.entity.SystemFile;
import lombok.Data;

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
     * 表头数据
     */
    private List<SalaryCell> header;

    /**
     * 严重错误，不允许进行下一步的
     */
    private List<String> fatalError = new ArrayList<>();


    //TODO: 异常数据
    //数据还是以List<List<SalaryCell>> 形式，表头一样
    //key: 错误分类
    private Map<String, List<List<SalaryCell>>> typedErrorMap = new HashMap<>();

    public void addFatalError(String error) {
        fatalError.add(error);
    }

}
