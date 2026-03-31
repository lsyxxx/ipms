package com.abt.wxapp.checkmodule.repository;

import com.abt.wxapp.checkmodule.entity.CheckComponent;
import com.abt.wxapp.checkmodule.entity.DynamicScheme;
import com.abt.wxapp.checkmodule.entity.options.*;
import com.abt.wxapp.checkmodule.model.CheckComponentType;
import com.abt.wxapp.checkmodule.model.PayType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DynamicFormRepositoryTest {

    @Autowired
    private DynamicSchemeRepository dynamicFormRepository;

    @Test
    void test() {
        final Optional<DynamicScheme> byId = dynamicFormRepository.findById(1L);
        assertTrue(byId.isPresent());
    }

}
