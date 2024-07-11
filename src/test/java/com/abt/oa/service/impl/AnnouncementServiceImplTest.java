package com.abt.oa.service.impl;

import com.abt.oa.reposity.AnnouncementAttachmentRepository;
import com.abt.oa.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnnouncementServiceImplTest {

    @Autowired
//    private AnnouncementService announcementService;
    private AnnouncementAttachmentRepository repository;

    @Test
    void findAll() {
    }

}