package com.abt.testing.service.impl;

import com.abt.common.util.QueryUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.testing.entity.ReportCoverInfo;
import com.abt.testing.model.*;
import com.abt.testing.repository.ReportCoverInfoRepository;
import com.abt.testing.repository.ReportCoverInfoWriteLockRepository;
import com.abt.testing.repository.TCheckmoduleRepository;
import com.abt.testing.service.ReportCoverInfoService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/** 报告封面维护；JSON 关联在应用内校验，项目名称按当前页批量补充。 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportCoverInfoServiceImpl implements ReportCoverInfoService {
    private static final int ID_BATCH_SIZE = 1000;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createDate", "id");

    private final ReportCoverInfoRepository repository;
    private final TCheckmoduleRepository checkModuleRepository;
    private final ReportCoverInfoWriteLockRepository writeLockRepository;

    @Override
    public ReportCoverInfoVO findById(String id) {
        return toViews(List.of(requireRecord(id))).get(0);
    }

    @Override
    public Page<ReportCoverInfoVO> findPage(ReportCoverInfoRequestForm form) {
        form.forcePaged();
        if (form.getPage() < 1 || form.getLimit() < 1 || form.getLimit() > ReportCoverInfoRequestForm.PAGE_LIMIT) {
            throw new BusinessException("页码必须大于等于1，每页数量必须在1到999之间");
        }
        Page<ReportCoverInfo> page = repository.findAll(buildSpecification(form),
                PageRequest.of(form.jpaPage(), form.getLimit(), DEFAULT_SORT));
        Map<String, String> names = findModuleNames(collectModuleIds(page.getContent()));
        return page.map(record -> toView(record, names));
    }

    @Override
    public List<ReportCoverInfoVO> findList(ReportCoverInfoRequestForm form) {
        return toViews(repository.findAll(buildSpecification(form), DEFAULT_SORT));
    }

    @Override
    @Transactional
    public ReportCoverInfoVO save(ReportCoverInfoRequest request) {
        if (request.getId() != null) {
            throw new BusinessException("新增时不能指定ID");
        }
        lockWrites();
        ReportCoverInfo record = new ReportCoverInfo();
        applyRequest(record, request);
        validateRelations(record);
        ReportCoverInfo saved = repository.saveAndFlush(record);
        log.info("新增报告封面配置, id={}", saved.getId());
        return toViews(List.of(saved)).get(0);
    }

    @Override
    @Transactional
    public ReportCoverInfoVO update(ReportCoverInfoRequest request) {
        lockWrites();
        ReportCoverInfo record = requireRecord(request.getId());
        // 先校验独立候选对象，防止唯一性查询触发 Hibernate 自动 flush 未校验的修改。
        ReportCoverInfo candidate = new ReportCoverInfo();
        candidate.setId(record.getId());
        applyRequest(candidate, request);
        validateRelations(candidate);
        BeanUtils.copyProperties(candidate, record, "id", "createDate", "createUserid", "createUsername",
                "updateDate", "updateUserid", "updateUsername");
        ReportCoverInfo saved = repository.saveAndFlush(record);
        log.info("更新报告封面配置, id={}", saved.getId());
        return toViews(List.of(saved)).get(0);
    }

    @Override
    @Transactional
    public void delete(String id) {
        lockWrites();
        repository.delete(requireRecord(id));
        repository.flush();
        log.info("物理删除报告封面配置, id={}", id);
    }

    private ReportCoverInfo requireRecord(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BusinessException("封面配置ID不能为空");
        }
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("报告封面配置不存在"));
    }

    private void lockWrites() {
        Integer result = writeLockRepository.acquireWriteLock();
        if (result == null || result < 0) {
            log.error("获取报告封面配置写锁失败, result={}", result);
            throw new BusinessException("报告封面配置正在被修改，请稍后重试");
        }
    }

    private void applyRequest(ReportCoverInfo record, ReportCoverInfoRequest request) {
        if (!StringUtils.hasText(request.getCoverName())) {
            throw new BusinessException("封面检测项目名称不能为空");
        }
        Integer status = request.getStatus() == null ? ReportCoverStatus.ENABLED.getCode() : request.getStatus();
        if (status != ReportCoverStatus.ENABLED.getCode() && status != ReportCoverStatus.DISABLED.getCode()) {
            throw new BusinessException("状态只能为0禁用或1启用");
        }
        record.setCoverName(request.getCoverName().trim());
        record.setCheckModuleIds(normalizeList(request.getCheckModuleIds(), "关联检测项目ID", true));
        record.setItemCode(request.getItemCode());
        record.setSampleSpec(request.getSampleSpec());
        record.setSampleStatus(request.getSampleStatus());
        record.setInstruments(normalizeList(request.getInstruments(), "检测设备", false));
        record.setStandards(normalizeList(request.getStandards(), "检测依据", false));
        record.setCategory(request.getCategory());
        record.setVersion(request.getVersion());
        record.setOwnership(request.getOwnership());
        record.setRemark(request.getRemark());
        record.setStatus(status);
    }

    private List<String> normalizeList(List<String> values, String field, boolean distinct) {
        if (values == null) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (!StringUtils.hasText(value)) {
                throw new BusinessException(field + "不能为空");
            }
            result.add(value.trim());
        }
        return distinct ? new ArrayList<>(new LinkedHashSet<>(result)) : result;
    }

    /** 检查唯一名称、项目存在性与全表唯一归属（包含禁用配置）。 */
    private void validateRelations(ReportCoverInfo record) {
        repository.findByCoverName(record.getCoverName()).ifPresent(existing -> {
            if (!Objects.equals(existing.getId(), record.getId())) {
                throw new BusinessException("封面检测项目名称已存在");
            }
        });
        if (record.getCheckModuleIds().isEmpty()) {
            return;
        }
        Map<String, String> names = findModuleNames(record.getCheckModuleIds());
        for (String id : record.getCheckModuleIds()) {
            if (!names.containsKey(id)) {
                throw new BusinessException("关联检测项目不存在：" + id);
            }
        }
        Set<String> requestedIds = new HashSet<>(record.getCheckModuleIds());
        for (ReportCoverInfo existing : repository.findAll()) {
            if (Objects.equals(existing.getId(), record.getId())) {
                continue;
            }
            for (String id : existing.getCheckModuleIds()) {
                if (requestedIds.contains(id)) {
                    throw new BusinessException("检测项目已关联其他封面配置：" + names.get(id)
                            + "（" + existing.getCoverName() + "）");
                }
            }
        }
    }

    private Specification<ReportCoverInfo> buildSpecification(ReportCoverInfoRequestForm form) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(form.getCoverName())) {
                predicates.add(cb.like(root.get("coverName"), QueryUtil.like(form.getCoverName().trim())));
            }
            if (StringUtils.hasText(form.getItemCode())) {
                predicates.add(cb.like(root.get("itemCode"), QueryUtil.like(form.getItemCode().trim())));
            }
            if (StringUtils.hasText(form.getCategory())) {
                predicates.add(cb.equal(root.get("category"), form.getCategory()));
            }
            if (StringUtils.hasText(form.getVersion())) {
                predicates.add(cb.equal(root.get("version"), form.getVersion()));
            }
            if (StringUtils.hasText(form.getOwnership())) {
                predicates.add(cb.equal(root.get("ownership"), form.getOwnership()));
            }
            if (form.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), form.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<String> collectModuleIds(List<ReportCoverInfo> records) {
        return records.stream().flatMap(record -> record.getCheckModuleIds().stream()).distinct().toList();
    }

    /** 每批不超过 1000 个 ID，兼容 SQL Server 单语句参数上限。 */
    private Map<String, String> findModuleNames(List<String> ids) {
        List<String> distinctIds = new ArrayList<>(new LinkedHashSet<>(ids));
        Map<String, String> names = new HashMap<>();
        for (int start = 0; start < distinctIds.size(); start += ID_BATCH_SIZE) {
            List<String> batch = distinctIds.subList(start, Math.min(start + ID_BATCH_SIZE, distinctIds.size()));
            for (ReportCoverCheckModuleVO module : checkModuleRepository.findCoverCheckModules(batch)) {
                names.put(module.getId(), module.getName());
            }
        }
        return names;
    }

    private List<ReportCoverInfoVO> toViews(List<ReportCoverInfo> records) {
        Map<String, String> names = findModuleNames(collectModuleIds(records));
        return records.stream().map(record -> toView(record, names)).toList();
    }

    private ReportCoverInfoVO toView(ReportCoverInfo record, Map<String, String> names) {
        ReportCoverInfoVO view = new ReportCoverInfoVO();
        BeanUtils.copyProperties(record, view);
        view.setCheckModules(record.getCheckModuleIds().stream()
                .map(id -> new ReportCoverCheckModuleVO(id, names.get(id))).toList());
        return view;
    }
}
