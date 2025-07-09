package com.abt.safety.service.impl;

import com.abt.common.util.QueryUtil;
import com.abt.common.util.TokenUtil;
import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.model.RecordStatus;
import com.abt.safety.model.SafetyRecordRequestForm;
import com.abt.safety.repository.SafetyRecordRepository;
import com.abt.safety.service.SafetyRecordService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;

import com.abt.safety.entity.SafetyForm;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.WithQueryUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 安全检查记录
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SafetyRecordServiceImpl implements SafetyRecordService {

    private final SafetyRecordRepository safetyRecordRepository;

    @Override
    public boolean recordExist(LocalDate checkDate, Long formId) {
        return safetyRecordRepository.checkSubmitDuplicated(checkDate.atStartOfDay(), checkDate.plusDays(1L).atStartOfDay(), formId);
    }

    @Transactional
    @Override
    public SafetyRecord saveCheck(SafetyForm form) {
        final SafetyRecord record = createRecordWithTokenUser(form);
        record.setFormId(form.getId());
        record.setState(RecordStatus.SUBMITTED);
        record.setCheckType(form.getCheckType());
        record.setLocationType(form.getLocationType());
        //判断是否完成, 没有问题就算结束
        record.calcProblemCount();
        record.calcHasProblem();
        record.setCompleted(!record.isHasProblem());
        return safetyRecordRepository.save(record);
    }

    private SafetyRecord createRecordWithTokenUser(SafetyForm form) {
        SafetyRecord record = new SafetyRecord();
        record.setLocation(form.getLocation());
        final UserView uv = TokenUtil.getUserFromAuthToken();
        record.setCheckerId(uv.getId());
        record.setCheckerName(uv.getName());
        record.setCheckTime(LocalDateTime.now());
        record.setCheckFormInstance(form);
        return record;
    }

    private String getBase64Image(String fullPath) throws IOException {
        Path filePath = Paths.get(fullPath);
        if (!Files.exists(filePath)) {
            log.error("图片未找到! " + fullPath);
        }
        byte[] imageBytes = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @Override
    public SafetyRecord loadRecord(String id) {
        final SafetyRecord record = safetyRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("未查询到安全检查记录(id=" + id + ")"));
        WithQueryUtil.build(record);
        return record;
    }

    @Override
    public Page<SafetyRecord> findSafetyRecordPageable(SafetyRecordRequestForm requestForm) {
        PageRequest pageRequest = PageRequest.of(requestForm.jpaPage(), requestForm.getSize(), Sort.by("checkTime").descending());
        String query = requestForm.getQuery();
        Specification<SafetyRecord> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 添加未删除条件
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            if (StringUtils.isNotBlank(query) && StringUtils.isNotBlank(query.trim())) {
                String pattern = QueryUtil.like(query.toLowerCase());
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), pattern)
                ));
            }
            //查看权限条件
            if (StringUtils.isNotBlank(requestForm.getUserid())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("checkerId"), requestForm.getUserid()),
                        criteriaBuilder.equal(root.get("dispatcherId"), requestForm.getUserid()),
                        criteriaBuilder.equal(root.get("rectifierId"), requestForm.getUserid())
                ));
            }

            if (requestForm.getLocalStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("checkTime"), requestForm.getLocalStartDate().atStartOfDay()));
            }
            if (requestForm.getLocalEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("checkTime"), requestForm.getLocalEndDate().atTime(23, 59, 59)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return safetyRecordRepository.findAll(spec, pageRequest);
    }

    @Override
    public SafetyRecord loadRecordOnly(String id) {
        return safetyRecordRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到安全检查记录(id=" + id + ")"));
    }

    @Override
    public SafetyRecord dispatch(String id, String dispatcherId, String dispatcherName, String rectifierId, String rectifierName) {
        final SafetyRecord record = loadRecordOnly(id);
        record.setDispatcherId(dispatcherId);
        record.setDispatcherName(dispatcherName);
        record.setRectifierId(rectifierId);
        record.setRectifierName(rectifierName);
        record.setDispatchTime(LocalDateTime.now());
        record.setState(RecordStatus.DISPATCHED);
        return safetyRecordRepository.save(record);
    }

    @Override
    public SafetyRecord rectified(String id, String rectifyRemark, List<SystemFile> systemFiles) {
        final SafetyRecord record = loadRecordOnly(id);
        record.setRectifyRemark(rectifyRemark);
        record.setRectifyTime(LocalDateTime.now());
        record.setState(RecordStatus.RECTIFIED);
        record.setRectifyFiles(systemFiles);
        //整改完就算结束
        record.setCompleted(true);
        return safetyRecordRepository.save(record);
    }

    @Transactional
    @Override
    public void logicDelete(String id) {
        safetyRecordRepository.logicDelete(id);
    }

    /**
     * 读取检查记录图片
     */
    public void loadRecordImages(String id) {

    }

}
