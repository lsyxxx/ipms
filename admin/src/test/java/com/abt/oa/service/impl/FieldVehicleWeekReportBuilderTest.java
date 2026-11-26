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
        fuel.get(4).setMileage(new BigDecimal("100"));
        FieldVehicleWeekReportVO report = FieldVehicleWeekReportBuilder.build(List.of(current, dispatched, cross, old), fuel, start, end, now);
        var summary = report.getSummary();
        assertEquals(3, summary.getDispatchCount());
        assertEquals(1, summary.getOutCarCount());
        assertEquals(1, summary.getWeekCarCount()); // 只有跨周还车的 B 不单独增加本周用车。
        assertEquals(2, summary.getEndedCount());
        assertEquals(2, summary.getWeekFuelCount());
        assertEquals(2, summary.getPendingFuelCount());
        decimal("35.125", summary.getWeekFuelQuantity());
        decimal("350.22", summary.getWeekFuelAmount());
        decimal("300", summary.getUsedMileage());
        decimal("15", summary.getConsumptionFuelQuantity());
        decimal("200", summary.getConsumptionMileage());
        decimal("7.5", summary.getFuelConsumption());
        assertEquals(1, summary.getConsumptionTripCount());
        var trip = report.getTrips().get(0);
        assertTrue(trip.isCrossWeek());
        assertNull(trip.getUsedMileage());
        assertEquals(4, trip.getFuelItems().size());
        decimal("100.250", trip.getFuelTotalQuantity());
        decimal("20.125", trip.getWeekFuelQuantity());
        var row = report.getDriveRows().get(0);
        decimal("200", row.getTotalMileage());
        decimal("320.33", row.getTotalAmount());
        assertNull(row.getWeekStartMileage()); decimal("400", row.getWeekEndMileage()); assertNull(row.getDept());
        assertEquals(List.of("F6", "F2"), report.getWeekFuelItems().stream().map(f -> f.getId()).toList());
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

    @Test
    void unfinishedTripUsesLatestFuelOnOrBeforeCutoffAndPreservesFullDetails() {
        var running = record("U", "A", start.minusDays(2), null, "1000", null);
        var previous = fuel("PREVIOUS", "U", start.minusDays(1), "20", "160");
        previous.setMileage(new BigDecimal("1200"));
        var latest = fuel("LATEST", "U", end.minusDays(1), "40", "320");
        latest.setMileage(new BigDecimal("1600"));
        var earlier = fuel("EARLIER", "U", start, "30", "240");
        earlier.setMileage(new BigDecimal("1300"));
        var future = fuel("FUTURE", "U", end.plusDays(1), "50", "400");
        future.setMileage(new BigDecimal("1900"));
        var pending = fuel("PENDING", "U", null, "60", "480");
        pending.setMileage(new BigDecimal("2000"));
        var report = FieldVehicleWeekReportBuilder.build(List.of(running), List.of(future, pending, latest, earlier, previous), start, end, now);
        var trip = report.getTrips().get(0);
        decimal("400", trip.getUsedMileage());
        decimal("1200", trip.getStatisticsStartMileage());
        assertEquals("PREVIOUS", trip.getStatisticsStartFuelId());
        assertEquals(start.minusDays(1), trip.getStatisticsStartDate());
        decimal("40", trip.getWeekFuelQuantity());
        decimal("200", trip.getFuelTotalQuantity());
        assertEquals(5, trip.getFuelCount());
        assertEquals("LATEST", trip.getStatisticsFuelId());
        assertEquals(end.minusDays(1), trip.getStatisticsEndDate());
        assertNull(trip.getEndMileage());
        decimal("400", report.getSummary().getUsedMileage());
        decimal("40", report.getSummary().getConsumptionFuelQuantity());
        decimal("10.0", report.getSummary().getFuelConsumption());
        assertEquals(List.of("LATEST"), report.getWeekFuelItems().stream().map(f -> f.getId()).toList());
        var row = report.getDriveRows().get(0);
        decimal("400", row.getUsedMileage()); decimal("600", row.getTotalMileage());
        decimal("1200", row.getWeekStartMileage()); decimal("1600", row.getWeekEndMileage());
        decimal("40", row.getFuelQuantity()); decimal("320", row.getFuelAmount());
        decimal("720", row.getTotalAmount()); decimal("10.0", row.getFuelConsumption());
        assertEquals(1, row.getFuelItems().size());
        assertEquals(1, report.getSummary().getPendingFuelCount());

        // 当前已还车也不得将截止日之后的最终里程带回历史周。
        running.setStatus(FieldVehicleStatus.FINISHED.getCode());
        running.setEndDate(end.plusDays(2)); running.setEndMileage(new BigDecimal("2200"));
        var historical = FieldVehicleWeekReportBuilder.build(List.of(running), List.of(future, pending, latest, earlier, previous),
                start, end, now.plusMonths(1));
        decimal("400", historical.getSummary().getUsedMileage());
        decimal("40", historical.getSummary().getWeekFuelQuantity());
        decimal("10.0", historical.getSummary().getFuelConsumption());
        decimal("600", historical.getDriveRows().get(0).getTotalMileage());
        decimal("2200", historical.getTrips().get(0).getEndMileage());
        assertEquals("LATEST", historical.getTrips().get(0).getStatisticsFuelId());
    }

    @Test
    void returnOnCutoffUsesActualMileageAndAllWeekFuel() {
        var finished = record("D", "A", start.minusDays(2), end, "100", "500");
        var previous = fuel("P", "D", start.minusDays(1), "20", "160");
        previous.setMileage(new BigDecimal("150"));
        var first = fuel("F1", "D", start, "10", "80");
        first.setMileage(new BigDecimal("200"));
        var last = fuel("F2", "D", end, "20", "160");
        last.setMileage(new BigDecimal("400"));
        var report = FieldVehicleWeekReportBuilder.build(List.of(finished), List.of(first, last, previous), start, end, now.plusMonths(1));
        decimal("350", report.getSummary().getUsedMileage());
        decimal("150", report.getDriveRows().get(0).getWeekStartMileage());
        decimal("500", report.getDriveRows().get(0).getWeekEndMileage());
        decimal("30", report.getSummary().getWeekFuelQuantity());
        decimal("30", report.getSummary().getConsumptionFuelQuantity());
        assertNull(report.getTrips().get(0).getStatisticsFuelId());
    }

    @Test
    void cutoffDoesNotFallBackToOlderMileageWhenLatestFuelHasNoReading() {
        var finishedLater = record("U", "A", start, end.plusDays(2), "100", "900");
        var older = fuel("OLD", "U", start, "10", "80");
        older.setMileage(new BigDecimal("300"));
        var latest = fuel("LAST", "U", end, "20", "160");
        var report = FieldVehicleWeekReportBuilder.build(List.of(finishedLater), List.of(older, latest), start, end, now.plusMonths(1));
        assertNull(report.getTrips().get(0).getUsedMileage());
        decimal("0", report.getSummary().getUsedMileage());
        decimal("20", report.getSummary().getWeekFuelQuantity());
        assertNull(report.getSummary().getFuelConsumption());
        decimal("0", report.getDriveRows().get(0).getTotalMileage());

        latest.setMileage(new BigDecimal("90"));
        var invalid = FieldVehicleWeekReportBuilder.build(List.of(finishedLater), List.of(older, latest), start, end, now);
        assertNull(invalid.getTrips().get(0).getUsedMileage());
        assertEquals(1, invalid.getSummary().getAbnormalMileageCount());
    }

    @Test
    void sameDayFuelUsesCreationTimeThenIdAndCutoffIsInclusive() {
        var running = record("U", "A", start, null, "100", null);
        var first = fuel("Z", "U", end, "10", "80");
        first.setCreateDate(end.atTime(9, 0)); first.setMileage(new BigDecimal("200"));
        var second = fuel("B", "U", end, "20", "160");
        second.setCreateDate(end.atTime(10, 0)); second.setMileage(new BigDecimal("400"));
        var tie = fuel("A", "U", end, "30", "240");
        tie.setCreateDate(second.getCreateDate()); tie.setMileage(new BigDecimal("300"));
        var report = FieldVehicleWeekReportBuilder.build(List.of(running), List.of(first, second, tie), start, end, now);
        assertEquals("B", report.getTrips().get(0).getStatisticsFuelId());
        decimal("300", report.getSummary().getUsedMileage());
        decimal("20", report.getSummary().getWeekFuelQuantity());
    }

    @Test
    void noDatedFuelBeforeCutoffLeavesMileageUnknownAndDoesNotUseFutureOrUndatedFuel() {
        var running = record("U", "A", start.minusDays(2), null, "100", null);
        var future = fuel("F", "U", end.plusDays(1), "10", "80");
        future.setMileage(new BigDecimal("500"));
        var pending = fuel("P", "U", null, "20", "160");
        pending.setMileage(new BigDecimal("700"));
        var report = FieldVehicleWeekReportBuilder.build(List.of(running), List.of(future, pending), start, end, now);
        assertNull(report.getTrips().get(0).getUsedMileage());
        assertNull(report.getTrips().get(0).getStatisticsFuelId());
        decimal("0", report.getSummary().getWeekFuelQuantity());
        assertEquals(0, report.getSummary().getAbnormalMileageCount());
        assertNull(report.getSummary().getFuelConsumption());
    }

    @Test
    void latestFuelBeforeWeekProvidesMileageButIsNotCountedAgainAsWeekFuel() {
        var running = record("U", "A", start.minusDays(10), null, "100", null);
        var previous = fuel("P", "U", start.minusDays(1), "20", "160");
        previous.setMileage(new BigDecimal("400"));
        var report = FieldVehicleWeekReportBuilder.build(List.of(running), List.of(previous), start, end, now);
        decimal("0", report.getSummary().getUsedMileage());
        decimal("300", report.getDriveRows().get(0).getTotalMileage());
        decimal("0", report.getSummary().getWeekFuelQuantity());
        assertTrue(report.getWeekFuelItems().isEmpty());
        assertTrue(report.getDriveRows().get(0).getFuelItems().isEmpty());
        assertEquals("P", report.getTrips().get(0).getStatisticsFuelId());
        decimal("400", report.getDriveRows().get(0).getWeekStartMileage());
        decimal("400", report.getDriveRows().get(0).getWeekEndMileage());
        decimal("0", report.getTrips().get(0).getStatisticsFuelQuantity());
    }

    @Test
    void consecutiveWeeksCarryLastWeekFuelReadingWithoutRepeatingPreviousMileage() {
        var running = record("U", "A", start.minusWeeks(1), null, "500", null);
        var previous = fuel("P", "U", start.minusDays(1), "20", "160");
        previous.setMileage(new BigDecimal("1000"));
        var current = fuel("C", "U", end, "30", "240");
        current.setMileage(new BigDecimal("1300"));
        var next = fuel("N", "U", end.plusWeeks(1), "40", "320");
        next.setMileage(new BigDecimal("1600"));
        var items = List.of(next, previous, current);
        var week1 = FieldVehicleWeekReportBuilder.build(List.of(running), items, start, end, now.plusMonths(1));
        var week2 = FieldVehicleWeekReportBuilder.build(List.of(running), items, start.plusWeeks(1), end.plusWeeks(1), now.plusMonths(1));
        decimal("300", week1.getSummary().getUsedMileage());
        decimal("300", week2.getSummary().getUsedMileage());
        decimal("1300", week2.getDriveRows().get(0).getWeekStartMileage());
        decimal("1600", week2.getDriveRows().get(0).getWeekEndMileage());
        decimal("40", week2.getSummary().getWeekFuelQuantity());
        assertEquals("C", week2.getTrips().get(0).getStatisticsStartFuelId());
        running.setEndDate(end.plusWeeks(2)); running.setEndMileage(new BigDecimal("2000"));
        running.setStatus(FieldVehicleStatus.FINISHED.getCode());
        var historical = FieldVehicleWeekReportBuilder.build(List.of(running), items, start.plusWeeks(1), end.plusWeeks(1), now.plusMonths(1));
        decimal("300", historical.getSummary().getUsedMileage());
        decimal("1600", historical.getDriveRows().get(0).getWeekEndMileage());
    }

    @Test
    void missingLastWeekFuelDoesNotFallBackToDispatchOrFirstWeekReading() {
        var running = record("U", "A", start.minusWeeks(2), null, "100", null);
        var tooOld = fuel("OLD", "U", start.minusDays(8), "10", "80");
        tooOld.setMileage(new BigDecimal("200"));
        var current = fuel("C", "U", end, "30", "240");
        current.setMileage(new BigDecimal("600"));
        var report = FieldVehicleWeekReportBuilder.build(List.of(running), List.of(tooOld, current), start, end, now);
        assertNull(report.getTrips().get(0).getStatisticsStartMileage());
        assertNull(report.getTrips().get(0).getUsedMileage());
        assertNull(report.getDriveRows().get(0).getWeekStartMileage());
        decimal("600", report.getDriveRows().get(0).getWeekEndMileage());
        decimal("30", report.getSummary().getWeekFuelQuantity());
        assertNull(report.getSummary().getFuelConsumption());

        var previous = fuel("P", "U", start.minusDays(1), "20", "160");
        var missing = FieldVehicleWeekReportBuilder.build(List.of(running), List.of(tooOld, previous, current), start, end, now);
        assertEquals("P", missing.getTrips().get(0).getStatisticsStartFuelId());
        assertNull(missing.getTrips().get(0).getUsedMileage());
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
