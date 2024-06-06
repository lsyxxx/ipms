package com.abt.oa.reposity;

import com.abt.oa.entity.AnnouncementAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AnnouncementAttachmentRepository extends JpaRepository<AnnouncementAttachment, String>, JpaSpecificationExecutor<AnnouncementAttachment> {

    void deleteByAnnouncementId(String announcementId);


    List<AnnouncementAttachment> findByAnnouncementId(String announcementId);

    List<AnnouncementAttachment> findByApplyUserID(String applyUserID);
    List<AnnouncementAttachment> findByApplyUserIDAndIsSer(String applyUserID, String isSer);
}