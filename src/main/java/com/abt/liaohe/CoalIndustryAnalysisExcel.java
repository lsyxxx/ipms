package com.abt.liaohe;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.abt.liaohe.Util.getCellValueAsString;
import static com.abt.liaohe.Util.removeBlank;

/**
 * 煤工业分析
 */
@Service
public class CoalIndustryAnalysisExcel {
    private final RawDataRepository rawDataRepository;
    private final CoalIndustryDataRepository coalIndustryDataRepository;

    public CoalIndustryAnalysisExcel(RawDataRepository rawDataRepository, CoalIndustryDataRepository coalIndustryDataRepository) {
        this.rawDataRepository = rawDataRepository;
        this.coalIndustryDataRepository = coalIndustryDataRepository;
    }


    /**
     * 从tmp_raw_data中获取数据
     */
    public void readRawData(String fileName) {
        coalIndustryDataRepository.deleteByReportName(fileName);
        final List<RawData> list = rawDataRepository.findByReportName(fileName);
        //生成排序的treeMap
        final Map<Integer, List<RawData>> rowMap = list.stream().collect(Collectors.groupingBy(RawData::getRowIdx, TreeMap::new, Collectors.toList()));
        List<CoalIndustryData> cached = new ArrayList<>();
        for(Map.Entry<Integer, List<RawData>> entry : rowMap.entrySet()) {
            List<RawData> rawDataList = entry.getValue();
            CoalIndustryData coalIndustryData = new CoalIndustryData(rawDataList);
            coalIndustryData.setReportName(fileName);
            coalIndustryData.handleData();
            if (StringUtils.isBlank(coalIndustryData.getTid())) {
                System.out.printf("检测编号为空！报告:%s%n", coalIndustryData.getReportName());
                continue;
            }
            cached.add(coalIndustryData);
        }
        coalIndustryDataRepository.saveAllAndFlush(cached);
    }



}
