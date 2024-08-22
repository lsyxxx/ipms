package com.abt.oa.reposity;

import com.abt.oa.entity.AnnouncementAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AnnouncementAttachmentRepository extends JpaRepository<AnnouncementAttachment, String>, JpaSpecificationExecutor<AnnouncementAttachment> {

    void deleteByAnnouncementId(String announcementId);


    List<AnnouncementAttachment> findByAnnouncementId(String announcementId);

    List<AnnouncementAttachment> findByApplyUserID(String applyUserID);
    List<AnnouncementAttachment> findByApplyUserIDAndIsSer(String applyUserID, String isSer);

    @Modifying
    @Transactional
    @Query("DELETE FROM AnnouncementAttachment e WHERE e.announcementId IN :aids and e.applyUserID = :userid")
    int deleteAllByAnnouncementIdsAndApplyUserId(List<String> aids, String userid);
}