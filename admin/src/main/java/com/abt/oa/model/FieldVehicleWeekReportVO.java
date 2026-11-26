package com.abt.oa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 周报聚合结果，所有区域共享查询条件和查询时点。 */
@Getter
@Setter
public class FieldVehicleWeekReportVO {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private LocalDate queryDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime queriedAt;
    private Summary summary = new Summary();
    private List<Resource> resources = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Trip> trips = new ArrayList<>();
    private List<FuelItem> weekFuelItems = new ArrayList<>();
    private List<FuelItem> pendingFuelItems = new ArrayList<>();
    private List<DriveRow> driveRows = new ArrayList<>();

    /** 全部命中行程的汇总；未知油耗为空，金额、数量按原始精度累加。 */
    @Getter
    @Setter
    public static class Summary {
        private int outCarCount;
        private int weekCarCount;
        private int dispatchCount;
        private int usingCount;
        private int endedCount;
        private BigDecimal weekFuelQuantity = BigDecimal.ZERO;
        private BigDecimal weekFuelAmount = BigDecimal.ZERO;
        private int weekFuelCount;
        private int pendingFuelCount;
        private BigDecimal usedMileage = BigDecimal.ZERO;
        private int abnormalMileageCount;
        private BigDecimal fuelConsumption;
        private BigDecimal consumptionFuelQuantity = BigDecimal.ZERO;
        private BigDecimal consumptionMileage = BigDecimal.ZERO;
        private int consumptionTripCount;
    }

    /** 一次用车及其完整加油明细。 */
    @Getter
    @Setter
    public static class Trip extends FieldVehicleListVO {
        private boolean crossWeek;
        private boolean mileageValid;
        /** 周前派车取上周最后一笔加油读数；本周派车取实际派车读数。 */
        private LocalDate statisticsStartDate;
        private BigDecimal statisticsStartMileage;
        private String statisticsStartFuelId;
        /** 本次统计采用的结束读数日期、公里数及加油记录；实际还车字段保持原值。 */
        private LocalDate statisticsEndDate;
        private BigDecimal statisticsEndMileage;
        private String statisticsFuelId;
        /** 参考油耗分子：截止日未结束取本周最近一笔升数，跨周已结束取周内升数，否则取整次升数。 */
        private BigDecimal statisticsFuelQuantity = BigDecimal.ZERO;
        private BigDecimal weekFuelQuantity = BigDecimal.ZERO;
        private BigDecimal weekFuelAmount = BigDecimal.ZERO;
        private List<FuelItem> fuelItems = new ArrayList<>();
    }

    /** 加油弹窗及详情明细，明确日期归属并保留所属行程标识。 */
    @Getter
    @Setter
    public static class FuelItem extends FieldVehicleFuelVO {
        private String carNo;
        private String purpose;
        private String fuelScope;
    }

    /** FullCalendar 车辆资源。 */
    @Getter
    @Setter
    public static class Resource {
        private String id;
        private String title;
        private int order;
    }

    /** 日历事件，end 为 FullCalendar 约定的不包含结束日期。 */
    @Getter
    @Setter
    public static class Event {
        private String id;
        private String resourceId;
        private String title;
        private LocalDate start;
        private LocalDate end;
        private boolean allDay = true;
        private List<String> classNames;
        private EventProps extendedProps;
    }

    /** 日历悬停展示信息。 */
    @Getter
    @Setter
    public static class EventProps extends FieldVehicleListVO {
        private String tripId;
        private boolean crossWeek;
        private String line;
    }

    /** 按车辆汇总；周边界采用行程统计读数，缺少读数或部门时为空。 */
    @Getter
    @Setter
    public static class DriveRow {
        private String carId;
        private int seq;
        private String carNo;
        private String dept;
        private BigDecimal weekStartMileage;
        private BigDecimal weekEndMileage;
        private BigDecimal usedMileage = BigDecimal.ZERO;
        private BigDecimal fuelQuantity = BigDecimal.ZERO;
        private BigDecimal fuelAmount = BigDecimal.ZERO;
        private BigDecimal totalMileage = BigDecimal.ZERO;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private BigDecimal fuelConsumption;
        private String remark;
        private List<FuelItem> fuelItems = new ArrayList<>();
    }
}
