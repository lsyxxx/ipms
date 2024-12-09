package com.abt.wf.service.impl;

import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.service.ExcelService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.ImageData.ImageType;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ExcelServiceImpl implements ExcelService {

    public static void main(String[] args) throws IOException {
        write();
        System.out.println("写入完成");
    }

    public static void write() throws IOException {
        String templateFileName = "C:\\Users\\Administrator\\Desktop\\purchase_apply_template.xlsx";
        String newFile = "C:\\Users\\Administrator\\Desktop\\test.xlsx";
        List<PurchaseApplyDetail> list = new ArrayList<PurchaseApplyDetail>();
        PurchaseApplyDetail d1 = new PurchaseApplyDetail();;
        d1.setName("剪刀");
        d1.setPrice(new BigDecimal("2.00"));
        d1.setSpecification("大号");
        d1.setUnit("把");
        d1.setQuantity(4);
        list.add(d1);
        PurchaseApplyDetail d2 = new PurchaseApplyDetail();;
        d2.setName("记号笔");
        d2.setPrice(new BigDecimal("5.68"));
        d2.setSpecification("红色");
        d2.setUnit("个");
        d2.setQuantity(3);
        list.add(d2);
        PurchaseApplyDetail d3 = new PurchaseApplyDetail();;
        d3.setName("本子");
        d3.setPrice(new BigDecimal("1.69"));
        d3.setSpecification("实验本");
        d3.setUnit("本");
        d3.setQuantity(9);
        list.add(d3);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("createDate", LocalDate.now());
        map.put("managerUpdate", LocalDate.of(2024, 11, 12));
        map.put("leaderUpdate", "2023-12-12");
        WriteCellData<Void> writeCellData = new WriteCellData<>();
        ImageData imageData = new ImageData();
        String imagePath = "F:\\00平台资料汇总\\签名\\签名\\抠出来的\\112刘宋菀-2.png";
        File im = new File(imagePath);
        imageData.setImage( Files.readAllBytes(im.toPath()));
        imageData.setImageType(ImageType.PICTURE_TYPE_PNG);
        imageData.setTop(5);
        imageData.setBottom(5);
        imageData.setLeft(5);
        imageData.setRight(5);

        ImageData imageData2 = new ImageData();
        String imagePath2 = "F:\\00平台资料汇总\\签名\\签名\\抠出来的\\002何爱平-2.png";
        File im2 = new File(imagePath2);
        imageData2.setImage( Files.readAllBytes(im2.toPath()));
        imageData2.setImageType(ImageType.PICTURE_TYPE_PNG);
        imageData2.setTop(5);
        imageData2.setBottom(5);
        imageData2.setLeft(5);
        imageData2.setRight(5);
        writeCellData.setImageDataList(List.of(imageData));
        map.put("createUserSig", writeCellData);
        WriteCellData<Void> writeCellData2 = new WriteCellData<>();
        writeCellData2.setImageDataList(List.of(imageData2));
        map.put("managerSig",writeCellData2);


        try (ExcelWriter excelWriter = EasyExcel.write(newFile).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            for (int i = 0; i < 20; i++) {
                excelWriter.fill(list, fillConfig, writeSheet);
            }
            excelWriter.fill(map, writeSheet);
        }

    }
}
