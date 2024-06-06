package com.abt.oa.service;

import com.abt.oa.entity.CarApplyRecord;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CarApplyExcel {

//    @Value("${excel.model.carApply.model}")
    public static final String carApplyTemplatePath = "C:\\Users\\Administrator\\Desktop\\model_car_apply.xlsx";

//    @Value("${excel.model.carApply.copy}")
    public static final String copyFilePath = "C:\\Users\\Administrator\\Desktop\\copyCarApply.xlsx";

    /**
     * 复制模板到copyUrl
     * @param modelUrl 模板文件完成路径(包含文件名)
     * @param copyUrl 复制后路径(包含文件名)
     */
    public static void copyModel(String modelUrl, String copyUrl) throws IOException {
        FileUtils.copyFile(new File(modelUrl), new File(copyUrl));
    }

    public static void writeTemplate(String templateFullPath, String destFilePath, boolean forceNewRow,  List data, Map<String, Object> map) throws IOException {
        try (ExcelWriter excelWriter = EasyExcel.write(destFilePath).withTemplate(templateFullPath).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            // 这里注意 入参用了forceNewRow 代表在写入list的时候不管list下面有没有空行 都会创建一行，然后下面的数据往后移动。默认 是false，会直接使用下一行，如果没有则创建。
            // forceNewRow 如果设置了true,有个缺点 就是他会把所有的数据都放到内存了，所以慎用
            // 简单的说 如果你的模板有list,且list不是最后一行，下面还有数据需要填充 就必须设置 forceNewRow=true 但是这个就会把所有数据放到内存 会很耗内存
            // 如果数据量大 list不是最后一行 参照下一个
            FillConfig fillConfig = FillConfig.builder().forceNewRow(forceNewRow).build();
            excelWriter.fill(data, fillConfig, writeSheet);
            excelWriter.fill(map, writeSheet);
        }
    }

    private static List<CarApplyRecord> data() {
        List<CarApplyRecord> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CarApplyRecord record = new CarApplyRecord();
            record.setCarNo("陕U" + i);
            record.setDriver("张" + i);
            record.setUseDesc("用途" + i);
            record.setUseCarDept("用车部门"+i);
            record.setApplyUserName("用车人" + i);
            record.setPeer("同行人" + i);
            record.setDepartureAddress("出发地" + i);
            record.setDepartureDate(LocalDateTime.now().minusWeeks(2));
            record.setDestination("目的地" + i);
            record.setReturnDate(LocalDateTime.now());
            record.setBackdate(LocalDateTime.now());
            record.setBackKil(i + 0.00);
            record.setCollectPrice(1000.00 + i);
            record.setCreateUserName("创建人" + i);
            record.setIsFinish("完成");
            list.add(record);
        }

        return list;
    }

}
