package com.abt.liaohe;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

/**
 * 岩石常规物性读取
 */
public class RockExcelReadListener extends AnalysisEventListener<Map<Integer, String>> {

    private String fileName;

    public RockExcelReadListener(String fileName) {
        this.fileName = fileName;
    }


    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        System.out.printf("解析一条数据: %s%n", data.toString());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.printf("%s解析完成", fileName);
    }
}
