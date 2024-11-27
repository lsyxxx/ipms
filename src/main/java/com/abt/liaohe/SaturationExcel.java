package com.abt.liaohe;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class SaturationExcel {

    private final SaturationAnalysisDataRepository saturationAnalysisDataRepository;
    private final RawDataRepository rawDataRepository;
    @Getter
    @Setter
    private File file;

    public SaturationExcel(SaturationAnalysisDataRepository saturationAnalysisDataRepository, RawDataRepository rawDataRepository) {
        this.saturationAnalysisDataRepository = saturationAnalysisDataRepository;
        this.rawDataRepository = rawDataRepository;
    }

    public void saveData() {
        String fileName = this.file.getName();
        saturationAnalysisDataRepository.deleteByReportName(fileName);
        final List<RawData> list = rawDataRepository.findByReportName(fileName);
        final List<RawData> clList = list.stream().filter(i -> i.getTestName().contains("克氏") || i.getTestName().contains("脉冲")).toList();
        if (!clList.isEmpty()) {
            System.out.printf("包含覆压报告 - %s\n", fileName);
        } else {
            return;
        }
        //生成排序的treeMap
        final Map<Integer, List<RawData>> rowMap = list.stream().filter(i -> i.getRowIdx() != 0)
                .collect(Collectors.groupingBy(RawData::getRowIdx, TreeMap::new, Collectors.toList()));
        List<SaturationAnalysisData> cached = new ArrayList<>();
        //查询井号
        String wellNo = "";
        final Optional<RawData> wellNoOp = list.stream().filter(i -> i.getRowIdx() == 0 && i.getTestName().contains("井号")).findFirst();
        if (wellNoOp.isPresent()) {
            wellNo = wellNoOp.get().getTestValue();
        }
        for(Map.Entry<Integer, List<RawData>> entry : rowMap.entrySet()) {
            List<RawData> rawDataList = entry.getValue();
            SaturationAnalysisData data = new SaturationAnalysisData(rawDataList);
            data.setReportName(this.file.getName());
            data.handleData();
            if (StringUtils.isBlank(data.getWellNo())) {
                data.setWellNo(wellNo);
            }
            if (StringUtils.isBlank(data.getTid())) {
                System.out.printf("检测编号为空！报告:%s%n", data.getReportName());
                continue;
            }
            cached.add(data);
        }
        saturationAnalysisDataRepository.saveAllAndFlush(cached);
    }


}
