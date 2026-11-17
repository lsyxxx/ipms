package com.abt.testing.service;

import com.abt.sys.exception.BusinessException;
import com.abt.testing.entity.ReportCoverInfo;
import com.abt.testing.model.*;
import com.abt.testing.repository.ReportCoverInfoRepository;
import com.abt.testing.repository.ReportCoverInfoWriteLockRepository;
import com.abt.testing.repository.TCheckmoduleRepository;
import com.abt.testing.service.impl.ReportCoverInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 无数据库业务测试：项目归属、批量名称回填、更新审计保护及物理删除。 */
class ReportCoverInfoServiceImplTest {
    private final ReportCoverInfoRepository repository = mock(ReportCoverInfoRepository.class);
    private final TCheckmoduleRepository modules = mock(TCheckmoduleRepository.class);
    private final ReportCoverInfoWriteLockRepository locks = mock(ReportCoverInfoWriteLockRepository.class);
    private final ReportCoverInfoServiceImpl service = new ReportCoverInfoServiceImpl(repository, modules, locks);

    @BeforeEach
    void setup() {
        when(locks.acquireWriteLock()).thenReturn(0);
        when(repository.findAll()).thenReturn(List.of());
        when(repository.saveAndFlush(any())).thenAnswer(call -> {
            ReportCoverInfo record = call.getArgument(0);
            if (record.getId() == null) record.setId("new-cover");
            return record;
        });
        when(modules.findCoverCheckModules(anyCollection())).thenAnswer(call -> {
            Collection<String> ids = call.getArgument(0);
            return ids.stream().map(id -> new ReportCoverCheckModuleVO(id, "名称-" + id)).toList();
        });
    }

    @Test
    void saveNormalizesNameAndIdsAndKeepsTextLists() {
        ReportCoverInfoRequest request = request("  封面名称  ", " M1 ", "M2", "M1");
        request.setItemCode("ABC");
        request.setSampleSpec("200目 0.7g");
        request.setCategory("化学");
        request.setInstruments(List.of("设备A（型号/说明）", "设备B"));
        request.setStandards(List.of("标准一 GB/T 1-2026", "标准二（旧版）"));
        ReportCoverInfoVO result = service.save(request);
        assertEquals("封面名称", result.getCoverName());
        assertEquals(List.of("M1", "M2"), result.getCheckModuleIds());
        assertEquals(List.of("名称-M1", "名称-M2"), result.getCheckModules().stream()
                .map(ReportCoverCheckModuleVO::getName).toList());
        assertEquals(request.getInstruments(), result.getInstruments());
        assertEquals(request.getStandards(), result.getStandards());
        assertEquals("ABC", result.getItemCode());
        assertEquals("200目 0.7g", result.getSampleSpec());
        assertEquals("化学", result.getCategory());
        var order = inOrder(locks, repository);
        order.verify(locks).acquireWriteLock();
        order.verify(repository).findByCoverName("封面名称");
        order.verify(repository).findAll();
        order.verify(repository).saveAndFlush(any());
    }

    @Test
    void saveAllowsUnassociatedCoverAndDefaultsEmptyLists() {
        ReportCoverInfoVO result = service.save(request("封面名称"));
        assertTrue(result.getCheckModuleIds().isEmpty());
        assertTrue(result.getCheckModules().isEmpty());
        assertTrue(result.getInstruments().isEmpty());
        assertTrue(result.getStandards().isEmpty());
        assertEquals(ReportCoverStatus.ENABLED.getCode(), result.getStatus());
        verifyNoInteractions(modules);
    }

    @Test
    void rejectsDuplicateNameAfterTrimming() {
        when(repository.findByCoverName("重复名称")).thenReturn(Optional.of(record("existing", "M1")));
        assertThrows(BusinessException.class, () -> service.save(request(" 重复名称 ")));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsProjectOccupiedByDisabledCover() {
        ReportCoverInfo occupied = record("occupied", "M1");
        occupied.setStatus(ReportCoverStatus.DISABLED.getCode());
        when(repository.findAll()).thenReturn(List.of(occupied));
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.save(request("新封面", "M1")));
        assertTrue(error.getMessage().contains("已关联其他封面"));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsMissingProjectAndInvalidStatus() {
        when(modules.findCoverCheckModules(anyCollection())).thenReturn(List.of());
        assertThrows(BusinessException.class, () -> service.save(request("封面", "missing")));
        ReportCoverInfoRequest request = request("封面");
        request.setStatus(2);
        assertThrows(BusinessException.class, () -> service.save(request));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void updateAllowsOwnNameAndProjectsAndPreservesAudit() {
        ReportCoverInfo existing = record("cover", "M1");
        LocalDateTime date = LocalDateTime.of(2026, 1, 1, 10, 0);
        existing.setCreateDate(date);
        existing.setCreateUserid("creator");
        existing.setCreateUsername("创建人");
        when(repository.findById("cover")).thenReturn(Optional.of(existing));
        when(repository.findByCoverName("封面")).thenReturn(Optional.of(existing));
        when(repository.findAll()).thenReturn(List.of(existing));
        ReportCoverInfoRequest request = request("封面", "M1", "M2");
        request.setId("cover");
        ReportCoverInfoVO result = service.update(request);
        assertEquals(List.of("M1", "M2"), result.getCheckModuleIds());
        assertEquals(date, result.getCreateDate());
        assertEquals("creator", result.getCreateUserid());
        assertEquals("创建人", result.getCreateUsername());
    }

    @Test
    void failedUpdateDoesNotChangeManagedEntityBeforeValidation() {
        ReportCoverInfo existing = record("cover", "M1");
        when(repository.findById("cover")).thenReturn(Optional.of(existing));
        when(repository.findByCoverName("其他名称")).thenReturn(Optional.of(record("other", "M2")));
        ReportCoverInfoRequest request = request("其他名称", "M2");
        request.setId("cover");
        assertThrows(BusinessException.class, () -> service.update(request));
        assertEquals("封面", existing.getCoverName());
        assertEquals(List.of("M1"), existing.getCheckModuleIds());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void updateCanReleaseAssociation() {
        ReportCoverInfo existing = record("cover", "M1");
        when(repository.findById("cover")).thenReturn(Optional.of(existing));
        ReportCoverInfoRequest request = request("封面");
        request.setId("cover");
        assertTrue(service.update(request).getCheckModuleIds().isEmpty());
        verifyNoInteractions(modules);
    }

    @Test
    void findPageBatchesDistinctIdsAndKeepsPagingAndMissingNames() {
        ReportCoverInfo a = record("A", "M1", "M2");
        ReportCoverInfo b = record("B", "M2", "deleted");
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(call -> new PageImpl<>(List.of(a, b), call.getArgument(1), 50));
        when(modules.findCoverCheckModules(anyCollection())).thenReturn(List.of(
                new ReportCoverCheckModuleVO("M1", "最新名称"), new ReportCoverCheckModuleVO("M2", "项目二")));
        ReportCoverInfoRequestForm form = new ReportCoverInfoRequestForm();
        form.setPage(2);
        form.setLimit(2);
        var result = service.findPage(form);
        assertEquals(50, result.getTotalElements());
        assertEquals(1, result.getNumber());
        assertEquals("最新名称", result.getContent().get(0).getCheckModules().get(0).getName());
        assertEquals("deleted", result.getContent().get(1).getCheckModules().get(1).getId());
        assertNull(result.getContent().get(1).getCheckModules().get(1).getName());
        verify(modules).findCoverCheckModules(List.of("M1", "M2", "deleted"));
    }

    @Test
    void moduleQueriesSplitAtOneThousandIds() {
        List<String> ids = IntStream.range(0, 2101).mapToObj(i -> "M" + i).toList();
        ReportCoverInfo record = record("cover");
        record.setCheckModuleIds(ids);
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(record));
        assertEquals(2101, service.findList(new ReportCoverInfoRequestForm()).get(0).getCheckModules().size());
        ArgumentCaptor<Collection<String>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(modules, times(3)).findCoverCheckModules(captor.capture());
        assertEquals(List.of(1000, 1000, 101), captor.getAllValues().stream().map(Collection::size).toList());
    }

    @Test
    void physicallyDeletesUnderSameWriteLock() {
        ReportCoverInfo existing = record("cover", "M1");
        when(repository.findById("cover")).thenReturn(Optional.of(existing));
        service.delete("cover");
        var order = inOrder(locks, repository);
        order.verify(locks).acquireWriteLock();
        order.verify(repository).findById("cover");
        order.verify(repository).delete(existing);
        order.verify(repository).flush();
    }

    @Test
    void lockFailurePreventsEveryWriteOperation() {
        when(locks.acquireWriteLock()).thenReturn(-1);
        assertThrows(BusinessException.class, () -> service.save(request("封面")));
        assertThrows(BusinessException.class, () -> service.update(request("封面")));
        assertThrows(BusinessException.class, () -> service.delete("cover"));
        verifyNoInteractions(repository, modules);
    }

    @Test
    void rejectsMissingRecordsAndInvalidPagination() {
        assertThrows(BusinessException.class, () -> service.findById(" "));
        assertThrows(BusinessException.class, () -> service.findById("missing"));
        assertThrows(BusinessException.class, () -> service.delete("missing"));
        ReportCoverInfoRequest request = request("封面");
        request.setId("missing");
        assertThrows(BusinessException.class, () -> service.update(request));
        ReportCoverInfoRequestForm form = new ReportCoverInfoRequestForm();
        form.setPage(0);
        assertThrows(BusinessException.class, () -> service.findPage(form));
    }

    private ReportCoverInfoRequest request(String name, String... ids) {
        ReportCoverInfoRequest request = new ReportCoverInfoRequest();
        request.setCoverName(name);
        request.setCheckModuleIds(Arrays.asList(ids));
        return request;
    }

    private ReportCoverInfo record(String id, String... ids) {
        ReportCoverInfo record = new ReportCoverInfo();
        record.setId(id);
        record.setCoverName("封面");
        record.setCheckModuleIds(Arrays.asList(ids));
        return record;
    }
}
