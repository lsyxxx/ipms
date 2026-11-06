package com.abt.oa.service.impl;

import com.abt.common.config.ValidateGroup;
import com.abt.oa.entity.*;
import com.abt.oa.enums.FieldVehicleStatus;
import com.abt.oa.model.*;
import com.abt.oa.reposity.*;
import com.abt.oa.service.FieldVehicleService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** 野外用车记录、车辆占用及还车结算；所有写操作在本层编排事务。 */
@Service
@Slf4j
public class FieldVehicleServiceImpl implements FieldVehicleService {
    public static final String MANAGER_AUTHORITY = "JS_OA_FIELD_VEHICLE_MANAGER";
    private static final int IN_BATCH_SIZE = 500;
    private static final String CAR_ENABLED = "1";
    private static final String CAR_BUSY = "1";
    private static final String CAR_IDLE = "0";
    private final FieldVehicleUseRepository records;
    private final FieldVehicleFuelRepository fuels;
    private final CarInfoRepository cars;
    private final Validator validator;
    private final TransactionTemplate transactions;

    public FieldVehicleServiceImpl(FieldVehicleUseRepository records, FieldVehicleFuelRepository fuels,
                                   CarInfoRepository cars, Validator validator, PlatformTransactionManager manager) {
        this.records = records;
        this.fuels = fuels;
        this.cars = cars;
        this.validator = validator;
        this.transactions = new TransactionTemplate(manager);
        this.transactions.setTimeout(30);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FieldVehicleListVO> findPage(FieldVehicleRequestForm form) {
        currentUser();
        validate(form);
        if (form.getPage() < 1 || form.getLimit() < 0 || form.getLimit() > 100) {
            throw new BusinessException("分页页码必须大于0，单页数量必须在1到100之间");
        }
        int size = form.getLimit() == 0 ? 20 : form.getLimit();
        validateStatus(form.getStatus());
        LocalDate start = parseDate(form.getStartDate());
        LocalDate end = parseDate(form.getEndDate());
        checkDateOrder(start, end, "查询开始日期不能晚于结束日期");
        Page<FieldVehicleUse> page = records.findAll(specification(form, start, end),
                PageRequest.of(form.getPage() - 1, size, Sort.by(Sort.Direction.DESC, "useDate", "createDate", "id")));
        return page.map(this::listVO);
    }

    @Override
    @Transactional(readOnly = true)
    public FieldVehicleDetailVO findById(String id) {
        currentUser();
        return detail(records.findById(requiredId(id)).orElseThrow(() -> new BusinessException("用车记录不存在")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldVehicleCarOptionVO> findList(CarOptionRequestForm form) {
        currentUser();
        validate(form);
        String query = trim(form.getQuery());
        List<CarInfo> vehicles = cars.findList(form.getIsAvtive(), query == null ? null : containsPattern(query));
        Map<String, BigDecimal> mileages = new HashMap<>();
        Set<String> usingCarIds = new HashSet<>();
        for (int from = 0; from < vehicles.size(); from += IN_BATCH_SIZE) {
            List<String> ids = vehicles.subList(from, Math.min(from + IN_BATCH_SIZE, vehicles.size())).stream().map(CarInfo::getId).toList();
            records.findLatestMileages(ids, FieldVehicleStatus.FINISHED.getCode())
                    .forEach(value -> mileages.put(value.carId(), value.endMileage()));
            usingCarIds.addAll(records.findUsingCarIds(ids, FieldVehicleStatus.USING.getCode()));
        }
        return vehicles.stream().map(c -> new FieldVehicleCarOptionVO(c.getId(), c.getCarNo(),
                mileages.get(c.getId()), !mileages.containsKey(c.getId()),
                CAR_BUSY.equals(trim(c.getIsUse())) || usingCarIds.contains(c.getId()) ? CAR_BUSY : CAR_IDLE)).toList();
    }

    @Override
    public FieldVehicleDetailVO saveDraft(FieldVehicleSaveRequest form) {
        UserView user = manager();
        validate(form, Default.class, ValidateGroup.Temp.class);
        return write(() -> {
            FieldVehicleUse record = draftRecord(form.getId(), user);
            CarInfo car = trim(form.getCarId()) == null ? null : cars.findById(form.getCarId().trim())
                    .orElseThrow(() -> new BusinessException("所选车辆不存在"));
            applyForm(record, form, car);
            record.setStatus(FieldVehicleStatus.DRAFT.getCode());
            record.update(user.getId(), user.getUsername());
            records.saveAndFlush(record);
            log.info("保存野外用车草稿 id={}, userId={}", record.getId(), user.getId());
            return detail(record);
        });
    }

    @Override
    public void delete(FieldVehicleDeleteRequest form) {
        UserView user = manager();
        validate(form);
        write(() -> {
            FieldVehicleUse record = lockedRecord(form.getId());
            if (!isStatus(record, FieldVehicleStatus.DRAFT)) {
                throw new BusinessException("只能删除草稿，使用中或已结束的用车记录不能删除");
            }
            if (!Objects.equals(record.getCreateUserid(), user.getId())) {
                throw new BusinessException("只能删除本人创建的草稿");
            }
            if (fuels.existsByMainId(record.getId())) {
                throw new BusinessException("草稿存在加油记录，请先核实数据后再删除");
            }
            records.delete(record);
            records.flush();
            log.info("物理删除野外用车草稿 id={}, userId={}", record.getId(), user.getId());
            return null;
        });
    }

    @Override
    public FieldVehicleDetailVO save(FieldVehicleSaveRequest form) {
        UserView user = manager();
        validate(form, Default.class, ValidateGroup.Apply.class);
        return write(() -> {
            // 先车辆锁再主记录锁，与还车顺序一致；草稿本身不持有车辆占用。
            CarInfo car = lockedCar(form.getCarId().trim());
            FieldVehicleUse record = draftRecord(form.getId(), user);
            requireAvailable(car);
            BigDecimal startMileage = startingMileage(car.getId(), form.getStartMileage());
            applyForm(record, form, car);
            record.setStartMileage(startMileage);
            record.setStatus(FieldVehicleStatus.USING.getCode());
            record.update(user.getId(), user.getUsername());
            changeCarUse(car, CAR_BUSY, user);
            records.saveAndFlush(record);
            log.info("野外用车派出 id={}, carId={}, userId={}", record.getId(), car.getId(), user.getId());
            return detail(record);
        });
    }

    @Override
    public FieldVehicleDetailVO update(FieldVehicleUpdateRequest form) {
        UserView user = manager();
        validate(form, Default.class, ValidateGroup.Apply.class);
        return write(() -> {
            FieldVehicleUse record = lockedUsing(form.getId());
            applyUsage(record, form);
            record.update(user.getId(), user.getUsername());
            records.saveAndFlush(record);
            log.info("修改野外用车资料 id={}, userId={}", record.getId(), user.getId());
            return detail(record);
        });
    }

    @Override
    public FieldVehicleDetailVO updateReturn(FieldVehicleReturnRequest form) {
        UserView user = manager();
        validate(form);
        return write(() -> {
            // 只先读标量车 ID，避免锁前载入实体导致一级缓存中的状态过期。
            String carId = records.findCarId(form.getId()).orElseThrow(() -> new BusinessException("用车记录不存在或尚未正式派车"));
            CarInfo car = lockedCar(carId);
            FieldVehicleUse record = lockedUsing(form.getId());
            checkDateOrder(record.getUseDate(), form.getEndDate(), "结束日期不能早于用车日期");
            if (form.getEndMileage().compareTo(record.getStartMileage()) < 0) {
                throw new BusinessException("结束公里数不能小于起始公里数");
            }
            FieldVehicleFuelSummary summary = refreshFuelTotals(record);
            checkDateOrder(summary.latestDate(), form.getEndDate(), "结束日期不能早于已填写的加油日期");
            record.setEndDate(form.getEndDate());
            record.setEndMileage(form.getEndMileage());
            record.setReturnUserId(user.getId());
            record.setReturnUsername(user.getUsername());
            record.setReturnTime(LocalDateTime.now());
            record.setStatus(FieldVehicleStatus.FINISHED.getCode());
            record.update(user.getId(), user.getUsername());
            changeCarUse(car, CAR_IDLE, user);
            records.saveAndFlush(record);
            log.info("野外用车还车 id={}, userId={}, fuelCount={}", record.getId(), user.getId(), record.getFuelCount());
            return detail(record);
        });
    }

    @Override
    public FieldVehicleFuelVO saveFuel(FieldVehicleFuelSaveRequest form) {
        UserView user = manager();
        validate(form);
        return write(() -> {
            FieldVehicleUse record = lockedUsing(form.getMainId());
            FieldVehicleFuel fuel = new FieldVehicleFuel();
            fuel.setMainId(record.getId());
            fuel.create(user.getId(), user.getUsername());
            applyFuel(fuel, form, record, user);
            fuels.save(fuel);
            refreshFuelTotals(record);
            records.saveAndFlush(record);
            log.info("新增野外加油 id={}, mainId={}, userId={}", fuel.getId(), record.getId(), user.getId());
            return fuelVO(fuel);
        });
    }

    @Override
    public FieldVehicleFuelVO updateFuel(FieldVehicleFuelUpdateRequest form) {
        UserView user = manager();
        validate(form);
        return write(() -> {
            FieldVehicleUse record = lockedUsing(form.getMainId());
            FieldVehicleFuel fuel = ownedFuel(form.getId(), record.getId());
            applyFuel(fuel, form, record, user);
            fuels.save(fuel);
            refreshFuelTotals(record);
            records.saveAndFlush(record);
            log.info("修改野外加油 id={}, mainId={}, userId={}", fuel.getId(), record.getId(), user.getId());
            return fuelVO(fuel);
        });
    }

    @Override
    public void deleteFuel(FieldVehicleFuelDeleteRequest form) {
        UserView user = manager();
        validate(form);
        write(() -> {
            FieldVehicleUse record = lockedUsing(form.getMainId());
            FieldVehicleFuel fuel = ownedFuel(form.getId(), record.getId());
            fuels.delete(fuel);
            record.update(user.getId(), user.getUsername());
            refreshFuelTotals(record);
            records.saveAndFlush(record);
            log.info("删除野外加油 id={}, mainId={}, date={}, amount={}, quantity={}, userId={}",
                    fuel.getId(), record.getId(), fuel.getFuelDate(), fuel.getAmount(), fuel.getFuelQuantity(), user.getId());
            return null;
        });
    }

    /** 事务在回滚边界外转换占用冲突，避免在失败事务内继续写入。 */
    private <T> T write(Supplier<T> action) {
        try {
            return transactions.execute(status -> action.get());
        } catch (DataIntegrityViolationException | ConcurrencyFailureException e) {
            log.error("野外用车事务发生数据或锁冲突", e);
            throw new BusinessException("车辆状态或记录数据已变化，请刷新后重试");
        }
    }

    private FieldVehicleUse draftRecord(String id, UserView user) {
        if (trim(id) == null) {
            FieldVehicleUse record = new FieldVehicleUse();
            record.create(user.getId(), user.getUsername());
            return record;
        }
        FieldVehicleUse record = lockedRecord(id);
        if (!isStatus(record, FieldVehicleStatus.DRAFT)) {
            throw new BusinessException("仅草稿允许保存或正式提交，请勿重复派车");
        }
        return record;
    }

    private FieldVehicleUse lockedRecord(String id) {
        return records.findLockedById(requiredId(id)).orElseThrow(() -> new BusinessException("用车记录不存在"));
    }

    private FieldVehicleUse lockedUsing(String id) {
        FieldVehicleUse record = lockedRecord(id);
        if (!isStatus(record, FieldVehicleStatus.USING)) {
            throw new BusinessException(isStatus(record, FieldVehicleStatus.FINISHED)
                    ? "该用车记录已结束，不允许修改或重复还车" : "仅使用中的用车记录允许此操作");
        }
        return record;
    }

    private CarInfo lockedCar(String id) {
        return cars.findLockedById(id).orElseThrow(() -> new BusinessException("所选车辆不存在"));
    }

    private FieldVehicleFuel ownedFuel(String id, String mainId) {
        return fuels.findByIdAndMainId(id, mainId).orElseThrow(() -> new BusinessException("加油记录不存在或不属于该用车记录"));
    }

    private void requireAvailable(CarInfo car) {
        if (!CAR_ENABLED.equals(trim(car.getIsAvtive()))) {
            throw new BusinessException("所选车辆未启用，不能派车");
        }
        String use = trim(car.getIsUse());
        if (CAR_BUSY.equals(use) || records.existsByCarIdAndStatus(car.getId(), FieldVehicleStatus.USING.getCode())) {
            throw new BusinessException("所选车辆正在使用，不能重复派车");
        }
        if (use != null && !CAR_IDLE.equals(use)) {
            throw new BusinessException("车辆占用状态异常，请先检查车辆档案");
        }
    }

    private BigDecimal startingMileage(String carId, BigDecimal supplied) {
        List<FieldVehicleUse> previous = records.findLatestEnded(carId, FieldVehicleStatus.FINISHED.getCode(), PageRequest.of(0, 1));
        if (previous.isEmpty()) {
            if (supplied == null) {
                throw new BusinessException("首次派车必须填写起始表显公里数");
            }
            return supplied;
        }
        BigDecimal latest = previous.get(0).getEndMileage();
        if (supplied != null && supplied.compareTo(latest) != 0) {
            throw new BusinessException("车辆起始公里数已变化，请刷新后重试");
        }
        return latest;
    }

    private void applyForm(FieldVehicleUse record, FieldVehicleSaveRequest form, CarInfo car) {
        checkDateOrder(form.getUseDate(), form.getExpectedReturnDate(), "预计返回日期不能早于用车日期");
        applyUsage(record, form);
        record.setCarId(car == null ? null : car.getId());
        record.setCarNo(car == null ? null : car.getCarNo());
        record.setUseDate(form.getUseDate());
        record.setExpectedReturnDate(form.getExpectedReturnDate());
        record.setStartMileage(form.getStartMileage());
        record.setRemark(trim(form.getRemark()));
    }

    private void applyUsage(FieldVehicleUse record, FieldVehicleUsageRequest form) {
        record.setDrivers(form.getDrivers());
        record.setPeers(form.getPeers());
        record.setPurpose(trim(form.getPurpose()));
        record.setDeparturePlace(trim(form.getDeparturePlace()));
        record.setDestination(trim(form.getDestination()));
    }

    private void applyFuel(FieldVehicleFuel fuel, FieldVehicleFuelSaveRequest form, FieldVehicleUse record, UserView user) {
        checkDateOrder(record.getUseDate(), form.getFuelDate(), "加油日期不能早于用车日期");
        fuel.setFuelDate(form.getFuelDate());
        fuel.setAmount(form.getAmount());
        fuel.setFuelQuantity(form.getFuelQuantity());
        fuel.setRemark(trim(form.getRemark()));
        fuel.update(user.getId(), user.getUsername());
        record.update(user.getId(), user.getUsername());
    }

    /** 持有主记录锁时先落明细再聚合，确保删除、并发新增及还车均使用最新有效记录。 */
    private FieldVehicleFuelSummary refreshFuelTotals(FieldVehicleUse record) {
        fuels.flush();
        FieldVehicleFuelSummary summary = fuels.findSummary(record.getId());
        record.setFuelTotalQuantity(quantity(summary));
        record.setFuelTotal(amount(summary));
        record.setFuelCount(count(summary));
        return summary;
    }

    private void changeCarUse(CarInfo car, String state, UserView user) {
        car.setIsUse(state);
        car.setOperator(user.getId());
        car.setOperatedate(LocalDateTime.now());
        cars.save(car);
    }

    private FieldVehicleDetailVO detail(FieldVehicleUse record) {
        List<FieldVehicleFuel> items = fuels.findItems(record.getId());
        FieldVehicleDetailVO vo = new FieldVehicleDetailVO();
        BeanUtils.copyProperties(listVO(record), vo);
        vo.setFuelItems(items.stream().map(this::fuelVO).toList());
        return vo;
    }

    private FieldVehicleListVO listVO(FieldVehicleUse record) {
        FieldVehicleListVO vo = new FieldVehicleListVO();
        BeanUtils.copyProperties(record, vo);
        for (FieldVehicleStatus value : FieldVehicleStatus.values()) {
            if (isStatus(record, value)) {
                vo.setStatusName(value.getLabel());
            }
        }
        if (isStatus(record, FieldVehicleStatus.FINISHED)) {
            vo.setUsedMileage(record.getEndMileage().subtract(record.getStartMileage()));
        }
        return vo;
    }

    private FieldVehicleFuelVO fuelVO(FieldVehicleFuel fuel) {
        FieldVehicleFuelVO vo = new FieldVehicleFuelVO();
        BeanUtils.copyProperties(fuel, vo);
        return vo;
    }

    private Specification<FieldVehicleUse> specification(FieldVehicleRequestForm form, LocalDate start, LocalDate end) {
        String query = trim(form.getQuery());
        String driver = trim(form.getDriverName());
        String peer = trim(form.getPeerName());
        return (root, criteria, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (trim(form.getCarId()) != null) predicates.add(cb.equal(root.get("carId"), form.getCarId().trim()));
            if (form.getStatus() != null) predicates.add(cb.equal(root.get("status"), form.getStatus()));
            if (start != null) predicates.add(cb.greaterThanOrEqualTo(root.get("useDate"), start));
            if (end != null) predicates.add(cb.lessThanOrEqualTo(root.get("useDate"), end));
            if (driver != null) predicates.add(cb.like(root.get("drivers"), containsPattern(driver), '!'));
            if (peer != null) predicates.add(cb.like(root.get("peers"), containsPattern(peer), '!'));
            if (query != null) {
                String pattern = containsPattern(query);
                predicates.add(cb.or(cb.like(root.get("carNo"), pattern, '!'), cb.like(root.get("purpose"), pattern, '!'),
                        cb.like(root.get("departurePlace"), pattern, '!'), cb.like(root.get("destination"), pattern, '!')));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private String containsPattern(String text) {
        return "%" + text.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![") + "%";
    }

    private UserView currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserView user) || StringUtils.isBlank(user.getId())) {
            throw new AuthenticationCredentialsNotFoundException("请先登录系统");
        }
        return user;
    }

    private UserView manager() {
        UserView user = currentUser();
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .noneMatch(role -> MANAGER_AUTHORITY.equals(role.getAuthority()))) {
            throw new AccessDeniedException("无野外用车负责人权限");
        }
        return user;
    }

    private void validate(Object form, Class<?>... groups) {
        if (form == null) throw new BusinessException("请求参数不能为空");
        String errors = validator.validate(form, groups).stream().map(v -> v.getMessage()).distinct().sorted().collect(Collectors.joining("；"));
        if (!errors.isEmpty()) throw new BusinessException(errors);
    }

    private static boolean isStatus(FieldVehicleUse record, FieldVehicleStatus status) {
        return Objects.equals(record.getStatus(), status.getCode());
    }

    private static void validateStatus(Integer status) {
        if (status != null && Arrays.stream(FieldVehicleStatus.values()).noneMatch(s -> s.getCode() == status)) {
            throw new BusinessException("用车状态无效");
        }
    }

    private static LocalDate parseDate(String date) {
        try {
            return trim(date) == null ? null : LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException("查询日期格式必须为yyyy-MM-dd");
        }
    }

    private static void checkDateOrder(LocalDate start, LocalDate end, String message) {
        if (start != null && end != null && end.isBefore(start)) throw new BusinessException(message);
    }

    private static String requiredId(String id) {
        if (trim(id) == null || id.length() > 50) throw new BusinessException("用车记录标识不能为空且不能超过50个字符");
        return id.trim();
    }

    private static String trim(String value) {
        return StringUtils.trimToNull(value);
    }

    private static BigDecimal quantity(FieldVehicleFuelSummary summary) {
        return total(summary.totalQuantity(), 3);
    }

    private static BigDecimal amount(FieldVehicleFuelSummary summary) {
        return total(summary.totalAmount(), 2);
    }

    private static BigDecimal total(BigDecimal value, int scale) {
        BigDecimal result = value == null ? BigDecimal.ZERO.setScale(scale) : value.setScale(scale, RoundingMode.UNNECESSARY);
        if (result.signum() < 0 || result.precision() > 38) throw new BusinessException("加油汇总超出允许范围");
        return result;
    }

    private static int count(FieldVehicleFuelSummary summary) {
        long value = summary.count() == null ? 0 : summary.count();
        if (value < 0 || value > Integer.MAX_VALUE) throw new BusinessException("加油次数超出允许范围");
        return (int) value;
    }
}
