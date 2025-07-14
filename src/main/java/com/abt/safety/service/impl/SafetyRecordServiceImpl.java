package com.abt.safety.service.impl;

import com.abt.common.util.QueryUtil;
import com.abt.common.util.TokenUtil;
import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.entity.SafetyRectify;
import com.abt.safety.model.RecordStatus;
import com.abt.safety.model.SafetyRecordRequestForm;
import com.abt.safety.repository.SafetyRecordRepository;
import com.abt.safety.repository.SafetyRectifyRepository;
import com.abt.safety.service.SafetyRecordService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;

import com.abt.safety.entity.SafetyForm;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.SystemMessageService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.abt.safety.Constants.SERVICE_SAFETY;

/**
 * 安全检查记录
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SafetyRecordServiceImpl implements SafetyRecordService {

    private final SafetyRecordRepository safetyRecordRepository;
    private final SafetyRectifyRepository safetyRectifyRepository;
    private final SystemMessageService systemMessageService;

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


    @Override
    public SafetyRecord loadRecord(String id) {
        final SafetyRecord record = safetyRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("未查询到安全检查记录(id=" + id + ")"));
        WithQueryUtil.build(record);
        final List<SafetyRectify> rectifies = safetyRectifyRepository.findByRecordId(id);
        record.setSafetyRectifyList(rectifies);
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
                        criteriaBuilder.equal(root.get("dispatcherId"), requestForm.getUserid())
//                        criteriaBuilder.equal(root.get("rectifierId"), requestForm.getUserid())
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
    public SafetyRectify findRectifyById(int id) {
        return safetyRectifyRepository.findById(id).orElseThrow(() -> new BusinessException("未查询到整改记录(id=" + id + ")"));
    }

    @Override
    public List<SafetyRectify> findRectifyListByRecordId(String recordId) {
        return safetyRectifyRepository.findByRecordId(recordId);
    }

    @Transactional
    @Override
    public SafetyRecord dispatch(SafetyRecord record, String dispatcherId, String dispatcherName, String rectifierId, String rectifierName) {
        final SafetyRectify rectifyRecord = createRectifyRecord(record.getId(), rectifierId, rectifierName, dispatcherId, dispatcherName);
        record.setState(RecordStatus.DISPATCHED);
        final SafetyRecord save = safetyRecordRepository.save(record);
        final SafetyRectify rectify = safetyRectifyRepository.save(rectifyRecord);
        save.setCurrentRectify(rectify);
        return save;
    }

    /**
     * 负责人整改
     * @param safetyRectify 整改记录
     */
    @Override
    public SafetyRecord rectify(SafetyRectify safetyRectify, SafetyRecord safetyRecord) {
        safetyRecord.setState(RecordStatus.RECTIFIED);
        safetyRectify.setRectifyTime(LocalDateTime.now());
        final SafetyRectify rectify = safetyRectifyRepository.save(safetyRectify);
        final SafetyRecord save = safetyRecordRepository.save(safetyRecord);
        save.setCurrentRectify(rectify);
        return save;
    }

    @Override
    public void rectifyPass(SafetyRectify safetyRectify) {
        safetyRectify.pass();
        safetyRectifyRepository.save(safetyRectify);
    }

    @Override
    public void complete(SafetyRecord safetyRecord) {
        safetyRecord.complete();
        safetyRecordRepository.save(safetyRecord);
    }

    @Override
    public void rectifyReject(SafetyRectify safetyRectify) {
        safetyRectify.reject();
        safetyRectifyRepository.save(safetyRectify);
    }

    @Transactional
    @Override
    public void logicDelete(String id) {
        safetyRecordRepository.logicDelete(id);
    }

    /**
     * 创建一条整改记录
     */
    public SafetyRectify createRectifyRecord(String recordId, String rectifierId, String rectifierName, String dispatcherId, String dispatcherName) {
        SafetyRectify rectify = new SafetyRectify();
        rectify.setRecordId(recordId);
        rectify.setRectifierId(rectifierId);
        rectify.setRectifierName(rectifierName);
        rectify.setDispatcherId(dispatcherId);
        rectify.setDispatcherName(dispatcherName);
        rectify.setDispatchTime(LocalDateTime.now());
        return rectify;
    }

    @Override
    public void remind(String toUserid, String toUsername, String fromUsername, SafetyRecord record) {
        record.remind(toUserid, toUsername);
        String msg = String.format("%s请您及时整改检查问题", fromUsername);
        final SystemMessage sysmsg = systemMessageService.createImportantSystemMsg(toUserid, toUsername, msg, SERVICE_SAFETY, record.getId());
        sysmsg.setImportantPriority();
    }

    @Override
    public Optional<SafetyRectify> findRunningRectify(String recordId) {
        return safetyRectifyRepository.findRunningRectify(recordId);
    }

}
