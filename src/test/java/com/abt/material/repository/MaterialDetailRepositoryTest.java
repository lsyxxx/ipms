package com.abt.material.repository;

import com.abt.material.entity.MaterialDetail;
import com.abt.material.model.IMaterialDetailDTO;
import com.abt.material.model.MaterialDetailDTO;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

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

    @Test
    void findAllWithInventories() {

    }

    @Test
    void testFindAllWithInventories() {
        final List<IMaterialDetailDTO> list = materialDetailRepository.findAllWithInventories(
                List.of("aa75e892-c3b3-4bac-a8db-4d36d25f3f46", "03666897-12bf-4d71-977d-00693e487a28"), List.of("all"));
        assertNotNull(list);
        System.out.println(list.size());
    }

}