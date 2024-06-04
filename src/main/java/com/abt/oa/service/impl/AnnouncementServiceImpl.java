package com.abt.oa.service.impl;

import com.abt.oa.entity.Announcement;
import com.abt.oa.model.AnnouncementRequestForm;
import com.abt.oa.reposity.AnnouncementRepository;
import com.abt.oa.service.AnnouncementService;
import com.abt.sys.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.abt.common.util.QueryUtil.like;

/**
 *
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    //添加一个草稿
    public void addTemp(Announcement announcement) {
        announcement.doTemp();
        announcementRepository.save(announcement);
    }

    public List<Announcement> findAll() {
       return announcementRepository.findAll();
    }


    //条件查询
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

    @Transactional
    public void delete(String id) {
        announcementRepository.deleteById(id);
        //TODO: 删除t_announcement_attachment

    }

    //发布消息
    public void publish(String id, String publisher) {
        Announcement entity = findEntity(id);
        entity.doPublish(publisher);
        announcementRepository.save(entity);
        //TODO: 生成t_announcement_attachment 对应数据
    }

    //撤回
    public void recall(String id) {
        Announcement entity = findEntity(id);
        entity.doTemp();
        announcementRepository.save(entity);
        //TODO: 删除t_announcement_attachment 对应数据
    }


    public Announcement findEntity(String id) {
        return announcementRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到行政通知(id: " + id + ")"));
    }


    //搜索: 状态，发布时间， title模糊搜索， 文件类型
    class AnnouncementSpecification  {

        //搜索: 发布时间
        public Specification<Announcement> afterStartDate(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (form.getStartDate() != null) {
                    return builder.greaterThanOrEqualTo(root.get("CreateDate"), LocalDate.parse(form.getStartDate()));
                }
                return null;
            };
        }

        //搜索: 发布时间
        public Specification<Announcement> beforeEndDate(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (form.getEndDate() != null) {
                    return builder.lessThanOrEqualTo(root.get("CreateDate"), LocalDate.parse(form.getEndDate()));
                }
                return null;
            };
        }

        //搜索: 状态
        public Specification<Announcement> statusEqual(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getState())) {
                    return builder.equal(root.get("Status"), form.getState());
                }
                return null;
            };
        }

        //搜索:  title模糊搜索
        public Specification<Announcement> titleLike(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (form.getId() != null) {
                    return builder.like(root.get("Title"), like(form.getQuery()));
                }
                return null;
            };
        }
        //搜索: 文件类型
        public Specification<Announcement>  fileTypeEqual(AnnouncementRequestForm form) {
            return (root, query, builder) -> {
                if (StringUtils.isNotBlank(form.getFileType())) {
                    return builder.equal(root.get("FileType"), form.getFileType());
                }
                return null;
            };
        }


    }

}
