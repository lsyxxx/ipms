package com.abt.oa.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.common.util.TokenUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.oa.entity.Announcement;
import com.abt.oa.entity.AnnouncementAttachment;
import com.abt.oa.model.AnnouncementAttachmentRequestForm;
import com.abt.oa.model.AnnouncementDetail;
import com.abt.oa.model.AnnouncementRequestForm;
import com.abt.oa.reposity.AnnouncementAttachmentRepository;
import com.abt.oa.service.AnnouncementService;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行政通知
 */
@RestController
@Slf4j
@RequestMapping("/ann")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AnnouncementAttachmentRepository announcementAttachmentRepository;

    public AnnouncementController(AnnouncementService announcementService, AnnouncementAttachmentRepository announcementAttachmentRepository) {
        this.announcementService = announcementService;
        this.announcementAttachmentRepository = announcementAttachmentRepository;
    }

    /**
     * 添加草稿
     */
    @PostMapping("/add/temp")
    public R<Object> addTemp( @Validated({ValidateGroup.Save.class}) @RequestBody Announcement announcement) {
        announcementService.addTemp(announcement);
        return R.success("添加草稿成功");
    }

    @GetMapping("/publish")
    public R<Object> publish(String id) {
        UserView user = TokenUtil.getUserFromAuthToken();
        announcementService.publish(id, user.getUsername());
        return R.success("发布成功");
    }

    @GetMapping("/del")
    public R<Object> delete(String id) {
        announcementService.delete(id);
        return R.success("删除成功");
    }

    @GetMapping("/recall")
    public R<Object> recall(String id) {
        announcementService.recall(id);
        return R.success("撤销成功");
    }

    @PostMapping("/reply")
    public R<Object> reply(@RequestBody AnnouncementAttachment attachment) {
        ValidateUtil.ensurePropertyNotnull(attachment.getAnnouncementId(), "行政通知实体id");
        ValidateUtil.ensurePropertyNotnull(attachment.getId(), "通知回复实体id");
        announcementService.reply(attachment);
        return R.success();
    }

    @GetMapping("/find")
    public R<List<Announcement>> findAnnouncementListBy(@ModelAttribute AnnouncementRequestForm requestForm) {
        final Page<Announcement> list = announcementService.findBy(requestForm);
        return R.success(list.getContent(), (int) list.getTotalElements());
    }

    @GetMapping("/detail")
    public R<AnnouncementDetail> findDetailById(String id) {
        final AnnouncementDetail detail = announcementService.loadDetail(id);
        return R.success(detail);
    }

    @GetMapping("/read")
    public R<Object> read(String id) {
        announcementService.read(id);
        return R.success("通知已标记为：已读");
    }

    @GetMapping("/attachment/find")
    public R<List<AnnouncementAttachment>> findAnnouncementAttachmentListBy(@ModelAttribute AnnouncementAttachmentRequestForm requestForm) {
        UserView user = TokenUtil.getUserFromAuthToken();
        requestForm.setUserid(user.getId());
        final Page<AnnouncementAttachment> page = announcementService.findAttachmentBy(requestForm);
        return R.success(page.getContent(), (int) page.getTotalElements());

    }

    @GetMapping("/attachmeent/readAll")
    public R<Integer> setReadAll() {
        UserView user = TokenUtil.getUserFromAuthToken();
        final int readCount = announcementService.setReadAll(user.getId());
        return R.success(readCount);
    }


    @GetMapping("/attachment/find/unread")
    public R<List<AnnouncementAttachment>> unreadAnnouncement() {
        UserView user = TokenUtil.getUserFromAuthToken();
        final List<AnnouncementAttachment> list = announcementService.findUnreadAnnouncementAttachment(user.getId());
        return R.success(list);
    }

    @GetMapping("/attachment/send")
    public R<Object> sendAnnouncementToUser(@RequestParam String jobNumber) {
        final int size = announcementService.sendAnnouncementsToUser(jobNumber);
        return R.success("已向用户[工号:" + jobNumber + "]发送" + size + "条规章制度");
    }
}
