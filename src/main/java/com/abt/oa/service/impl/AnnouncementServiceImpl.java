package com.abt.oa.service.impl;

import com.abt.common.model.Pair;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.ValidateUtil;
import com.abt.oa.OAConstants;
import com.abt.oa.entity.Announcement;
import com.abt.oa.entity.AnnouncementAttachment;
import com.abt.oa.model.AnnouncementAttachmentRequestForm;
import com.abt.oa.model.AnnouncementDetail;
import com.abt.oa.model.AnnouncementRequestForm;
import com.abt.oa.reposity.AnnouncementAttachmentRepository;
import com.abt.oa.reposity.AnnouncementRepository;
import com.abt.oa.service.AnnouncementService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.model.entity.TUser;
import com.abt.sys.repository.TUserRepository;
import com.abt.sys.service.EmployeeService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.abt.common.util.QueryUtil.like;
import static com.abt.oa.OAConstants.*;

/**
 *
 */
@Service
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {
    private final TUserRepository tUserRepository;

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementAttachmentRepository announcementAttachmentRepository;
    private final EmployeeService employeeService;



    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, AnnouncementAttachmentRepository announcementAttachmentRepository, EmployeeService employeeService,
                                   TUserRepository tUserRepository) {
        this.announcementRepository = announcementRepository;
        this.announcementAttachmentRepository = announcementAttachmentRepository;
        this.employeeService = employeeService;
        this.tUserRepository = tUserRepository;
    }

    @Override
    public void addTemp(Announcement announcement) {
        announcement.doTemp();
        announcementRepository.save(announcement);
    }

    @Override
    public List<Announcement> findAll() {
       return announcementRepository.findAll();
    }


    @Override
    public Page<Announcement> findBy(AnnouncementRequestForm requestForm) {
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getSize(), Sort.by(Sort.Order.desc("createDate")));
        AnnouncementSpecification spec = new AnnouncementSpecification();
        Specification<Announcement> cr = Specification.where(spec.titleLike(requestForm))
                .and(spec.fileTypeEqual(requestForm))
                .and(spec.afterStartDate(requestForm))
                .and(spec.beforeEndDate(requestForm))
                .and(spec.statusEqual(requestForm));
        return announcementRepository.findAll(cr, page);
    }

    @Override
    @Transactional
    public void delete(String id) {
        //validate
        ValidateUtil.ensurePropertyNotnull(id, "行政通知实体id");
        //只有草稿可以删除
        final Announcement entity = findEntity(id);
        final boolean isTemp = entity.isTemp();
        if (!isTemp) {
            throw new BusinessException("行政通知只有草稿可以删除！请撤回后再删除!");
        }
        //2. delete
        announcementRepository.deleteById(id);
        announcementAttachmentRepository.deleteByAnnouncementId(id);
    }

    public List<AnnouncementAttachment> publishToAll(Announcement announcement) {
        List<AnnouncementAttachment> list = new ArrayList<>();
        List<EmployeeInfo> employed = employeeService.findAllByExit(false);
        employed.forEach(i -> {
            if (StringUtils.isNotBlank(i.getUserid())) {
                //可能存在以前离职的员工没有录入系统User表中
                AnnouncementAttachment attachment = AnnouncementAttachment.create(announcement, i.getUserid(), i.getName());
                list.add(attachment);
            }
        });
        return list;
    }

    //指定用户
    public List<AnnouncementAttachment> publishToSpecific(Announcement announcement) {
        List<AnnouncementAttachment> list = new ArrayList<>();
        String toUsers = announcement.getNodeDesignates();
        final String[] toUsernames = announcement.getNodeDesignateTxts().split(",");
        List<String> userIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(toUsers)) {
            try {
                userIdList = JsonUtil.ObjectMapper().readValue(toUsers, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                throw new BusinessException("行政通知:JSON转换失败!");
            }
            for (int i = 0; i < userIdList.size(); i++) {
                AnnouncementAttachment attachment = AnnouncementAttachment.create(announcement, userIdList.get(i), toUsernames[i]);
                list.add(attachment);
            }
        }
        return list;
    }

    @Transactional
    @Override
    public void publish(String id, String publisher) {
        ValidateUtil.ensurePropertyNotnull(id, "行政通知实体id");
        Announcement entity = findEntity(id);
        entity.doPublish(publisher);
        final Announcement announcement = announcementRepository.save(entity);
        announcementAttachmentRepository.deleteByAnnouncementId(announcement.getId());
        if (OAConstants.ANNOUNCEMENT_ZDTYPE_ALL.equals(announcement.getZdType())) {
            List<AnnouncementAttachment> list = publishToAll(announcement);
            announcementAttachmentRepository.saveAllAndFlush(list);
        } else if (OAConstants.ANNOUNCEMENT_ZDTYPE_SPEC.equals(announcement.getZdType())) {
            final List<AnnouncementAttachment> list = publishToSpecific(announcement);
            announcementAttachmentRepository.saveAllAndFlush(list);
        } else {
            log.error("未知的类型:zdType: {}", announcement.getZdType());
            throw new BusinessException("未知的指定方式: zdType" + announcement.getZdType());
        }

    }

    //撤回
    @Transactional
    @Override
    public void recall(String id) {
        ValidateUtil.ensurePropertyNotnull(id, "行政通知实体id");
        Announcement entity = findEntity(id);
        entity.doTemp();
        announcementRepository.save(entity);
        announcementAttachmentRepository.deleteByAnnouncementId(entity.getId());
    }


    public Announcement findEntity(String id) {
        return announcementRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到行政通知(id: " + id + ")"));
    }

    @Override
    public void reply(AnnouncementAttachment attachment) {
        AnnouncementAttachment entity = findAttachment(attachment.getId());
        if (StringUtils.isNotBlank(attachment.getHfContent())) {
            entity.setIsHf(ANNOUNCEMENT_ATTACHMENT_HF);
            entity.setHfContent(attachment.getHfContent());
        } else {
            entity.setIsHf(ANNOUNCEMENT_ATTACHMENT_UNHF);
        }
        entity.setIsSer(ANNOUNCEMENT_ATTACHMENT_READ);
        entity.setReadDate(LocalDateTime.now());
        announcementAttachmentRepository.save(entity);
    }

    @Override
    public AnnouncementDetail loadDetail(String id) {
        final Announcement entity = findEntity(id);
        AnnouncementDetail detail = new AnnouncementDetail();
        detail.setAnnouncement(entity);
        //统计
        final List<AnnouncementAttachment> list = announcementAttachmentRepository.findByAnnouncementId(id);
        if (list.isEmpty()) {
            return detail;
        }
        int total = list.size();
        detail.setNotifyCount(total);
        //不用回复
        if (ANNOUNCEMENT_UNREQUIRE_HF.equals(entity.getIsHf())) {
            return detail;
        }
        final int readCount = (int)list.stream().filter(AnnouncementAttachment::isRead).count();
        detail.setReadCount(readCount);
        final int commentCount = (int)list.stream().filter(AnnouncementAttachment::isHf).count();
        detail.setCommentCount(commentCount);
        detail.setUnreadCount(total - readCount);
        detail.setUncommentCount(total - commentCount);
        final List<Pair> commentMap = list.stream().filter(AnnouncementAttachment::isHf)
                .map(attachment -> new Pair(attachment.getApplyUserName(), attachment.getHfContent()))
                .collect(Collectors.toList());
        detail.setCommentMap(commentMap);
        final List<String> uncommentUsers = list.stream().filter(i -> !i.isHf()).map(AnnouncementAttachment::getApplyUserName).toList();
        detail.setUncommentUsers(uncommentUsers);
        final List<String> commentUsers = list.stream().filter(AnnouncementAttachment::isHf).map(AnnouncementAttachment::getApplyUserName).toList();
        detail.setCommentUsers(commentUsers);
        return detail;
    }

    @Override
    public void read(String id) {
        AnnouncementAttachment attachment = findAttachment(id);
        read(attachment);
        announcementAttachmentRepository.save(attachment);
    }

    @Override
    public List<AnnouncementAttachment> findUnreadAnnouncementAttachment(String userid) {
        return announcementAttachmentRepository.findByApplyUserIDAndIsSer(userid, ANNOUNCEMENT_ATTACHMENT_UNREAD);
    }

    public AnnouncementAttachment findAttachment(String id) {
        return announcementAttachmentRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到行政通知回复实体(id: " + id + ")"));
    }

    private AnnouncementAttachment read(AnnouncementAttachment attachment) {
        attachment.setIsSer(ANNOUNCEMENT_ATTACHMENT_READ);
        attachment.setReadDate(LocalDateTime.now());
        return attachment;
    }

    @Override
    public Page<AnnouncementAttachment> findAttachmentBy(AnnouncementAttachmentRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getSize(),
                Sort.by(Sort.Order.desc("isSer"), Sort.Order.desc("isHf"), Sort.Order.desc("createDate")));
        AnnouncementAttachmentSpecification spec = new AnnouncementAttachmentSpecification();
        Specification<AnnouncementAttachment> cr = Specification.where(spec.useridEqual(requestForm))
                .and(spec.titleLike(requestForm))
                .and(spec.isReply(requestForm))
                .and(spec.isRead(requestForm))
                .and(spec.fileTypeEquals(requestForm))
                ;
        return announcementAttachmentRepository.findAll(cr, pageable);
    }

    @Override
    public int setReadAll(String userid) {
        final List<AnnouncementAttachment> list = announcementAttachmentRepository.findByApplyUserID(userid);
        list.forEach(this::read);
        announcementAttachmentRepository.saveAllAndFlush(list);
        return list.size();
    }

    //查询发送给所有人的
    @Override
    public List<Announcement> findAnnouncementsToAll() {
        return announcementRepository.findByZdTypeAndFileTypeOrderByCreateDateDesc(ANNOUNCEMENT_ZDTYPE_ALL, ANNOUNCEMENT_FILETYPE_RULES);
    }

    @Transactional
    @Override
    public int sendAnnouncementsToUser(String jobNumber) {
        final List<Announcement> list = findAnnouncementsToAll();
        AnnouncementAttachmentRequestForm form = new AnnouncementAttachmentRequestForm();
        final TUser user = tUserRepository.findByEmpnum(jobNumber);
        form.setUserid(user.getId());
        form.setLimit(999);
        final List<String> ids = list.stream().map(Announcement::getId).toList();
        announcementAttachmentRepository.deleteAllByAnnouncementIdsAndApplyUserId(ids, user.getId());
        addAttachments(user.getId(), user.getName(), list);
        return list.size();
    }


    public void addAttachments(String userid, String username, List<Announcement> announcements) {
//        if (announcements == null || announcements.isEmpty()) {
//            throw new BusinessException("请选择要发送的通知!");
//        }
        if (announcements == null || announcements.isEmpty()) {
            log.warn("没有要发送的消息!");
            return;
        }
        List<AnnouncementAttachment> list = new ArrayList<>();
        announcements.forEach(a -> {
            AnnouncementAttachment attachment = AnnouncementAttachment.create(a, userid, username);
            list.add(attachment);
        });
        announcementAttachmentRepository.saveAll(list);
    }

    class AnnouncementAttachmentSpecification {
        public Specification<AnnouncementAttachment> useridEqual(AnnouncementAttachmentRequestForm form) {
            return (root, query, builder) -> {
                if (form.getUserid() != null) {
                    return builder.equal(root.get("applyUserID"), form.getUserid());
                }
                return null;
            };
        }
        public Specification<AnnouncementAttachment> useridEqualAndHasAll(AnnouncementAttachmentRequestForm form) {
            return (root, query, builder) ->
                    builder.or(builder.equal(root.get("applyUserID"), form.getUserid()),
                            builder.equal(root.get("applyUserID"), "all"));
        }

        public Specification<AnnouncementAttachment> useridIsAll() {
            return (root, query, builder) -> builder.equal(root.get("applyUserID"), AnnouncementAttachment.TO_ALL_ID);
        }

        //搜索:  title模糊搜索
        public Specification<AnnouncementAttachment> titleLike(AnnouncementAttachmentRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getQuery())) {
                    return builder.like(root.get("title"), like(form.getQuery()));
                }
                return null;
            };
        }

        //是否已读
        public Specification<AnnouncementAttachment> isRead(AnnouncementAttachmentRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getIsSer())) {
                    return builder.equal(root.get("isSer"), form.getIsSer());
                }
                return null;
            };
        }

        //是否回复
        public Specification<AnnouncementAttachment> isReply(AnnouncementAttachmentRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getIsHf())) {
                    return builder.like(root.get("isHf"), form.getIsHf());
                }
                return null;
            };
        }

        //文件类型
        public Specification<AnnouncementAttachment> fileTypeEquals(AnnouncementAttachmentRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getFileType())) {
                    return builder.like(root.get("fileType"), form.getFileType());
                }
                return null;
            };
        }
    }

    //搜索: 状态，发布时间， title模糊搜索， 文件类型
    class AnnouncementSpecification  {

        //搜索: 发布时间
        public Specification<Announcement> afterStartDate(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (form.getStartDate() != null) {
                    return builder.greaterThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getStartDate()));
                }
                return null;
            };
        }

        //搜索: 发布时间
        public Specification<Announcement> beforeEndDate(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (form.getEndDate() != null) {
                    return builder.lessThanOrEqualTo(root.get("createDate"), LocalDate.parse(form.getEndDate()));
                }
                return null;
            };
        }

        //搜索: 状态
        public Specification<Announcement> statusEqual(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getState())) {
                    return builder.equal(root.get("status"), form.getState());
                }
                return null;
            };
        }

        //搜索:  title模糊搜索
        public Specification<Announcement> titleLike(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (form.getQuery() != null) {
                    return builder.like(root.get("title"), like(form.getQuery()));
                }
                return null;
            };
        }
        //搜索: 文件类型
        public Specification<Announcement>  fileTypeEqual(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getFileType())) {
                    return builder.equal(root.get("fileType"), form.getFileType());
                }
                return null;
            };
        }


    }

}
