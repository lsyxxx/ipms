package com.abt.oa.service.impl;

import com.abt.oa.entity.*;
import com.abt.oa.enums.FieldVehicleStatus;
import com.abt.oa.model.FieldVehicleWeekReportVO;
import com.abt.sys.exception.BusinessException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** 周报业务口径测试，固定查询时点覆盖跨周、加油归属和缺失里程。 */
class FieldVehicleWeekReportBuilderTest {
    private final LocalDate start = LocalDate.of(2026, 11, 2);
    private final LocalDate end = start.plusDays(6);
    private final LocalDateTime now = LocalDateTime.of(2026, 11, 5, 14, 30);

    @Test
    void crossWeekUnionAndFuelDatesUseDifferentScopes() {
        FieldVehicleUse current = record("U", "A", start.minusDays(7), null, "100", null);
        current.setExpectedReturnDate(end.plusDays(2));
        FieldVehicleUse cross = record("C", "B", start.minusDays(1), start.plusDays(2), "100", "300");
        FieldVehicleUse dispatched = record("D", "A", start, start.plusDays(1), "300", "400");
        FieldVehicleUse old = record("OLD", "A", start.minusDays(10), start.minusDays(8), "0", "100");
        List<FieldVehicleFuel> fuel = List.of(
                fuel("F1", "U", start, "10.125", "100.11"),
                fuel("F2", "U", end, "20.125", "200.22"),
                fuel("F3", "U", end.plusDays(1), "30", "300"),
                fuel("F4", "U", null, "40", "400"),
                fuel("F5", "C", start.minusDays(1), "5", "50"),
                fuel("F6", "C", start, "15", "150"),
                fuel("F7", "C", null, "10", "100"),
                fuel("F8", "OLD", start.minusDays(9), "2", "20"));
        FieldVehicleWeekReportVO report = FieldVehicleWeekReportBuilder.build(List.of(current, dispatched, cross, old), fuel, start, end, now);
        var summary = report.getSummary();
        assertEquals(3, summary.getDispatchCount());
        assertEquals(1, summary.getOutCarCount());
        assertEquals(1, summary.getWeekCarCount()); // 只有跨周还车的 B 不单独增加本周用车。
        assertEquals(2, summary.getEndedCount());
        assertEquals(3, summary.getWeekFuelCount());
        assertEquals(2, summary.getPendingFuelCount());
        decimal("45.250", summary.getWeekFuelQuantity());
        decimal("450.33", summary.getWeekFuelAmount());
        decimal("300", summary.getUsedMileage());
        decimal("30", summary.getConsumptionFuelQuantity());
        decimal("200", summary.getConsumptionMileage());
        decimal("15.0", summary.getFuelConsumption());
        assertEquals(1, summary.getConsumptionTripCount());
        var trip = report.getTrips().get(0);
        assertTrue(trip.isCrossWeek());
        assertNull(trip.getUsedMileage());
        assertEquals(4, trip.getFuelItems().size());
        decimal("100.250", trip.getFuelTotalQuantity());
        decimal("30.250", trip.getWeekFuelQuantity());
        var row = report.getDriveRows().get(0);
        decimal("200", row.getTotalMileage());
        decimal("320.33", row.getTotalAmount());
        assertNull(row.getWeekStartMileage()); assertNull(row.getWeekEndMileage()); assertNull(row.getDept());
        assertEquals(List.of("F1", "F6", "F2"), report.getWeekFuelItems().stream().map(f -> f.getId()).toList());
        assertEquals(2, report.getResources().size());
        var actual = report.getEvents().stream().filter(e -> e.getId().equals("U-using")).findFirst().orElseThrow();
        assertEquals(start, actual.getStart());
        assertEquals(now.toLocalDate().plusDays(1), actual.getEnd());
        var expected = report.getEvents().stream().filter(e -> e.getId().equals("U-expect")).findFirst().orElseThrow();
        assertEquals(now.toLocalDate().plusDays(1), expected.getStart());
        assertEquals(end.plusDays(1), expected.getEnd());
        assertTrue(expected.getExtendedProps().isCrossWeek());
    }

    @Test
    void invalidAndZeroMileagesDoNotCreateConsumption() {
        var missing = record("M", "A", start, start.plusDays(1), null, "10");
        var negative = record("N", "A", start, start.plusDays(1), "20", "10");
        var zero = record("Z", "A", start, start.plusDays(1), "10", "10");
        var validNoFuel = record("V", "A", start, start.plusDays(1), "10", "20");
        var report = FieldVehicleWeekReportBuilder.build(List.of(missing, negative, zero, validNoFuel),
                List.of(fuel("F", "Z", start, "1", "1")), start, end, now);
        assertEquals(2, report.getSummary().getAbnormalMileageCount());
        decimal("10", report.getSummary().getUsedMileage());
        assertNull(report.getSummary().getFuelConsumption());
        assertEquals(0, report.getSummary().getConsumptionTripCount());
        assertNull(report.getTrips().get(0).getUsedMileage());
        assertTrue(report.getTrips().get(2).isMileageValid());
    }

    @Test
    void calendarHandlesOverdueMissingEstimateFutureAndHistoricalWeek() {
        var overdue = record("O", "A", start.minusDays(1), null, "0", null);
        overdue.setExpectedReturnDate(start.plusDays(1));
        var missing = record("M", "B", start, null, "0", null);
        var future = record("F", "C", end, null, "0", null);
        future.setExpectedReturnDate(end.plusDays(1));
        var report = FieldVehicleWeekReportBuilder.build(List.of(overdue, missing, future), List.of(), start, end, now);
        assertEquals(3, report.getEvents().size());
        assertEquals(List.of("fv-expect"), report.getEvents().get(2).getClassNames());
        assertTrue(report.getEvents().stream().filter(e -> e.getClassNames().contains("fv-using"))
                .allMatch(e -> e.getEnd().equals(now.toLocalDate().plusDays(1))));
        var historical = FieldVehicleWeekReportBuilder.build(List.of(overdue), List.of(), start, end, now.plusWeeks(1));
        assertEquals(1, historical.getSummary().getOutCarCount());
        assertEquals(end.plusDays(1), historical.getEvents().get(0).getEnd());
        assertEquals(1, historical.getEvents().size());
    }

    @Test
    void inconsistentStatusAndIllegalFuelAreReportedAsErrors() {
        var invalid = record("I", "A", start, null, "0", null);
        invalid.setEndDate(start);
        assertThrows(BusinessException.class, () -> FieldVehicleWeekReportBuilder.inReport(invalid, start));
        var record = record("V", "A", start, start.plusDays(1), "0", "10");
        assertThrows(BusinessException.class, () -> FieldVehicleWeekReportBuilder.build(List.of(record),
                List.of(fuel("F", "V", start.minusDays(1), "1", "1")), start, end, now));
        var report = FieldVehicleWeekReportBuilder.build(List.of(), List.of(), start, end, now);
        assertEquals(0, report.getSummary().getDispatchCount());
        decimal("0", report.getSummary().getWeekFuelQuantity());
        assertNull(report.getSummary().getFuelConsumption());
        assertTrue(report.getTrips().isEmpty()); assertTrue(report.getResources().isEmpty());
    }

    private static FieldVehicleUse record(String id, String car, LocalDate use, LocalDate finish, String from, String to) {
        var record = new FieldVehicleUse();
        record.setId(id); record.setCarId(car); record.setCarNo(car); record.setUseDate(use); record.setEndDate(finish);
        record.setStatus(finish == null ? FieldVehicleStatus.USING.getCode() : FieldVehicleStatus.FINISHED.getCode());
        record.setStartMileage(from == null ? null : new BigDecimal(from));
        record.setEndMileage(to == null ? null : new BigDecimal(to));
        return record;
    }

    private static FieldVehicleFuel fuel(String id, String trip, LocalDate date, String quantity, String amount) {
        var fuel = new FieldVehicleFuel();
        fuel.setId(id); fuel.setMainId(trip); fuel.setFuelDate(date);
        fuel.setFuelQuantity(new BigDecimal(quantity)); fuel.setAmount(new BigDecimal(amount));
        return fuel;
    }

    private static void decimal(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
