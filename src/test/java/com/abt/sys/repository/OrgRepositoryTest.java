package com.abt.sys.repository;

import com.abt.sys.model.entity.Org;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.abt.sys.service.impl.SqlServerUserServiceImpl.ORG_ROOT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrgRepositoryTest {

    @Autowired
    private OrgRepository orgRepository;

    @Test
    void findAllByParentIdOrderByCascadeId() {
        final List<Org> all = orgRepository.findAllByParentIdOrderByCascadeId(ORG_ROOT);
        assertNotNull(all);
        all.forEach(i -> System.out.println(i.toString()));
    }
}