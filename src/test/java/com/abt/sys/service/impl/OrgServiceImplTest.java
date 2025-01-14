package com.abt.sys.service.impl;

import com.abt.sys.model.dto.OrgRequestForm;
import com.abt.sys.model.entity.Org;
import com.abt.sys.service.OrgService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrgServiceImplTest {
    @Autowired
    private OrgService orgService;

    @Test
    void getAllBy() {
        OrgRequestForm form = new OrgRequestForm();
        form.setStatus(0);
        final List<Org> all = orgService.getAllBy(form);
        Assertions.assertNotNull(all, "not null");
        System.out.println(all.size());
        all.forEach(i -> {
            System.out.printf("name: %s, cascadeId: %s\n", i.getName(), i.getCascadeId());
        });

    }

    @Test
    void getAllDeptUserList() {

    }

    @Test
    void getABTOrgTree() {
        final Org orgTree = orgService.getABTOrgTree();
        System.out.println(orgTree.getName());

    }

}