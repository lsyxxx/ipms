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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
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
}