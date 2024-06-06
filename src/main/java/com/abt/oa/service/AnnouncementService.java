package com.abt.oa.service;

import com.abt.oa.entity.Announcement;
import com.abt.oa.entity.AnnouncementAttachment;
import com.abt.oa.model.AnnouncementAttachmentRequestForm;
import com.abt.oa.model.AnnouncementDetail;
import com.abt.oa.model.AnnouncementRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AnnouncementService {
    /**
     * 添加一个草稿
     */
    void addTemp(Announcement announcement);

    List<Announcement> findAll();

    /**
     * 条件查询: 状态，发布时间， title模糊搜索， 文件类型
     * @param requestForm 搜索条件
     */
    Page<Announcement> findBy(AnnouncementRequestForm requestForm);

    @Transactional
    void delete(String id);

    /**
     * 发布消息
     * @param id 实体id
     * @param publisher 发布人名称
     */
    void publish(String id, String publisher);

    /**
     * 撤回
     * @param id 实体类id
     */
    void recall(String id);

    /**
     * 回复
     */
    void reply(AnnouncementAttachment attachment);

    AnnouncementDetail loadDetail(String id);

    /**
     * 标记已读
     * @param id 通知回复实体id
     */
    void read(String id);

    List<AnnouncementAttachment> findUnreadAnnouncementAttachment(String userid);

    Page<AnnouncementAttachment> findAttachmentBy(AnnouncementAttachmentRequestForm requestForm);

    int setReadAll(String userid);
}
