package com.abt.oa.reposity;

import com.abt.oa.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, String>, JpaSpecificationExecutor<Announcement> {
    List<Announcement> findByZdTypeAndFileTypeOrderByCreateDateDesc(String zdType, String fileType);

}