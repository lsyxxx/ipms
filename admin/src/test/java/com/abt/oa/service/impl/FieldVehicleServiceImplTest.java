package com.abt.oa.service.impl;

import com.abt.oa.entity.*;
import com.abt.oa.enums.FieldVehicleStatus;
import com.abt.oa.model.*;
import com.abt.oa.reposity.*;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 无数据库服务测试：权限、草稿分组、实时展示及最终结算。 */
class FieldVehicleServiceImplTest {
    private final FieldVehicleUseRepository records = mock(FieldVehicleUseRepository.class);
    private final FieldVehicleFuelRepository fuels = mock(FieldVehicleFuelRepository.class);
    private final CarInfoRepository cars = mock(CarInfoRepository.class);
    private final PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
    private ValidatorFactory validators;
    private FieldVehicleServiceImpl service;

    @BeforeEach
    void setup() {
        validators = Validation.buildDefaultValidatorFactory();
        when(transactions.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        service = new FieldVehicleServiceImpl(records, fuels, cars, validators.getValidator(), transactions);
        authenticate(true);
        AtomicInteger sequence = new AtomicInteger();
        when(records.saveAndFlush(any())).thenAnswer(call -> {
            FieldVehicleUse record = call.getArgument(0);
            if (record.getId() == null) record.setId("FV" + sequence.incrementAndGet());
            return record;
        });
        when(fuels.saveAndFlush(any())).thenAnswer(call -> {
            FieldVehicleFuel fuel = call.getArgument(0);
            if (fuel.getId() == null) fuel.setId("FUEL" + sequence.incrementAndGet());
            return fuel;
        });
        when(fuels.save(any())).thenAnswer(call -> {
            FieldVehicleFuel fuel = call.getArgument(0);
            if (fuel.getId() == null) fuel.setId("FUEL" + sequence.incrementAndGet());
            return fuel;
        });
        when(fuels.findSummary(anyString())).thenReturn(new FieldVehicleFuelSummary(null, null, 0L, null));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        validators.close();
    }

    @Test
    void vehicleListIncludesLegacyAndFieldOccupationAndKeepsMileageHints() {
        CarInfo idle = new CarInfo(); idle.setId("IDLE"); idle.setIsUse(" 0 ");
        CarInfo legacyBusy = new CarInfo(); legacyBusy.setId("LEGACY"); legacyBusy.setIsUse(" 1 ");
        CarInfo fieldBusy = new CarInfo(); fieldBusy.setId("FIELD"); fieldBusy.setIsUse(null);
        CarInfo blank = new CarInfo(); blank.setId("BLANK"); blank.setIsUse(" ");
        when(cars.findList("1", null)).thenReturn(List.of(idle, legacyBusy, fieldBusy, blank));
        when(records.findUsingCarIds(anyCollection(), eq(FieldVehicleStatus.USING.getCode())))
                .thenReturn(List.of("FIELD"));
        when(records.findLatestMileages(anyCollection(), eq(FieldVehicleStatus.FINISHED.getCode())))
                .thenReturn(List.of(new FieldVehicleLastMileage("IDLE", new BigDecimal("200.00"))));
        List<FieldVehicleCarOptionVO> result = service.findList(new CarOptionRequestForm());
        assertEquals(List.of("0", "1", "1", "0"), result.stream().map(FieldVehicleCarOptionVO::isUse).toList());
        assertEquals(new BigDecimal("200.00"), result.get(0).startMileage());
        assertFalse(result.get(0).requiresInitialMileage());
        assertTrue(result.get(1).requiresInitialMileage());
        verify(records, never()).existsByCarIdAndStatus(anyString(), anyInt());
    }

    @Test
    void vehicleListPassesDisabledFilterEscapesPlateAndRejectsInvalidFlag() {
        CarOptionRequestForm form = new CarOptionRequestForm();
        form.setIsAvtive("0"); form.setQuery("A%_[!");
        assertTrue(service.findList(form).isEmpty());
        verify(cars).findList("0", "%A!%!_![!!%");
        verifyNoInteractions(records);
        form.setIsAvtive("2");
        assertThrows(BusinessException.class, () -> service.findList(form));
        verify(cars, times(1)).findList(any(), any());
    }

    @Test
    void vehicleStatusAndMileageLookupsSplitLargeListsIntoBatches() {
        List<CarInfo> vehicles = new ArrayList<>();
        for (int i = 0; i < 501; i++) {
            CarInfo car = new CarInfo(); car.setId("C" + i); vehicles.add(car);
        }
        when(cars.findList("1", null)).thenReturn(vehicles);
        when(records.findUsingCarIds(anyCollection(), anyInt())).thenAnswer(call ->
                ((Collection<String>) call.getArgument(0)).contains("C500") ? List.of("C500") : List.of());
        List<FieldVehicleCarOptionVO> result = service.findList(new CarOptionRequestForm());
        assertEquals(501, result.size());
        assertEquals("1", result.get(500).isUse());
        verify(records).findUsingCarIds(argThat(ids -> ids.size() == 500), anyInt());
        verify(records).findUsingCarIds(List.of("C500"), FieldVehicleStatus.USING.getCode());
        verify(records).findLatestMileages(argThat(ids -> ids.size() == 500), anyInt());
        verify(records).findLatestMileages(List.of("C500"), FieldVehicleStatus.FINISHED.getCode());
    }

    @Test
    void emptyDraftPersistsAuditWithoutOccupyingCar() {
        FieldVehicleDetailVO draft = service.saveDraft(new FieldVehicleSaveRequest());
        assertEquals(FieldVehicleStatus.DRAFT.getCode(), draft.getStatus());
        assertNull(draft.getDrivers());
        assertNull(draft.getPeers());
        assertNull(draft.getUseDate());
        assertEquals("manager", draft.getCreateUserid());
        assertNotNull(draft.getCreateDate());
        assertEquals(0, draft.getFuelCount());
        verifyNoInteractions(cars);
    }

    @Test
    void deletesOwnDraftPhysicallyWithoutChangingVehicleOccupation() {
        FieldVehicleUse draft = record(FieldVehicleStatus.DRAFT);
        draft.create("manager", "Manager");
        when(records.findLockedById("FV")).thenReturn(Optional.of(draft));
        FieldVehicleDeleteRequest form = deleteForm();
        service.delete(form);
        verify(records).delete(draft);
        verify(records).flush();
        verifyNoInteractions(cars);
    }

    @Test
    void anotherManagersDraftCannotBeDeleted() {
        FieldVehicleUse draft = record(FieldVehicleStatus.DRAFT);
        draft.create("another-manager", "Another manager");
        when(records.findLockedById("FV")).thenReturn(Optional.of(draft));
        BusinessException error = assertThrows(BusinessException.class, () -> service.delete(deleteForm()));
        assertEquals("只能删除本人创建的草稿", error.getMessage());
        verify(records, never()).delete(any(FieldVehicleUse.class));
        verifyNoInteractions(cars, fuels);
    }

    @Test
    void activeOrFinishedRecordsCannotBeDeletedEvenByTheirCreator() {
        FieldVehicleUse record = record(FieldVehicleStatus.USING);
        record.create("manager", "Manager");
        when(records.findLockedById("FV")).thenReturn(Optional.of(record));
        assertThrows(BusinessException.class, () -> service.delete(deleteForm()));
        record.setStatus(FieldVehicleStatus.FINISHED.getCode());
        assertThrows(BusinessException.class, () -> service.delete(deleteForm()));
        verify(records, never()).delete(any(FieldVehicleUse.class));
        verifyNoInteractions(cars, fuels);
    }

    @Test
    void draftWithInconsistentFuelRowsIsNotCascadedOrDeleted() {
        FieldVehicleUse draft = record(FieldVehicleStatus.DRAFT);
        draft.create("manager", "Manager");
        when(records.findLockedById("FV")).thenReturn(Optional.of(draft));
        when(fuels.existsByMainId("FV")).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.delete(deleteForm()));
        verify(records, never()).delete(any(FieldVehicleUse.class));
        verify(fuels, never()).delete(any());
    }

    @Test
    void completeValidationOnlyAppliesToOfficialSubmission() {
        assertThrows(BusinessException.class, () -> service.save(new FieldVehicleSaveRequest()));
        verifyNoInteractions(records, cars);
        FieldVehicleSaveRequest draft = new FieldVehicleSaveRequest();
        draft.setDrivers(null);
        assertNull(service.saveDraft(draft).getDrivers());
        draft.setStartMileage(new BigDecimal("-1"));
        assertThrows(BusinessException.class, () -> service.saveDraft(draft));
    }

    @Test
    void publishesExistingDraftWithOneManualDriverAndPreservesCreationAudit() {
        FieldVehicleUse draft = record(FieldVehicleStatus.DRAFT);
        draft.create("original", "Original manager");
        var created = draft.getCreateDate();
        when(records.findLockedById("FV")).thenReturn(Optional.of(draft));
        CarInfo car = availableCar();
        FieldVehicleSaveRequest form = official();
        form.setId("FV");
        FieldVehicleDetailVO result = service.save(form);
        assertEquals("1", car.getIsUse());
        assertEquals(" 张三 ", result.getDrivers());
        assertNull(result.getPeers());
        assertNull(result.getExpectedReturnDate());
        assertEquals("original", result.getCreateUserid());
        assertEquals(created, result.getCreateDate());
        assertEquals(new BigDecimal("0.00"), draft.getFuelTotal());
        assertEquals(0, draft.getFuelCount());
    }

    @Test
    void rejectsLegacyOccupationAndRequiresInitialMileageOnlyOnFirstDispatch() {
        CarInfo car = availableCar();
        car.setIsUse("1");
        assertThrows(BusinessException.class, () -> service.save(official()));
        verify(cars, never()).save(any());
        car.setIsUse("0");
        FieldVehicleSaveRequest form = official();
        form.setStartMileage(null);
        assertThrows(BusinessException.class, () -> service.save(form));
        FieldVehicleUse previous = record(FieldVehicleStatus.FINISHED);
        previous.setEndMileage(new BigDecimal("200.00"));
        when(records.findLatestEnded(eq("CAR"), anyInt(), any())).thenReturn(List.of(previous));
        assertEquals(new BigDecimal("200.00"), service.save(form).getStartMileage());
    }

    @Test
    void previewReadsPersistedTotalsAndReturnRecalculatesLatestValues() {
        FieldVehicleUse record = usingRecord();
        record.setFuelTotalQuantity(new BigDecimal("67.500"));
        record.setFuelTotal(new BigDecimal("500.00"));
        record.setFuelCount(2);
        CarInfo car = availableCar();
        car.setIsUse("1");
        when(records.findById("FV")).thenReturn(Optional.of(record));
        when(records.findCarId("FV")).thenReturn(Optional.of("CAR"));
        when(fuels.findItems("FV")).thenReturn(List.of(fuel("F1", "40.500", "300.00"), fuel("F2", "27.000", "200.00")));
        FieldVehicleDetailVO preview = service.findById("FV");
        assertEquals(new BigDecimal("67.500"), preview.getFuelTotalQuantity());
        assertEquals(new BigDecimal("500.00"), preview.getFuelTotal());
        assertEquals(2, preview.getFuelCount());
        assertEquals(new BigDecimal("500.00"), record.getFuelTotal());
        verify(records, never()).saveAndFlush(any());
        verify(fuels, never()).findSummary(anyString());
        when(fuels.findSummary("FV")).thenReturn(new FieldVehicleFuelSummary(new BigDecimal("70.000"), new BigDecimal("520.00"), 3L, null));
        FieldVehicleDetailVO result = service.updateReturn(returnForm());
        assertEquals(3, result.getFuelCount());
        assertEquals(new BigDecimal("70.000"), record.getFuelTotalQuantity());
        assertEquals(new BigDecimal("520.00"), record.getFuelTotal());
        assertEquals("0", car.getIsUse());
        assertEquals("manager", record.getReturnUserId());
        assertEquals(FieldVehicleStatus.FINISHED.getCode(), record.getStatus());
    }

    @Test
    void emptyFuelReturnPersistsZerosAndDateValidationChecksDatedRowsOnly() {
        FieldVehicleUse record = usingRecord();
        availableCar();
        when(records.findCarId("FV")).thenReturn(Optional.of("CAR"));
        when(fuels.findSummary("FV")).thenReturn(new FieldVehicleFuelSummary(null, null, 0L, LocalDate.of(2026, 11, 10)));
        assertThrows(BusinessException.class, () -> service.updateReturn(returnForm()));
        assertEquals(FieldVehicleStatus.USING.getCode(), record.getStatus());
        when(fuels.findSummary("FV")).thenReturn(new FieldVehicleFuelSummary(null, null, 0L, null));
        service.updateReturn(returnForm());
        assertEquals(new BigDecimal("0.000"), record.getFuelTotalQuantity());
        assertEquals(new BigDecimal("0.00"), record.getFuelTotal());
        assertEquals(0, record.getFuelCount());
    }

    @Test
    void fuelRequiresQuantityButAllowsMissingDateAndCanClearRemark() {
        usingRecord();
        FieldVehicleFuelSaveRequest form = fuelForm();
        form.setFuelQuantity(null);
        assertThrows(BusinessException.class, () -> service.saveFuel(form));
        form.setFuelQuantity(new BigDecimal("27.000"));
        FieldVehicleFuelVO created = service.saveFuel(form);
        assertNull(created.getFuelDate());
        assertEquals("manager", created.getCreateUserid());
        FieldVehicleFuel existing = fuel("F1", "27.000", "200.00");
        existing.create("original", "Original");
        existing.setRemark("old");
        when(fuels.findByIdAndMainId("F1", "FV")).thenReturn(Optional.of(existing));
        FieldVehicleFuelUpdateRequest update = new FieldVehicleFuelUpdateRequest();
        update.setId("F1"); update.setMainId("FV"); update.setAmount(new BigDecimal("200.00"));
        update.setFuelQuantity(new BigDecimal("27.000"));
        assertNull(service.updateFuel(update).getRemark());
        assertEquals("original", existing.getCreateUserid());
        update.setId("OTHER");
        assertThrows(BusinessException.class, () -> service.updateFuel(update));
    }

    @Test
    void fuelCreateUpdateDeleteReplaceMainTotalsWithCurrentAggregate() {
        FieldVehicleUse record = usingRecord();
        record.setFuelCount(99); record.setFuelTotal(new BigDecimal("999.00"));
        when(fuels.findSummary("FV")).thenReturn(new FieldVehicleFuelSummary(new BigDecimal("27.000"), new BigDecimal("200.00"), 1L, null));
        FieldVehicleFuelVO created = service.saveFuel(fuelForm());
        assertEquals(1, record.getFuelCount());
        assertEquals(new BigDecimal("27.000"), record.getFuelTotalQuantity());
        assertEquals(new BigDecimal("200.00"), record.getFuelTotal());
        FieldVehicleFuel existing = fuel(created.getId(), "27.000", "200.00");
        when(fuels.findByIdAndMainId(created.getId(), "FV")).thenReturn(Optional.of(existing));
        FieldVehicleFuelUpdateRequest update = new FieldVehicleFuelUpdateRequest();
        update.setId(created.getId()); update.setMainId("FV");
        update.setAmount(new BigDecimal("150.00")); update.setFuelQuantity(new BigDecimal("20.000"));
        when(fuels.findSummary("FV")).thenReturn(new FieldVehicleFuelSummary(new BigDecimal("20.000"), new BigDecimal("150.00"), 1L, null));
        service.updateFuel(update);
        assertEquals(new BigDecimal("150.00"), record.getFuelTotal());
        assertEquals(new BigDecimal("20.000"), record.getFuelTotalQuantity());
        FieldVehicleFuelDeleteRequest delete = new FieldVehicleFuelDeleteRequest(); delete.setId(created.getId()); delete.setMainId("FV");
        when(fuels.findSummary("FV")).thenReturn(new FieldVehicleFuelSummary(null, null, 0L, null));
        service.deleteFuel(delete);
        assertEquals(new BigDecimal("0.000"), record.getFuelTotalQuantity());
        assertEquals(new BigDecimal("0.00"), record.getFuelTotal());
        assertEquals(0, record.getFuelCount());
        verify(fuels, times(3)).findSummary("FV");
    }

    @Test
    void finishedRecordsRejectAllWritePaths() {
        FieldVehicleUse closed = usingRecord();
        closed.setStatus(FieldVehicleStatus.FINISHED.getCode());
        availableCar();
        when(records.findCarId("FV")).thenReturn(Optional.of("CAR"));
        FieldVehicleSaveRequest save = official(); save.setId("FV");
        FieldVehicleUpdateRequest update = usageUpdate();
        FieldVehicleFuelUpdateRequest fuelUpdate = new FieldVehicleFuelUpdateRequest();
        fuelUpdate.setId("F1"); fuelUpdate.setMainId("FV"); fuelUpdate.setAmount(BigDecimal.ONE); fuelUpdate.setFuelQuantity(BigDecimal.ONE);
        FieldVehicleFuelDeleteRequest delete = new FieldVehicleFuelDeleteRequest(); delete.setId("F1"); delete.setMainId("FV");
        assertThrows(BusinessException.class, () -> service.saveDraft(save));
        assertThrows(BusinessException.class, () -> service.save(save));
        assertThrows(BusinessException.class, () -> service.update(update));
        assertThrows(BusinessException.class, () -> service.saveFuel(fuelForm()));
        assertThrows(BusinessException.class, () -> service.updateFuel(fuelUpdate));
        assertThrows(BusinessException.class, () -> service.deleteFuel(delete));
        assertThrows(BusinessException.class, () -> service.updateReturn(returnForm()));
        verify(records, never()).saveAndFlush(any());
        verify(fuels, never()).saveAndFlush(any());
        verify(cars, never()).save(any());
    }

    @Test
    void ordinaryUsersAreDeniedEveryWriteEvenWhenTheyAreNamedAsDriver() {
        authenticate(false);
        assertThrows(AccessDeniedException.class, () -> service.saveDraft(null));
        assertThrows(AccessDeniedException.class, () -> service.delete(null));
        assertThrows(AccessDeniedException.class, () -> service.save(null));
        assertThrows(AccessDeniedException.class, () -> service.update(null));
        assertThrows(AccessDeniedException.class, () -> service.updateReturn(null));
        assertThrows(AccessDeniedException.class, () -> service.saveFuel(null));
        assertThrows(AccessDeniedException.class, () -> service.updateFuel(null));
        assertThrows(AccessDeniedException.class, () -> service.deleteFuel(null));
        FieldVehicleUse draft = record(FieldVehicleStatus.DRAFT);
        when(records.findById("FV")).thenReturn(Optional.of(draft));
        assertNotNull(service.findById("FV"));
        verifyNoInteractions(cars, transactions);
    }

    private static void authenticate(boolean manager) {
        var roles = manager ? List.of(new SimpleGrantedAuthority(FieldVehicleServiceImpl.MANAGER_AUTHORITY)) : List.<SimpleGrantedAuthority>of();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserView().setId("manager").setName("Manager"), "", roles));
    }

    private FieldVehicleUse usingRecord() {
        FieldVehicleUse record = record(FieldVehicleStatus.USING);
        when(records.findLockedById("FV")).thenReturn(Optional.of(record));
        return record;
    }

    private static FieldVehicleUse record(FieldVehicleStatus status) {
        FieldVehicleUse record = new FieldVehicleUse(); record.setId("FV"); record.setCarId("CAR");
        record.setStatus(status.getCode()); record.setStartMileage(new BigDecimal("100.00"));
        record.setUseDate(LocalDate.of(2026, 11, 5)); return record;
    }

    private CarInfo availableCar() {
        CarInfo car = new CarInfo(); car.setId("CAR"); car.setCarNo("CAR-001"); car.setIsAvtive("1"); car.setIsUse("0");
        when(cars.findLockedById("CAR")).thenReturn(Optional.of(car)); return car;
    }

    private static FieldVehicleSaveRequest official() {
        FieldVehicleSaveRequest form = new FieldVehicleSaveRequest(); form.setCarId("CAR");
        form.setDrivers(" 张三 "); form.setPurpose("采样");
        form.setDeparturePlace("公司"); form.setDestination("现场"); form.setUseDate(LocalDate.of(2026, 11, 5));
        form.setStartMileage(new BigDecimal("100.00")); return form;
    }

    private static FieldVehicleUpdateRequest usageUpdate() {
        FieldVehicleUpdateRequest form = new FieldVehicleUpdateRequest(); form.setId("FV");
        form.setDrivers("张三"); form.setPurpose("采样");
        form.setDeparturePlace("公司"); form.setDestination("现场"); return form;
    }

    private static FieldVehicleReturnRequest returnForm() {
        FieldVehicleReturnRequest form = new FieldVehicleReturnRequest(); form.setId("FV");
        form.setEndDate(LocalDate.of(2026, 11, 7)); form.setEndMileage(new BigDecimal("120.00")); return form;
    }

    private static FieldVehicleDeleteRequest deleteForm() {
        FieldVehicleDeleteRequest form = new FieldVehicleDeleteRequest(); form.setId("FV"); return form;
    }

    private static FieldVehicleFuelSaveRequest fuelForm() {
        FieldVehicleFuelSaveRequest form = new FieldVehicleFuelSaveRequest(); form.setMainId("FV");
        form.setAmount(new BigDecimal("200.00")); form.setFuelQuantity(new BigDecimal("27.000")); return form;
    }

    private static FieldVehicleFuel fuel(String id, String quantity, String amount) {
        FieldVehicleFuel fuel = new FieldVehicleFuel(); fuel.setId(id); fuel.setMainId("FV");
        fuel.setFuelQuantity(new BigDecimal(quantity)); fuel.setAmount(new BigDecimal(amount)); return fuel;
    }
}
