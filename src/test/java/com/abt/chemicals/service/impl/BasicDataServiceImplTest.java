package com.abt.chemicals.service.impl;

import com.abt.chemicals.entity.*;
import com.abt.chemicals.model.StandardType;
import com.abt.chemicals.service.BasicDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;


@SpringBootTest
@Slf4j
class BasicDataServiceImplTest {

    @Autowired
    private BasicDataService basicDataService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void queryType() {

    }

    @Test
    void queryType2() {
        final List<ChemicalType> list = basicDataService.queryType(null, 2, null);
        Assert.notEmpty(list, "list is empty");
        list.forEach(i -> {
            log.info("id: {}, parentId: {}, name: {}, childrenSize: {}, parent: {}", i.getId(), i.getParentId(), i.getName(), i.getChildren().size(), i.getParent() == null ? null : i.getParent().getName());
        });
    }

    @Test
    void deleteType() {
        basicDataService.deleteType("");
    }

    @Test
    void editType() {
        ChemicalType form = new ChemicalType();
        //add type1
//        form.setName("测试123132");
//        form.setLevel(1);
//        basicDataService.editType(form);
        //edit type1
//        form.setId("e70834af-3307-42bf-829b-6403a3ef9df4");
//        form.setName("测试编辑111");
//        form.setNote("备注jaofjasf");
//        form.setSort(0);
//        basicDataService.editType(form);
        //add type2
//        form.setName("测试添加功能");
//        basicDataService.addType(form);
        //edit type2
        form.setName("测试111222");
        form.setLevel(2);
        form.setParentId("e70834af-3307-42bf-829b-6403a3ef9df4");
        basicDataService.saveType(form);
    }

    @Test
    void queryCompany() {
        basicDataService.queryAllCompanyByType("producer");
    }

    @Test
    void dynamicCompanyQuery() {

    }

    @Test
    void deleteCompany() {
    }

    @Test
    void saveProduct() {
        Product form = new Product();
        form.setName("酸化缓蚀剂")
                .setType2("uuid5-3")
                .setType1("uuid5")
                .setUsage("在酸化作业措施时可以防止或减缓酸液材料对设备设施的腐蚀。")
                .setManufacturing("酸化缓蚀剂主料与溶剂按比例混合物搅拌均匀。");
        form.setStandards(List.of(new Standard().setCode("SY-T5405").setName("酸化用缓蚀剂性能试验方法及评价指标").setType(StandardType.HB.getCode())));
        form.setMainMaterial(List.of(Material.main("咪唑啉"), Material.main("有机含氮化合物"), Material.main("醇类异构体")));
        List<Company> producers = new ArrayList<>();
        Company producer1 = new Company();
        producer1.setId("f3c6f5af-482d-4cb5-bc7b-976140accb85");
        producer1.setPriceList(List.of(Price.of(12220.11, "元/吨", LocalDate.now())
                , Price.of(1444.77, "元/吨", LocalDate.of(2023, 12, 7))));
        producer1.setContactList(List.of(Contact.of("张经理", "13778566545", null),
                Contact.of("王经理", "17777777777", null)));
        Company producer2 = new Company();
        producer2.setId("592c9397-ce3b-4d28-a79b-4e32d6c5862d");
        producer2.setPriceList(List.of(Price.of(19999.99, "元/吨", LocalDate.of(2024, 1, 4))
                , Price.of(16666.66, "元/吨", LocalDate.of(2023, 12, 7))));
        producer2.setContactList(List.of(Contact.of("钱经理", "16666666666", null),
                Contact.of("赵主任", "199999999", null)));
        producers.add(producer1);
        producers.add(producer2);
        form.setProducers(producers);
        List<Company> buyers = new ArrayList<>();
        Company buyer1 = new Company();
        buyer1.setId("17d5ae83-6ab6-4161-996d-b3c4a4d73ab5");
        buyer1.setPriceList(List.of(Price.of(22222.22, "元/吨", LocalDate.of(2024, 2, 4))
                , Price.of(33333.33, "元/吨", LocalDate.of(2023, 11, 1))));
        buyer1.setContactList(List.of(Contact.of("孙科长", "16666666666", null),
                Contact.of("赵主任", "199999999", null)));
        buyers.add(buyer1);
        form.setBuyers(buyers);

        basicDataService.saveProduct(form);

    }

    @Test
    void queryProductById() {
        final Product product = basicDataService.queryProductById("5a57e140-d97a-4930-8363-0c24ab70cc56");
        log.info("base --------------- ");
        log.info(product.baseInfo());
        log.info("main material-------------");
        log.info(product.getMainMaterial().toString());
        log.info("aux material---------------");
        log.info(product.getAuxMaterial().toString());
        log.info("standard ------------------");
        log.info(product.getStandards().toString());
        product.getProducers().forEach(p -> {
            log.info("producer: {} ----------------", p.getName());
            p.getPriceList().forEach(i -> {
                log.info("+-- price: {}", i.toString());
            });
            p.getContactList().forEach(i -> {
                log.info("+-- contact: {}", i.toString());
            });
        });
        product.getBuyers().forEach(p -> {
            log.info("producer: {} ----------------", p.getName());
            p.getPriceList().forEach(i -> {
                log.info("+-- price: {}", i.toString());
            });
            p.getContactList().forEach(i -> {
                log.info("+-- contact: {}", i.toString());
            });
        });


    }

    @Test
    void saveCompany() {
        Company form1 = new Company();
        form1.setType(Company.TYPE_BUYER);
        form1.setEnable(true);
        form1.setName("延长石油采气一厂");
        form1.setAddress("延安");
        basicDataService.saveCompany(form1);
        Company form2 = new Company();
        form2.setType(Company.TYPE_BUYER);
        form2.setEnable(true);
        form2.setName("辽河油田");
        form2.setAddress("辽宁");
        basicDataService.saveCompany(form2);
        Company form3 = new Company();
        form3.setType(Company.TYPE_PRODUCER);
        form3.setEnable(true);
        form3.setName("辽河油田");
        form3.setAddress("辽宁");
        basicDataService.saveCompany(form3);
    }
}