package com.abt.sys.repository;

import com.abt.sys.model.entity.SystemFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class SystemFileRepositoryTest {

    @Autowired
    private SystemFileRepository systemFileRepository;

    private SystemFile saved;

    @BeforeEach
    void setUp() {
        saved = new SystemFile();
        saved.setBizType("wf_reim_5000");
        saved.setName("testFile.png");
        saved.setUrl("root/u/testFile.png");
        saved.setRelationId1("rel1");
        saved.setRelationId2("rel2");
        saved.setDescription("测试测试123");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void deleteInBatch() {
    }

    @Test
    void softDelete() {
        String id = "123";
        final Optional<SystemFile> byId = systemFileRepository.findById(id);
        log.info("before delete, is_del: {}", byId.get().isDeleted());
        systemFileRepository.softDelete(id);
        log.info("soft deleted....");
        final Optional<SystemFile> find = systemFileRepository.findById(id);
        if (find.isEmpty()) {
            log.error("实体不存在, id: {}", id);
        } else {
            saved = find.get();
            log.info("is_del: {}", saved.isDeleted());
        }


    }
}