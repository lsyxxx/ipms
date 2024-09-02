package com.abt.finance.repository;

import com.abt.finance.entity.FixedAsset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FixedAssetRepositoryTest {

    @Autowired
    private FixedAssetRepository fixedAssetRepository;
    @Test
    void findTop1ByOrderByCreateDateDesc() {

    }
}