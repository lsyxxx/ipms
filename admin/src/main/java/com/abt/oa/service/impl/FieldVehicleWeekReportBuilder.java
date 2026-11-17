package com.abt.oa.service.impl;

import com.abt.oa.entity.FieldVehicleFuel;
import com.abt.oa.entity.FieldVehicleUse;
import com.abt.oa.enums.FieldVehicleStatus;
import com.abt.oa.model.FieldVehicleWeekReportVO;
import com.abt.oa.model.FieldVehicleWeekReportVO.*;
import com.abt.sys.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** 周报内存聚合：按行程组装明细、日历和同口径汇总，不访问数据库。 */
final class FieldVehicleWeekReportBuilder {
    private FieldVehicleWeekReportBuilder() { }

    /** 输入已限定正式状态、用车日期不晚于周末，返回 D、U、C 并集。 */
    static boolean inReport(FieldVehicleUse record, LocalDate start) {
        validateRecord(record);
        return !record.getUseDate().isBefore(start) || using(record)
                || !record.getEndDate().isBefore(start);
    }

    /** 历史集合用于累计，周报集合用于卡片、明细、日历和周内加油。 */
    static FieldVehicleWeekReportVO build(List<FieldVehicleUse> history, List<FieldVehicleFuel> items,
                                          LocalDate start, LocalDate end, LocalDateTime queriedAt) {
        FieldVehicleWeekReportVO report = new FieldVehicleWeekReportVO();
        report.setWeekStart(start);
        report.setWeekEnd(end);
        report.setQueriedAt(queriedAt);
        report.setQueryDate(queriedAt.toLocalDate());
        history.forEach(FieldVehicleWeekReportBuilder::validateRecord);
        Map<String, List<FieldVehicleFuel>> byTrip = items.stream().collect(Collectors.groupingBy(FieldVehicleFuel::getMainId));
        Map<String, List<FieldVehicleUse>> historyByCar = history.stream().collect(Collectors.groupingBy(FieldVehicleUse::getCarId));
        Set<String> outCars = new HashSet<>();
        Set<String> weekCars = new HashSet<>();
        Map<String, List<Trip>> byCar = new TreeMap<>();
        for (FieldVehicleUse record : history) {
            if (!inReport(record, start)) continue;
            Trip trip = trip(record, byTrip.getOrDefault(record.getId(), List.of()), report);
            report.getTrips().add(trip);
            byCar.computeIfAbsent(record.getCarId(), key -> new ArrayList<>()).add(trip);
            if (using(record)) outCars.add(record.getCarId());
            if (using(record) || !record.getUseDate().isBefore(start)) weekCars.add(record.getCarId());
            calendar(trip, report);
        }
        Summary summary = report.getSummary();
        summary.setOutCarCount(outCars.size());
        summary.setWeekCarCount(weekCars.size());
        summary.setDispatchCount(report.getTrips().size());
        summary.setWeekFuelCount(report.getWeekFuelItems().size());
        summary.setPendingFuelCount(report.getPendingFuelItems().size());
        summary.setFuelConsumption(consumption(summary.getConsumptionFuelQuantity(), summary.getConsumptionMileage()));
        Comparator<FuelItem> fuelOrder = Comparator.comparing(FuelItem::getFuelDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(FuelItem::getId);
        report.getWeekFuelItems().sort(fuelOrder);
        report.getPendingFuelItems().sort(fuelOrder);
        List<List<Trip>> vehicles = new ArrayList<>(byCar.values());
        vehicles.sort(Comparator.comparing(trips -> Objects.toString(trips.get(0).getCarNo(), "")));
        for (List<Trip> trips : vehicles) {
            DriveRow row = driveRow(trips, historyByCar.get(trips.get(0).getCarId()), byTrip, end, report.getDriveRows().size() + 1);
            report.getDriveRows().add(row);
            Resource resource = new Resource();
            resource.setId(row.getCarId()); resource.setTitle(row.getCarNo()); resource.setOrder(row.getSeq());
            report.getResources().add(resource);
        }
        return report;
    }

    /** 明细重算整次累计，不将不同周及未填日期的加油混入周内小计。 */
    private static Trip trip(FieldVehicleUse record, List<FieldVehicleFuel> items, FieldVehicleWeekReportVO report) {
        Trip trip = new Trip();
        BeanUtils.copyProperties(record, trip);
        trip.setStatusName(using(record) ? FieldVehicleStatus.USING.getLabel() : FieldVehicleStatus.FINISHED.getLabel());
        LocalDate finish = using(record) ? record.getExpectedReturnDate() : record.getEndDate();
        trip.setCrossWeek(record.getUseDate().isBefore(report.getWeekStart())
                || (finish != null && finish.isAfter(report.getWeekEnd()))
                || (using(record) && report.getQueryDate().isAfter(report.getWeekEnd())));
        trip.setMileageValid(validMileage(record));
        trip.setUsedMileage(trip.isMileageValid() ? record.getEndMileage().subtract(record.getStartMileage()) : null);
        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal amount = BigDecimal.ZERO;
        for (FieldVehicleFuel fuel : items) {
            validateFuel(fuel, record);
            FuelItem item = new FuelItem();
            BeanUtils.copyProperties(fuel, item);
            item.setCarNo(record.getCarNo()); item.setPurpose(record.getPurpose());
            boolean inWeek = fuel.getFuelDate() != null && !fuel.getFuelDate().isBefore(report.getWeekStart())
                    && !fuel.getFuelDate().isAfter(report.getWeekEnd());
            item.setFuelScope(fuel.getFuelDate() == null ? "待确认日期" : inWeek ? "本周" : "其他周");
            trip.getFuelItems().add(item);
            quantity = quantity.add(fuel.getFuelQuantity()); amount = amount.add(fuel.getAmount());
            if (inWeek) {
                trip.setWeekFuelQuantity(trip.getWeekFuelQuantity().add(fuel.getFuelQuantity()));
                trip.setWeekFuelAmount(trip.getWeekFuelAmount().add(fuel.getAmount()));
                report.getWeekFuelItems().add(item);
            } else if (fuel.getFuelDate() == null) report.getPendingFuelItems().add(item);
        }
        trip.setFuelTotalQuantity(quantity); trip.setFuelTotal(amount); trip.setFuelCount(items.size());
        Summary summary = report.getSummary();
        summary.setWeekFuelQuantity(summary.getWeekFuelQuantity().add(trip.getWeekFuelQuantity()));
        summary.setWeekFuelAmount(summary.getWeekFuelAmount().add(trip.getWeekFuelAmount()));
        if (using(record)) summary.setUsingCount(summary.getUsingCount() + 1);
        else {
            summary.setEndedCount(summary.getEndedCount() + 1);
            if (!trip.isMileageValid()) summary.setAbnormalMileageCount(summary.getAbnormalMileageCount() + 1);
            else {
                summary.setUsedMileage(summary.getUsedMileage().add(trip.getUsedMileage()));
                if (trip.getUsedMileage().signum() > 0 && !items.isEmpty()) {
                    summary.setConsumptionMileage(summary.getConsumptionMileage().add(trip.getUsedMileage()));
                    summary.setConsumptionFuelQuantity(summary.getConsumptionFuelQuantity().add(quantity));
                    summary.setConsumptionTripCount(summary.getConsumptionTripCount() + 1);
                }
            }
        }
        return trip;
    }

    /** 实际行程不画到未来；预计段从查询日次日开始，日期结束值统一转为排他边界。 */
    private static void calendar(Trip trip, FieldVehicleWeekReportVO report) {
        boolean using = Objects.equals(trip.getStatus(), FieldVehicleStatus.USING.getCode());
        LocalDate actualEnd = using ? report.getQueryDate() : earlier(trip.getEndDate(), report.getQueryDate());
        event(trip, trip.getUseDate(), actualEnd, using ? "using" : "done", report);
        if (using && trip.getExpectedReturnDate() != null) {
            event(trip, later(trip.getUseDate(), report.getQueryDate().plusDays(1)), trip.getExpectedReturnDate(), "expect", report);
        }
    }

    private static void event(Trip trip, LocalDate start, LocalDate end, String line, FieldVehicleWeekReportVO report) {
        start = later(start, report.getWeekStart()); end = earlier(end, report.getWeekEnd());
        if (end.isBefore(start)) return;
        Event event = new Event();
        event.setId(trip.getId() + "-" + line); event.setResourceId(trip.getCarId());
        event.setTitle(trip.getPurpose()); event.setStart(start); event.setEnd(end.plusDays(1));
        event.setClassNames(List.of("fv-" + line));
        EventProps props = new EventProps(); BeanUtils.copyProperties(trip, props);
        props.setTripId(trip.getId()); props.setLine("expect".equals(line) ? "预计用车" : "实际用车"); props.setCrossWeek(trip.isCrossWeek());
        event.setExtendedProps(props); report.getEvents().add(event);
    }

    /** 累计只计截至周末已结束的有效行程里程、以及有明确日期且不晚于周末的加油金额。 */
    private static DriveRow driveRow(List<Trip> trips, List<FieldVehicleUse> history,
                                     Map<String, List<FieldVehicleFuel>> byTrip, LocalDate end, int seq) {
        DriveRow row = new DriveRow();
        row.setCarId(trips.get(0).getCarId()); row.setCarNo(trips.get(0).getCarNo()); row.setSeq(seq);
        row.setRemark("周始/周末公里数、部门暂无数据；里程按完整行程统计；累计金额不含待确认日期加油");
        BigDecimal consumptionQuantity = BigDecimal.ZERO;
        BigDecimal consumptionMileage = BigDecimal.ZERO;
        for (Trip trip : trips) {
            if (trip.isMileageValid()) {
                row.setUsedMileage(row.getUsedMileage().add(trip.getUsedMileage()));
                if (trip.getUsedMileage().signum() > 0 && trip.getFuelCount() > 0) {
                    consumptionQuantity = consumptionQuantity.add(trip.getFuelTotalQuantity());
                    consumptionMileage = consumptionMileage.add(trip.getUsedMileage());
                }
            }
            row.setFuelQuantity(row.getFuelQuantity().add(trip.getWeekFuelQuantity()));
            row.setFuelAmount(row.getFuelAmount().add(trip.getWeekFuelAmount()));
            trip.getFuelItems().stream().filter(f -> f.getFuelDate() != null && !f.getFuelDate().isBefore(end.minusDays(6))
                    && !f.getFuelDate().isAfter(end)).forEach(row.getFuelItems()::add);
        }
        row.getFuelItems().sort(Comparator.comparing(FuelItem::getFuelDate).thenComparing(FuelItem::getId));
        row.setFuelConsumption(consumption(consumptionQuantity, consumptionMileage));
        for (FieldVehicleUse record : history) {
            if (!Objects.equals(record.getCarId(), row.getCarId())) continue;
            if (validMileage(record) && !record.getEndDate().isAfter(end))
                row.setTotalMileage(row.getTotalMileage().add(record.getEndMileage().subtract(record.getStartMileage())));
            for (FieldVehicleFuel fuel : byTrip.getOrDefault(record.getId(), List.of())) {
                validateFuel(fuel, record);
                if (fuel.getFuelDate() != null && !fuel.getFuelDate().isAfter(end))
                    row.setTotalAmount(row.getTotalAmount().add(fuel.getAmount()));
            }
        }
        return row;
    }

    private static boolean using(FieldVehicleUse record) {
        return Objects.equals(record.getStatus(), FieldVehicleStatus.USING.getCode());
    }

    private static boolean validMileage(FieldVehicleUse record) {
        return Objects.equals(record.getStatus(), FieldVehicleStatus.FINISHED.getCode())
                && record.getStartMileage() != null && record.getEndMileage() != null
                && record.getStartMileage().signum() >= 0 && record.getEndMileage().compareTo(record.getStartMileage()) >= 0;
    }

    private static void validateRecord(FieldVehicleUse record) {
        if (StringUtils.isBlank(record.getCarId()) || record.getUseDate() == null
                || (using(record) && record.getEndDate() != null)
                || (!using(record) && (record.getEndDate() == null || record.getEndDate().isBefore(record.getUseDate()))))
            throw new BusinessException("用车记录日期或状态异常，请核实数据后查询周报，记录标识：" + record.getId());
    }

    private static void validateFuel(FieldVehicleFuel fuel, FieldVehicleUse record) {
        if (fuel.getFuelQuantity() == null || fuel.getFuelQuantity().signum() <= 0
                || fuel.getAmount() == null || fuel.getAmount().signum() < 0
                || (fuel.getFuelDate() != null && (fuel.getFuelDate().isBefore(record.getUseDate())
                || (!using(record) && fuel.getFuelDate().isAfter(record.getEndDate())))))
            throw new BusinessException("加油明细数据异常，请核实后查询周报，记录标识：" + fuel.getId());
    }

    private static BigDecimal consumption(BigDecimal quantity, BigDecimal mileage) {
        return mileage.signum() > 0 ? quantity.multiply(BigDecimal.valueOf(100)).divide(mileage, 1, RoundingMode.HALF_UP) : null;
    }

    private static LocalDate earlier(LocalDate a, LocalDate b) { return a.isBefore(b) ? a : b; }
    private static LocalDate later(LocalDate a, LocalDate b) { return a.isAfter(b) ? a : b; }
}
