package com.abt.oa.service.impl;

import com.abt.oa.entity.*;
import com.abt.oa.model.*;
import com.abt.oa.reposity.*;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 显式启用的真实 SQL Server 测试：仅创建随机命名的独立测试库，不连接现有业务库。
 * 凭据由 IPMS_TEST_SQL_USER/IPMS_TEST_SQL_PASSWORD 环境变量提供，不保存明文密码。
 */
@EnabledIfSystemProperty(named = "field.vehicle.sqlserver.test", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FieldVehicleSqlServerTest {
    private static final String MASTER_URL = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true";
    private String database;
    private String user;
    private String password;
    private String url;
    private SessionFactory sessions;
    private ValidatorFactory validators;
    private FieldVehicleServiceImpl service;
    private FieldVehicleUseRepository records;
    private FieldVehicleFuelRepository fuels;
    private CarInfoRepository cars;
    private TransactionTemplate transactions;

    @BeforeAll
    void initializeIsolatedDatabase() throws Exception {
        user = System.getenv().getOrDefault("IPMS_TEST_SQL_USER", "sa");
        password = Objects.requireNonNull(System.getenv("IPMS_TEST_SQL_PASSWORD"), "缺少测试环境变量 IPMS_TEST_SQL_PASSWORD");
        database = "ipms_field_vehicle_test_" + UUID.randomUUID().toString().replace("-", "");
        assertTrue(database.matches("ipms_field_vehicle_test_[0-9a-f]{32}"));
        try (Connection connection = DriverManager.getConnection(MASTER_URL, user, password); Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE [" + database + "]");
            statement.execute("ALTER DATABASE [" + database + "] SET COMPATIBILITY_LEVEL = 120");
        }
        url = MASTER_URL.replace("databaseName=master", "databaseName=" + database);
        // 只自动建测试用车辆档案；两张业务表必须由交付 SQL 原样创建。
        try (SessionFactory ignored = configuration("create").addAnnotatedClass(CarInfo.class).buildSessionFactory()) {
            execute(Files.readString(schemaScript()));
            execute(Files.readString(schemaScript())); // 验证重复执行。
        }
        sessions = configuration("none").addAnnotatedClass(CarInfo.class)
                .addAnnotatedClass(FieldVehicleUse.class).addAnnotatedClass(FieldVehicleFuel.class).buildSessionFactory();
        EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(sessions);
        JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager);
        records = factory.getRepository(FieldVehicleUseRepository.class);
        fuels = factory.getRepository(FieldVehicleFuelRepository.class);
        cars = factory.getRepository(CarInfoRepository.class);
        JpaTransactionManager manager = new JpaTransactionManager(sessions);
        transactions = new TransactionTemplate(manager);
        validators = Validation.buildDefaultValidatorFactory();
        service = new FieldVehicleServiceImpl(records, fuels, cars, validators.getValidator(), manager);
    }

    @BeforeEach
    void setup() {
        authenticate();
        transactions.executeWithoutResult(status -> {
            fuels.deleteAllInBatch(); records.deleteAllInBatch(); cars.deleteAllInBatch();
            cars.saveAndFlush(car("CAR"));
        });
    }

    @AfterEach
    void clearUser() { SecurityContextHolder.clearContext(); }

    @AfterAll
    void dropOnlyCreatedDatabase() throws Exception {
        if (sessions != null) sessions.close();
        if (validators != null) validators.close();
        if (database == null) return;
        // 固定前缀和随机后缀双重限制；绝不接受已有业务库名。
        if (!database.matches("ipms_field_vehicle_test_[0-9a-f]{32}")) throw new IllegalStateException("测试数据库名校验失败");
        try (Connection connection = DriverManager.getConnection(MASTER_URL, user, password); Statement statement = connection.createStatement()) {
            statement.execute("IF DB_ID('" + database + "') IS NOT NULL BEGIN ALTER DATABASE [" + database +
                    "] SET SINGLE_USER WITH ROLLBACK IMMEDIATE; DROP DATABASE [" + database + "]; END");
        }
    }

    @Test
    void vehicleQueryFiltersEnabledFlagAndKeepsBothKindsOfOccupiedCars() {
        String activeId = service.save(official("司机")).getId();
        transactions.executeWithoutResult(status -> {
            CarInfo fieldBusy = cars.findById("CAR").orElseThrow();
            fieldBusy.setIsUse("0"); cars.save(fieldBusy);
            CarInfo legacyBusy = car("LEGACY"); legacyBusy.setCarNo("A%_001"); legacyBusy.setIsUse("1");
            CarInfo disabled = car("DISABLED"); disabled.setIsAvtive("0");
            cars.saveAllAndFlush(List.of(legacyBusy, disabled));
        });
        CarOptionRequestForm form = new CarOptionRequestForm();
        var enabled = service.findList(form);
        assertEquals(2, enabled.size());
        assertTrue(enabled.stream().allMatch(car -> "1".equals(car.isUse())));
        form.setQuery("A%_");
        assertEquals(List.of("LEGACY"), service.findList(form).stream().map(FieldVehicleCarOptionVO::carId).toList());
        form.setQuery(null); form.setIsAvtive("0");
        var disabled = service.findList(form);
        assertEquals(1, disabled.size());
        assertEquals("DISABLED", disabled.get(0).carId());
        assertEquals("0", disabled.get(0).isUse());
        service.updateReturn(returnForm(activeId));
        form.setIsAvtive("1");
        var returned = service.findList(form).stream().filter(car -> "CAR".equals(car.carId())).findFirst().orElseThrow();
        assertEquals("0", returned.isUse());
        assertFalse(returned.requiresInitialMileage());
        assertNotNull(returned.startMileage());
    }

    @Test
    void emptyDraftUsesRealSchemaAndSubmittingChangesCarState() {
        FieldVehicleDetailVO draft = service.saveDraft(new FieldVehicleSaveRequest());
        LocalDateTime created = records.findById(draft.getId()).orElseThrow().getCreateDate();
        assertNull(draft.getUseDate());
        assertEquals("0", cars.findById("CAR").orElseThrow().getIsUse());
        FieldVehicleSaveRequest form = official("张三"); form.setId(draft.getId());
        FieldVehicleDetailVO active = service.save(form);
        assertEquals(draft.getId(), active.getId());
        assertEquals(created, active.getCreateDate());
        assertEquals("1", cars.findById("CAR").orElseThrow().getIsUse());
        FieldVehicleUse stored = records.findById(active.getId()).orElseThrow();
        assertEquals("张三", stored.getDrivers());
        assertNull(stored.getPeers());
        assertEquals(0, stored.getFuelCount());
        assertEquals(new BigDecimal("0.00"), stored.getFuelTotal());
    }

    @Test
    void ownDraftDeletionRemovesRowWithoutReleasingASelectedBusyCar() {
        String active = service.save(official("司机")).getId();
        String draft = service.saveDraft(official("司机")).getId();
        FieldVehicleDeleteRequest form = deleteForm(draft);
        service.delete(form);
        assertFalse(records.existsById(draft));
        assertTrue(records.existsById(active));
        assertEquals("1", cars.findById("CAR").orElseThrow().getIsUse());
        assertThrows(BusinessException.class, () -> service.findById(draft));
        assertThrows(BusinessException.class, () -> service.delete(form));
    }

    @Test
    void deletionRejectsAnotherCreatorsDraftAndAllDispatchedStates() {
        String draft = service.saveDraft(new FieldVehicleSaveRequest()).getId();
        authenticate("another-manager");
        assertThrows(BusinessException.class, () -> service.delete(deleteForm(draft)));
        assertTrue(records.existsById(draft));
        authenticate();
        String active = service.save(official("司机")).getId();
        assertThrows(BusinessException.class, () -> service.delete(deleteForm(active)));
        service.updateReturn(returnForm(active));
        assertThrows(BusinessException.class, () -> service.delete(deleteForm(active)));
        assertEquals(2, records.count());
    }

    @Test
    void deleteWaitingForDraftSubmissionCannotDeleteTheNowActiveRow() throws Exception {
        String id = service.saveDraft(official("司机")).getId();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch held = new CountDownLatch(1), submitDraft = new CountDownLatch(1);
        try {
            Future<?> submitted = pool.submit(() -> {
                authenticate();
                try {
                    transactions.executeWithoutResult(status -> {
                        records.findLockedById(id).orElseThrow();
                        held.countDown(); await(submitDraft);
                        FieldVehicleSaveRequest form = official("司机"); form.setId(id);
                        service.save(form);
                    });
                } finally { SecurityContextHolder.clearContext(); }
            });
            assertTrue(held.await(5, TimeUnit.SECONDS));
            Future<Boolean> deleted = pool.submit(() -> {
                authenticate();
                try { service.delete(deleteForm(id)); return true; }
                catch (BusinessException e) { return false; }
                finally { SecurityContextHolder.clearContext(); }
            });
            assertThrows(TimeoutException.class, () -> deleted.get(300, TimeUnit.MILLISECONDS));
            submitDraft.countDown(); submitted.get(10, TimeUnit.SECONDS);
            assertFalse(deleted.get(10, TimeUnit.SECONDS));
            assertEquals(1, records.findById(id).orElseThrow().getStatus());
            assertEquals("1", cars.findById("CAR").orElseThrow().getIsUse());
        } finally { submitDraft.countDown(); pool.shutdownNow(); }
    }

    @Test
    void livePreviewThenReturnRecalculatesAndFreezesAllFuelIncludingUndatedRows() {
        String id = service.save(official("张三")).getId();
        addFuel(id, "40.500", "300.00", LocalDate.of(2026, 11, 5));
        addFuel(id, "27.000", "200.00", null);
        FieldVehicleDetailVO preview = service.findById(id);
        assertTotals(preview, "67.500", "500.00", 2);
        assertEquals(new BigDecimal("500.00"), records.findById(id).orElseThrow().getFuelTotal());
        addFuel(id, "1.500", "10.00", null);
        FieldVehicleDetailVO closed = service.updateReturn(returnForm(id));
        assertTotals(closed, "69.000", "510.00", 3);
        assertTotals(service.findById(id), "69.000", "510.00", 3);
        FieldVehicleUse stored = records.findById(id).orElseThrow();
        assertEquals(new BigDecimal("69.000"), stored.getFuelTotalQuantity());
        assertEquals(new BigDecimal("510.00"), stored.getFuelTotal());
        assertEquals(3, stored.getFuelCount());
        assertEquals("0", cars.findById("CAR").orElseThrow().getIsUse());
        assertThrows(BusinessException.class, () -> addFuel(id, "1.000", "1.00", null));
        assertThrows(BusinessException.class, () -> service.updateReturn(returnForm(id)));
        FieldVehicleSaveRequest next = official("李四"); next.setStartMileage(null);
        assertEquals(new BigDecimal("120.00"), service.save(next).getStartMileage());
    }

    @Test
    void emptyReturnPersistsAllThreeZeros() {
        String id = service.save(official("独自用车")).getId();
        assertTotals(service.updateReturn(returnForm(id)), "0.000", "0.00", 0);
        FieldVehicleUse stored = records.findById(id).orElseThrow();
        assertEquals(0, stored.getFuelCount());
        assertNotNull(stored.getFuelTotal());
    }

    @Test
    void updateDeleteAndForeignMainChecksUseActualRowsAndPreserveCreateAudit() {
        String id = service.save(official("张三")).getId();
        FieldVehicleFuelVO first = addFuel(id, "40.500", "300.00", null);
        LocalDateTime created = fuels.findById(first.getId()).orElseThrow().getCreateDate();
        FieldVehicleFuelVO second = addFuel(id, "27.000", "200.00", null);
        FieldVehicleFuelUpdateRequest update = new FieldVehicleFuelUpdateRequest();
        update.setId(first.getId()); update.setMainId(id); update.setFuelQuantity(new BigDecimal("38.000"));
        update.setAmount(new BigDecimal("280.00")); update.setRemark("现场补充说明\n第二行");
        FieldVehicleFuelVO updated = service.updateFuel(update);
        assertEquals(created, updated.getCreateDate());
        assertEquals(first.getCreateUserid(), updated.getCreateUserid());
        assertEquals("现场补充说明\n第二行", updated.getRemark());
        transactions.executeWithoutResult(status -> cars.saveAndFlush(car("CAR2")));
        FieldVehicleSaveRequest another = official("另一名司机"); another.setCarId("CAR2");
        String anotherId = service.save(another).getId();
        update.setMainId(anotherId);
        assertThrows(BusinessException.class, () -> service.updateFuel(update));
        FieldVehicleFuelDeleteRequest delete = new FieldVehicleFuelDeleteRequest();
        delete.setId(second.getId()); delete.setMainId(id); service.deleteFuel(delete);
        assertTotals(service.updateReturn(returnForm(id)), "38.000", "280.00", 1);
    }

    @Test
    void everyFuelChangeRecalculatesAndPersistsTotalsIncludingLastDeletion() {
        String id = service.save(official("司机")).getId();
        assertStoredTotals(id, "0.000", "0.00", 0);
        FieldVehicleFuelVO first = addFuel(id, "40.500", "300.00", null);
        assertStoredTotals(id, "40.500", "300.00", 1);
        FieldVehicleFuelVO second = addFuel(id, "27.000", "200.00", null);
        assertStoredTotals(id, "67.500", "500.00", 2);
        FieldVehicleFuelUpdateRequest update = new FieldVehicleFuelUpdateRequest();
        update.setId(first.getId()); update.setMainId(id);
        update.setFuelQuantity(new BigDecimal("20.000")); update.setAmount(new BigDecimal("150.00"));
        service.updateFuel(update);
        assertStoredTotals(id, "47.000", "350.00", 2);
        FieldVehicleFuelDeleteRequest delete = new FieldVehicleFuelDeleteRequest();
        delete.setMainId(id); delete.setId(second.getId()); service.deleteFuel(delete);
        assertStoredTotals(id, "20.000", "150.00", 1);
        delete.setId(first.getId()); service.deleteFuel(delete);
        assertStoredTotals(id, "0.000", "0.00", 0);
        assertTotals(service.findById(id), "0.000", "0.00", 0);
        assertEquals(0, fuels.count());
    }

    @Test
    void concurrentFuelAddsDoNotLoseStoredTotals() throws Exception {
        String id = service.save(official("司机")).getId();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Callable<Void> addFirst = () -> {
                authenticate(); await(start);
                try { addFuel(id, "3.000", "30.00", null); return null; }
                finally { SecurityContextHolder.clearContext(); }
            };
            Callable<Void> addSecond = () -> {
                authenticate(); await(start);
                try { addFuel(id, "4.000", "40.00", null); return null; }
                finally { SecurityContextHolder.clearContext(); }
            };
            Future<Void> first = pool.submit(addFirst), second = pool.submit(addSecond);
            start.countDown(); first.get(10, TimeUnit.SECONDS); second.get(10, TimeUnit.SECONDS);
            assertStoredTotals(id, "7.000", "70.00", 2);
        } finally { start.countDown(); pool.shutdownNow(); }
    }

    @Test
    void failedMainSummaryUpdateRollsBackFuelInsertionAndDeletion() throws Exception {
        String id = service.save(official("司机")).getId();
        FieldVehicleFuelVO original = addFuel(id, "3.000", "30.00", null);
        execute("CREATE TRIGGER dbo.test_reject_fuel_totals ON dbo.oa_field_vehicle_use AFTER UPDATE AS " +
                "BEGIN IF UPDATE(fuel_count) AND EXISTS (SELECT 1 FROM inserted WHERE status = 1) " +
                "BEGIN RAISERROR ('test totals failure', 16, 1); ROLLBACK TRANSACTION; END END");
        try {
            assertThrows(RuntimeException.class, () -> addFuel(id, "4.000", "40.00", null));
            assertEquals(1, fuels.count());
            assertStoredTotals(id, "3.000", "30.00", 1);
            FieldVehicleFuelDeleteRequest delete = new FieldVehicleFuelDeleteRequest();
            delete.setMainId(id); delete.setId(original.getId());
            assertThrows(RuntimeException.class, () -> service.deleteFuel(delete));
            assertTrue(fuels.existsById(original.getId()));
            assertStoredTotals(id, "3.000", "30.00", 1);
        } finally { execute("DROP TRIGGER dbo.test_reject_fuel_totals"); }
    }

    @Test
    void migratesLegacyNullTotalsAndPreservesFinishedSettlementAndAudit() throws Exception {
        String finished = service.save(official("司机")).getId();
        addFuel(finished, "10.000", "70.00", null); service.updateReturn(returnForm(finished));
        FieldVehicleSaveRequest next = official("司机"); next.setStartMileage(null);
        String active = service.save(next).getId(); addFuel(active, "5.000", "30.00", null);
        String draft = service.saveDraft(new FieldVehicleSaveRequest()).getId();
        LocalDateTime created = records.findById(active).orElseThrow().getCreateDate();
        execute("ALTER TABLE dbo.oa_field_vehicle_use DROP CONSTRAINT CK_ofvu_fuel_summary; " +
                "ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN fuel_total_quantity DECIMAL(38,3) NULL; " +
                "ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN fuel_total DECIMAL(38,2) NULL; " +
                "ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN fuel_count INT NULL; " +
                "UPDATE dbo.oa_field_vehicle_use SET fuel_total_quantity = NULL, fuel_total = NULL, fuel_count = NULL WHERE status IN (0, 1); " +
                "ALTER TABLE dbo.oa_field_vehicle_use ADD CONSTRAINT CK_ofvu_fuel_summary CHECK (" +
                "(status IN (0, 1) AND fuel_total_quantity IS NULL AND fuel_total IS NULL AND fuel_count IS NULL) OR " +
                "(status = 2 AND fuel_total_quantity IS NOT NULL AND fuel_total IS NOT NULL AND fuel_count IS NOT NULL));");
        Path script = schemaScript().resolveSibling("20261105_field_vehicle_realtime_totals.sql");
        execute(Files.readString(script)); execute(Files.readString(script));
        assertStoredTotals(active, "5.000", "30.00", 1);
        assertStoredTotals(draft, "0.000", "0.00", 0);
        assertStoredTotals(finished, "10.000", "70.00", 1);
        assertEquals(created, records.findById(active).orElseThrow().getCreateDate());
        assertEquals(3, records.count()); assertEquals(2, fuels.count());
        assertEquals("1", cars.findById("CAR").orElseThrow().getIsUse());
    }

    @Test
    void personFilteringAndGroupedPaginationDoNotExpandFuelRows() {
        String exact = "张\"三%_![\\";
        String id = service.save(official(exact)).getId();
        addFuel(id, "1.000", "1.00", null); addFuel(id, "2.000", "2.00", null);
        service.updateReturn(returnForm(id));
        FieldVehicleSaveRequest next = official(exact + "丰"); next.setStartMileage(null);
        service.save(next);
        FieldVehicleRequestForm form = new FieldVehicleRequestForm(); form.setDriverName(exact);
        var page = service.findPage(form);
        assertEquals(2, page.getTotalElements()); // 包含查询，后缀不同也命中。
        form.setStatus(2);
        page = service.findPage(form);
        assertEquals(1, page.getTotalElements());
        assertEquals(id, page.getContent().get(0).getId());
        assertEquals(2, page.getContent().get(0).getFuelCount());
        form.setDriverName(null); form.setStatus(null); form.setLimit(1);
        assertEquals(2, service.findPage(form).getTotalElements());
        var vehicles = service.findList(new CarOptionRequestForm());
        assertEquals(1, vehicles.size());
        assertEquals("1", vehicles.get(0).isUse());
    }

    @Test
    void fuzzyNamesSearchesActualNamesWithoutMatchingJsonKeysOrJoiningPeople() {
        FieldVehicleSaveRequest first = official("阿张三李");
        first.setDrivers("阿张三李、王五");
        first.setPeers("赵李四钱、孙七");
        String id = service.save(first).getId();
        service.saveDraft(official("张三丰"));
        FieldVehicleRequestForm form = new FieldVehicleRequestForm();
        form.setDriverName("张三");
        assertEquals(2, service.findPage(form).getTotalElements());
        form.setPeerName("李四");
        assertEquals(id, service.findPage(form).getContent().get(0).getId());
        assertEquals(1, service.findPage(form).getTotalElements());
        form.setPeerName(null); form.setDriverName("name");
        assertEquals(0, service.findPage(form).getTotalElements());
        form.setDriverName("李王"); // 不得跨两个姓名拼接匹配。
        assertEquals(0, service.findPage(form).getTotalElements());
        form.setDriverName(null); form.setPeerName("%");
        assertEquals(0, service.findPage(form).getTotalElements());
        form.setPeerName("["); // 空同行人数组不能命中 JSON 结构字符。
        assertEquals(0, service.findPage(form).getTotalElements());
    }

    @Test
    void twoConcurrentDispatchesOnlyOneSucceeds() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Callable<Boolean> dispatch = () -> {
                authenticate(); start.await();
                try { service.save(official("司机")); return true; }
                catch (BusinessException e) { return false; }
                finally { SecurityContextHolder.clearContext(); }
            };
            Future<Boolean> first = pool.submit(dispatch), second = pool.submit(dispatch);
            start.countDown();
            assertNotEquals(first.get(15, TimeUnit.SECONDS), second.get(15, TimeUnit.SECONDS));
            assertEquals(1, records.count());
            assertEquals("1", cars.findById("CAR").orElseThrow().getIsUse());
        } finally { pool.shutdownNow(); }
    }

    @Test
    void returnWaitsForFuelTransactionAndIncludesItInFrozenSummary() throws Exception {
        String id = service.save(official("张三")).getId();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch held = new CountDownLatch(1), finishFuel = new CountDownLatch(1);
        try {
            Future<?> fuel = pool.submit(() -> {
                authenticate();
                try {
                    transactions.executeWithoutResult(status -> {
                        records.findLockedById(id).orElseThrow();
                        held.countDown();
                        await(finishFuel);
                        addFuel(id, "8.500", "60.00", null);
                    });
                } finally { SecurityContextHolder.clearContext(); }
            });
            assertTrue(held.await(5, TimeUnit.SECONDS));
            Future<FieldVehicleDetailVO> returned = pool.submit(() -> {
                authenticate();
                try { return service.updateReturn(returnForm(id)); }
                finally { SecurityContextHolder.clearContext(); }
            });
            assertThrows(TimeoutException.class, () -> returned.get(300, TimeUnit.MILLISECONDS));
            finishFuel.countDown(); fuel.get(10, TimeUnit.SECONDS);
            assertTotals(returned.get(10, TimeUnit.SECONDS), "8.500", "60.00", 1);
        } finally { finishFuel.countDown(); pool.shutdownNow(); }
    }

    @Test
    void databaseFailureRollsBackReturnSummaryAndVehicleRelease() throws Exception {
        String id = service.save(official("张三")).getId();
        addFuel(id, "8.500", "60.00", null);
        execute("CREATE TRIGGER dbo.test_reject_return ON dbo.oa_field_vehicle_use AFTER UPDATE AS " +
                "BEGIN IF EXISTS (SELECT 1 FROM inserted WHERE status = 2) " +
                "BEGIN RAISERROR ('test return failure', 16, 1); ROLLBACK TRANSACTION; END END");
        try {
            assertThrows(RuntimeException.class, () -> service.updateReturn(returnForm(id)));
            FieldVehicleUse stored = records.findById(id).orElseThrow();
            assertEquals(1, stored.getStatus());
            assertEquals(1, stored.getFuelCount());
            assertEquals(new BigDecimal("60.00"), stored.getFuelTotal());
            assertNull(stored.getEndDate());
            assertEquals("1", cars.findById("CAR").orElseThrow().getIsUse());
        } finally { execute("DROP TRIGGER dbo.test_reject_return"); }
    }

    @Test
    void backfillsMissingConstraintsWithExistingDataAndCanRunTwice() throws Exception {
        String id = service.save(official("司机")).getId();
        addFuel(id, "8.500", "60.00", null);
        execute("ALTER TABLE dbo.oa_field_vehicle_fuel DROP CONSTRAINT FK_ofvf_main, CK_ofvf_amount, CK_ofvf_quantity; " +
                "ALTER TABLE dbo.oa_field_vehicle_use DROP CONSTRAINT CK_ofvu_status, CK_ofvu_start_mileage, " +
                "CK_ofvu_expected_date, CK_ofvu_required, CK_ofvu_finished, CK_ofvu_fuel_summary;");
        Path script = schemaScript().resolveSibling("20261105_field_vehicle_constraints.sql");
        execute(Files.readString(script));
        execute(Files.readString(script));
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM sys.check_constraints " +
                     "WHERE parent_object_id IN (OBJECT_ID(N'dbo.oa_field_vehicle_use'), OBJECT_ID(N'dbo.oa_field_vehicle_fuel')) " +
                     "AND is_disabled = 0 AND is_not_trusted = 0")) {
            assertTrue(result.next());
            assertEquals(8, result.getInt(1));
        }
        assertEquals(1, records.count());
        assertEquals(1, fuels.count());
        assertTotals(service.updateReturn(returnForm(id)), "8.500", "60.00", 1);
    }

    private Configuration configuration(String ddl) {
        Configuration config = new Configuration().setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", user).setProperty("hibernate.connection.password", password)
                .setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .setProperty("hibernate.dialect", SqlServer2014Dialect.class.getName())
                .setProperty("hibernate.hbm2ddl.auto", ddl).setProperty("hibernate.show_sql", "false");
        config.getProperties().put("hibernate.session_factory.statement_inspector", (StatementInspector) sql -> {
            assertFalse(sql.toLowerCase(Locale.ROOT).matches("(?s).*(?<![a-z])(trim|openjson|json_value|string_agg|string_split)\\s*\\(.*"), sql);
            return sql;
        });
        return config;
    }

    /** 即使测试宿主为 2022，也要求 Hibernate 按 2014 方言生成 SQL。 */
    public static class SqlServer2014Dialect extends SQLServerDialect {
        public SqlServer2014Dialect() { super(DatabaseVersion.make(12, 0)); }
    }

    private static Path schemaScript() {
        Path dir = Path.of("").toAbsolutePath();
        while (dir != null) {
            Path script = dir.resolve("sql/20261105_field_vehicle.sql");
            if (Files.isRegularFile(script)) return script;
            dir = dir.getParent();
        }
        throw new IllegalStateException("找不到交付建表脚本");
    }

    private void execute(String sql) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void authenticate() {
        authenticate("manager");
    }

    private static void authenticate(String id) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserView().setId(id).setName("Manager"), "",
                List.of(new SimpleGrantedAuthority(FieldVehicleServiceImpl.MANAGER_AUTHORITY))));
    }

    private static CarInfo car(String id) {
        CarInfo car = new CarInfo(); car.setId(id); car.setCarNo("CAR-001"); car.setIsAvtive("1"); car.setIsUse("0");
        car.setInitialKil(BigDecimal.ZERO); car.setSyInitialKil(new BigDecimal("9999.00"));
        LocalDateTime now = LocalDateTime.now(); car.setApprovalData(now); car.setInsuredStartData(now); car.setInsuredEndData(now);
        car.setCreateUserID("manager"); car.setCreateDate(now); car.setOperator("manager"); car.setOperatedate(now); return car;
    }

    private static FieldVehicleSaveRequest official(String name) {
        FieldVehicleSaveRequest form = new FieldVehicleSaveRequest(); form.setCarId("CAR");
        form.setDrivers(name); form.setPurpose("采样");
        form.setDeparturePlace("公司"); form.setDestination("现场"); form.setUseDate(LocalDate.of(2026, 11, 5));
        form.setStartMileage(new BigDecimal("100.00")); return form;
    }

    private FieldVehicleFuelVO addFuel(String id, String quantity, String amount, LocalDate date) {
        FieldVehicleFuelSaveRequest form = new FieldVehicleFuelSaveRequest(); form.setMainId(id);
        form.setFuelQuantity(new BigDecimal(quantity)); form.setAmount(new BigDecimal(amount)); form.setFuelDate(date);
        return service.saveFuel(form);
    }

    private static FieldVehicleReturnRequest returnForm(String id) {
        FieldVehicleReturnRequest form = new FieldVehicleReturnRequest(); form.setId(id);
        form.setEndDate(LocalDate.of(2026, 11, 7)); form.setEndMileage(new BigDecimal("120.00")); return form;
    }

    private static FieldVehicleDeleteRequest deleteForm(String id) {
        FieldVehicleDeleteRequest form = new FieldVehicleDeleteRequest(); form.setId(id); return form;
    }

    private static void assertTotals(FieldVehicleListVO vo, String litres, String amount, int count) {
        assertEquals(new BigDecimal(litres), vo.getFuelTotalQuantity());
        assertEquals(new BigDecimal(amount), vo.getFuelTotal()); assertEquals(count, vo.getFuelCount());
    }

    private void assertStoredTotals(String id, String litres, String amount, int count) {
        FieldVehicleUse stored = records.findById(id).orElseThrow();
        assertEquals(new BigDecimal(litres), stored.getFuelTotalQuantity());
        assertEquals(new BigDecimal(amount), stored.getFuelTotal());
        assertEquals(count, stored.getFuelCount());
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("等待并发测试超时");
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException(e); }
    }
}
