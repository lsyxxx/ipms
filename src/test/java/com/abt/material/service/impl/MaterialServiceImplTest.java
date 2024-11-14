package com.abt.material.service.impl;

import com.abt.material.entity.MaterialDetail;
import com.abt.material.service.MaterialService;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
class MaterialServiceImplTest {
    @Autowired
    private MaterialService materialService;

    @Autowired
    private PurchaseApplyMainRepository purchaseApplyMainRepository;

    @Test
    void findAll() {
        final List<MaterialDetail> all = materialService.findAll();
        Assert.notEmpty(all, "not empty");
        all.forEach(i -> {
            System.out.printf("name: %s, spec: %s, sort: %s, dept: %s\n", i.getName(), i.getSpecification(), i.getIndex(), i.getDeptName());
        });

    }


    @Test
    void testSaveCascade() {
        PurchaseApplyMain main = new PurchaseApplyMain();
        main.setCreateUserid("user222");
        main.setCreateUsername("name222");
        main.setCreateDeptName("部门2222");
        PurchaseApplyDetail d1 = new PurchaseApplyDetail();
        d1.setDetailId("dtlNo122");
        d1.setName("U盘");
        d1.setSpecification("64G");
        d1.setQuantity(2);
        //需要在双方的对象都添加...才能级联保存
        main.addDetail(d1);
        d1.setMain(main);
        purchaseApplyMainRepository.save(main);
    }

    @Test
    void testDelete() {
        final PurchaseApplyMain main = purchaseApplyMainRepository.findByIdWithDetails("0fba3298-13d9-46ce-b30f-84b88d7d898a");
        System.out.printf("detail size: %d\n", main.getDetails().size());
        main.getDetails().remove(0);
        purchaseApplyMainRepository.save(main);
    }

    @Test
    void testDeleteMain() {
        final PurchaseApplyMain main = purchaseApplyMainRepository.findById("04450687-72e4-4062-974e-ab32ca1587eb").orElseThrow();
        purchaseApplyMainRepository.deleteById(main.getId());
    }

    @Test
    void testGet() {
        final List<PurchaseApplyMain> all = purchaseApplyMainRepository.findAll();
        Assert.notEmpty(all, "not empty");
        System.out.printf("size: %d\n", all.size());
        all.forEach(i -> {
            System.out.printf("m_id: %s\n", i.getDetails().get(0).getMain().getId());
        });
    }

    @Test
    void testUpdateTime() {
        //更新了updatetime
        final PurchaseApplyMain main = purchaseApplyMainRepository.findById("c1c0c663-6183-4e0a-82ea-c448294635ba").orElseThrow();
        main.setBusinessState("审批中");
        purchaseApplyMainRepository.save(main);
    }

    @Test
    void testAddDetail() {
        final PurchaseApplyMain main = purchaseApplyMainRepository.findByIdWithDetails("c1c0c663-6183-4e0a-82ea-c448294635ba");
        PurchaseApplyDetail d1 = new PurchaseApplyDetail();
        d1.setDetailId("dtl888");
        d1.setName("天平");
        d1.setSpecification("精确0.000001");
        d1.setQuantity(1);
        main.addDetail(d1);
        //需要setMain
        d1.setMain(main);
        purchaseApplyMainRepository.save(main);
    }

    @Test
    void testUpdateDetail() {
        //修改子对象不会修改main的updatetime
        final PurchaseApplyMain main = purchaseApplyMainRepository.findByIdWithDetails("c1c0c663-6183-4e0a-82ea-c448294635ba");
        PurchaseApplyDetail detail = main.getDetails().get(0);
        detail.setFinalId("finalId_132123");
        purchaseApplyMainRepository.save(main);
    }
}