package com.abt.chkmodule.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@EnableJpaRepositories(basePackages = "com.abt.chkmodule.repository")
@EntityScan(basePackages = "com.abt.chkmodule.entity")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=zjdata",
        "spring.datasource.username=sa",
        "spring.datasource.password=123456",
        "spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver"
})
class CheckModuleRepositoryTest {


//    @Autowired
//    private CheckModuleRepository checkModuleRepository;
//
//
//    @Test
//    void testDelete() {
//
//    }
}
