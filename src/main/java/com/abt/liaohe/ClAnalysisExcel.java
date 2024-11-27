package com.abt.liaohe;

import com.alibaba.excel.EasyExcel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@AllArgsConstructor
public class ClAnalysisExcel {

    private ClAnalysisDataRepository clAnalysisDataRepository;
    private RawDataRepository rawDataRepository;
    @Getter
    @Setter
    private File file;

    public void recognizeHeader() {

    }

    public void saveClData() {
        String fileName = this.file.getName();
        clAnalysisDataRepository.deleteByReportName(fileName);
        final List<RawData> list = rawDataRepository.findByReportName(fileName);
        final List<RawData> clList = list.stream().filter(i -> i.getTestName().contains("碳酸")).toList();
        if (!clList.isEmpty()) {
            System.out.printf("包含碳酸盐报告 - %s\n", fileName);
        } else {
            return;
        }
        //生成排序的treeMap
        final Map<Integer, List<RawData>> rowMap = list.stream().filter(i -> i.getRowIdx() != 0)
                .collect(Collectors.groupingBy(RawData::getRowIdx, TreeMap::new, Collectors.toList()));
        List<ClAnalysisData> cached = new ArrayList<>();
        //查询井号
        String wellNo = "";
        final Optional<RawData> wellNoOp = list.stream().filter(i -> i.getRowIdx() == 0 && i.getTestName().contains("井号")).findFirst();
        if (wellNoOp.isPresent()) {
            wellNo = wellNoOp.get().getTestValue();
        }
        for(Map.Entry<Integer, List<RawData>> entry : rowMap.entrySet()) {
            List<RawData> rawDataList = entry.getValue();
            ClAnalysisData clData = new ClAnalysisData(rawDataList);
            clData.setReportName(this.file.getName());
            clData.handleData();
            if (StringUtils.isBlank(clData.getWellNo())) {
                clData.setWellNo(wellNo);
            }
            if (StringUtils.isBlank(clData.getTid())) {
                System.out.printf("检测编号为空！报告:%s%n", clData.getReportName());
                continue;
            }
            cached.add(clData);
        }
        clAnalysisDataRepository.saveAllAndFlush(cached);
    }

    public void exportExcel() {
        String template = "F:\\00平台资料汇总\\辽河数据\\外送样分析化验模板\\模板\\岩石氯盐含量测定-辽河 - 导出模板.xlsx";
        final List<ClAnalysisData> all = clAnalysisDataRepository.findAll();
        //处理数字
        all.forEach(ClAnalysisData::handleNumber);
        EasyExcel.write(template, ClAnalysisData.class)
                .sheet("数据")
                .doWrite(all);
    }

}
