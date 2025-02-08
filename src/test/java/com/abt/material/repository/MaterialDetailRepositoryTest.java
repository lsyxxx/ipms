package com.abt.material.repository;

import com.abt.material.entity.MaterialDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MaterialDetailRepositoryTest {
    @Autowired
    MaterialDetailRepository materialDetailRepository;

    @Test
    void findWithMaterialType() {
        Pageable pageable = PageRequest.of(0, 9999);

        final Page<MaterialDetail> withMaterialType = materialDetailRepository.findAll(pageable);
        assertNotNull(withMaterialType);
        System.out.println(withMaterialType.getContent().size());
//        withMaterialType.getContent().forEach(System.out::println);
    }
}