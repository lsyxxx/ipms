package com.abt.chemicals.repository;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.service.BasicDataService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@Rollback(value = false)
class TypeRepositoryTest {

    @Autowired
    private TypeRepository typeRepository;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void findByLevelAndNameLikeOrderBySortAsc() {
        final List<ChemicalType> list = typeRepository.findByLevelAndNameContainingOrderBySortAsc(2, "酸");
        Assert.notEmpty(list, "list is empty");
        list.forEach(i -> {
            log.info(i.toString());
        });
    }

    @Test
    void saveAll() {
        ChemicalType type1 = new ChemicalType();
        type1.setId("70d309b8-7116-432f-86ce-912ea91e7822");
        type1.setName("name9999");
        type1.setLevel(1);

        ChemicalType type2 = new ChemicalType();
        type2.setId("8aead23f-e211-4aeb-934d-19df643bc37e");
        type2.setName("name0002");
        type2.setLevel(1);

        List<ChemicalType> list = new ArrayList<>();
        list.add(type1);
        list.add(type2);
        typeRepository.saveAllAndFlush(list);

    }
}