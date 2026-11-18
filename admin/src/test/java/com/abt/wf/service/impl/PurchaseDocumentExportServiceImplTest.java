package com.abt.wf.service.impl;

import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseDocumentExportRequest;
import com.abt.wf.model.PurchaseDocumentExportVO;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.zip.ZipFile;

import static com.abt.wf.model.PurchaseDocumentExportStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 无需数据库，验证整条异步导出链路与边界。 */
class PurchaseDocumentExportServiceImplTest {
    @TempDir Path temporary;
    private final PurchaseApplyMainRepository repository = mock(PurchaseApplyMainRepository.class);
    private final PurchaseService purchaseService = mock(PurchaseService.class);
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final List<Runnable> queue = new ArrayList<>();
    private PurchaseDocumentExportServiceImpl service;

    @BeforeEach
    void initialize() throws IOException {
        service = new PurchaseDocumentExportServiceImpl(repository, purchaseService, queue::add, mapper, temporary.toString(), "");
        service.initialize();
    }

    @Test
    void cancelsQueuedTaskBeforeAnyDatabaseWorkAndChecksOwner() {
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            var task = service.save(request("MY"));
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("other");
            assertThrows(AccessDeniedException.class, () -> service.cancel(task.getId()));
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            assertEquals(CANCELLED, service.cancel(task.getId()).getStatus());
            assertEquals(CANCELLED, service.cancel(task.getId()).getStatus());
            queue.remove(0).run();
            assertEquals(CANCELLED, service.findById(task.getId()).getStatus());
            assertThrows(BusinessException.class, () -> service.findZip(task.getId()));
            verifyNoInteractions(repository, purchaseService);
            assertDoesNotThrow(() -> service.save(request("MY")));
        }
    }

    @Test
    void runningCancellationFinishesCurrentPdfThenStopsAndRemovesPartialFiles() throws Exception {
        ids(List.of("1001", "1002"), false, "");
        when(repository.findDocumentExportBatch(anyList())).thenReturn(List.of(main("1001", true), main("1002", false)));
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            var task = service.save(request("MY"));
            when(purchaseService.createDocumentPdf(any(), anyString())).thenAnswer(invocation -> {
                Path work = Path.of(invocation.getArgument(1, String.class));
                Files.createDirectories(work);
                assertEquals(CANCELLING, service.cancel(task.getId()).getStatus());
                assertThrows(BusinessException.class, () -> service.save(request("MY")));
                assertEquals(CANCELLING, service.cancel(task.getId()).getStatus());
                return Files.writeString(work.resolve("document.pdf"), "current pdf").toFile();
            });
            queue.remove(0).run();
            assertEquals(CANCELLED, service.findById(task.getId()).getStatus());
            verify(purchaseService, times(1)).createDocumentPdf(any(), anyString());
            Path directory = temporary.resolve("purchase-document-exports").resolve(task.getId());
            assertFalse(Files.exists(directory.resolve("documents.zip.part")));
            assertFalse(Files.exists(directory.resolve("documents.zip")));
            assertFalse(Files.exists(directory.resolve("current")));
            assertFalse(Files.exists(directory.resolve("ids.txt")));
            assertThrows(BusinessException.class, () -> service.findZip(task.getId()));
            var restarted = new PurchaseDocumentExportServiceImpl(repository, purchaseService, queue::add, mapper,
                    temporary.toString(), "");
            restarted.initialize();
            assertEquals(CANCELLED, restarted.findById(task.getId()).getStatus());
        }
    }

    @Test
    void completedTaskCannotBeCancelledOrLoseItsZip() throws Exception {
        ids(List.of("1001"), false, "");
        when(repository.findDocumentExportBatch(anyList())).thenReturn(List.of());
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            var task = service.save(request("MY"));
            queue.remove(0).run();
            assertEquals(COMPLETED, service.cancel(task.getId()).getStatus());
            assertTrue(Files.isRegularFile(service.findZip(task.getId())));
        }
    }

    @Test
    void exportsMixedResultsAndKeepsUnacceptedPurchaseInZip() throws Exception {
        PurchaseApplyMain accepted = main("1001", true);
        PurchaseApplyMain unaccepted = main("1002", false);
        PurchaseApplyMain broken = main("1003", true);
        ids(List.of("1001", "1002", "1003", "1004"), false, "");
        when(repository.findDocumentExportBatch(anyList())).thenReturn(List.of(accepted, unaccepted, broken));
        when(purchaseService.createDocumentPdf(any(), anyString())).thenAnswer(invocation -> {
            PurchaseApplyMain main = invocation.getArgument(0);
            if (main.getId().equals("1003")) throw new IOException("内部敏感路径不应返回前端");
            Path directory = Path.of(invocation.getArgument(1, String.class));
            Files.createDirectories(directory);
            return Files.writeString(directory.resolve("document.pdf"), "PDF:" + main.getId()).toFile();
        });
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            PurchaseDocumentExportVO task = service.save(request("MY"));
            assertEquals(QUEUED, task.getStatus());
            queue.remove(0).run();
            PurchaseDocumentExportVO result = service.findById(task.getId());
            assertEquals(COMPLETED, result.getStatus());
            assertEquals(4, result.getTotal());
            assertEquals(4, result.getProcessed());
            assertEquals(2, result.getSuccess());
            assertEquals(2, result.getFailed());
            assertEquals(1, result.getWithoutAcceptance());
            try (ZipFile zip = new ZipFile(service.findZip(task.getId()).toFile(), StandardCharsets.UTF_8)) {
                assertNotNull(zip.getEntry("1001-740.00-张三-检测部.pdf"));
                assertNotNull(zip.getEntry("1002-740.00-张三-检测部.pdf"));
                assertNull(zip.getEntry("1003-740.00-张三-检测部.pdf"));
                String csv = new String(zip.getInputStream(zip.getEntry("导出结果清单.csv")).readAllBytes(), StandardCharsets.UTF_8);
                assertTrue(csv.contains("未验收，仅导出采购单"));
                assertTrue(csv.contains("单据已删除或不存在"));
                assertFalse(csv.contains("内部敏感路径"));
            }
            assertFalse(Files.exists(service.findZip(task.getId()).getParent().resolve("current")));
            assertFalse(Files.exists(service.findZip(task.getId()).getParent().resolve("ids.txt")));
            // 已完成文件与结果在服务重启后仍可使用。
            PurchaseDocumentExportServiceImpl restarted = new PurchaseDocumentExportServiceImpl(repository,
                    purchaseService, queue::add, mapper, temporary.toString(), "");
            restarted.initialize();
            assertEquals(COMPLETED, restarted.findById(task.getId()).getStatus());
            assertTrue(Files.isRegularFile(restarted.findZip(task.getId())));
        }
    }

    @Test
    void usesSearchAndAuthenticatedOwnerAndIgnoresPagination() {
        PurchaseDocumentExportRequest form = request("ALL");
        form.setQuery("试剂"); form.setState("已通过"); form.setDepts("dept1,dept2");
        form.setStartDate("2026-01-01"); form.setEndDate("2026-12-31");
        form.setPage(8); form.setLimit(1); form.setUserid("another-user");
        ids(List.of(), false, "");
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            PurchaseDocumentExportVO task = service.save(form);
            queue.remove(0).run();
            verify(repository).findDocumentExportIds(eq("ALL"), eq("owner"), eq("试剂"), eq("已通过"),
                    eq(false), eq(List.of("dept1", "dept2")), eq(java.time.LocalDate.of(2026, 1, 1).atStartOfDay()),
                    eq(java.time.LocalDate.of(2026, 12, 31).atStartOfDay()), any(), eq(""), eq(PageRequest.of(0, 100)));
            assertEquals(FAILED, service.findById(task.getId()).getStatus());
            assertEquals("没有符合搜索条件的采购单", service.findById(task.getId()).getMessage());
        }
    }

    @Test
    void scansMultipleBatchesWithoutLongInList() throws Exception {
        List<String> first = java.util.stream.IntStream.rangeClosed(1, 100).mapToObj(i -> String.format("%04d", i)).toList();
        ids(first, true, ""); ids(List.of("0101"), false, "0100");
        when(repository.findDocumentExportBatch(anyList())).thenReturn(List.of());
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            var task = service.save(request("MY")); queue.remove(0).run();
            assertEquals(101, service.findById(task.getId()).getTotal());
            verify(repository).findDocumentExportBatch(first);
            verify(repository).findDocumentExportBatch(List.of("0101"));
        }
    }

    @Test
    void ownerChecksDuplicateProtectionAndInterruptedRecovery() throws Exception {
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            var task = service.save(request("MY"));
            assertThrows(BusinessException.class, () -> service.save(request("ALL")));
            assertThrows(BusinessException.class, () -> service.findZip(task.getId()));
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("other");
            assertTrue(service.findList().isEmpty());
            assertThrows(AccessDeniedException.class, () -> service.findById(task.getId()));
            assertThrows(AccessDeniedException.class, () -> service.findZip(task.getId()));
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            PurchaseDocumentExportServiceImpl restarted = new PurchaseDocumentExportServiceImpl(repository,
                    purchaseService, queue::add, mapper, temporary.toString(), "");
            restarted.initialize();
            assertEquals(FAILED, restarted.findById(task.getId()).getStatus());
            assertTrue(restarted.findById(task.getId()).getMessage().contains("中断"));
        }
    }

    @Test
    void rejectsFullQueueWithoutRunningOnRequestThread() throws IOException {
        service = new PurchaseDocumentExportServiceImpl(repository, purchaseService,
                runnable -> { throw new RejectedExecutionException("full"); }, mapper, temporary.toString(), "");
        service.initialize();
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            assertThrows(BusinessException.class, () -> service.save(request("MY")));
            assertEquals(FAILED, service.findList().get(0).getStatus());
            verifyNoInteractions(repository);
        }
    }

    @Test
    void cleansOnlyExpiredTerminalTasks() throws Exception {
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            var task = service.save(request("MY"));
            Path directory = temporary.resolve("purchase-document-exports").resolve(task.getId());
            PurchaseDocumentExportServiceImpl.TaskData data = mapper.readValue(directory.resolve("task.json").toFile(),
                    PurchaseDocumentExportServiceImpl.TaskData.class);
            data.getResult().setStatus(COMPLETED); data.getResult().setExpiresAt(System.currentTimeMillis() - 1);
            mapper.writeValue(directory.resolve("task.json").toFile(), data);
            Path orphan = Files.createDirectory(temporary.resolve("purchase-document-exports")
                    .resolve("00000000-0000-0000-0000-000000000000"));
            Files.setLastModifiedTime(orphan, java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis()
                    - java.time.Duration.ofHours(25).toMillis()));
            PurchaseDocumentExportServiceImpl restarted = new PurchaseDocumentExportServiceImpl(repository,
                    purchaseService, queue::add, mapper, temporary.toString(), "");
            restarted.initialize();
            assertFalse(Files.exists(directory));
            assertFalse(Files.exists(orphan));
            assertTrue(restarted.findList().isEmpty());
        }
    }

    @Test
    void validatesDatesAndProtectsFilenameAndCsv() throws IOException {
        var form = request("MY"); form.setStartDate("2026-12-31"); form.setEndDate("2026-01-01");
        try (MockedStatic<TokenUtil> token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::getUseridFromAuthToken).thenReturn("owner");
            assertThrows(BusinessException.class, () -> service.save(form));
            form.setStartDate("bad-date");
            assertThrows(BusinessException.class, () -> service.save(form));
        }
        var main = main("123", false); main.setCreateUsername("张/三"); main.setCreateDeptName("检测:部");
        assertEquals("123-740.00-张_三-检测_部.pdf", PurchaseDocumentExportServiceImpl.documentFileName(main));
        StringWriter csv = new StringWriter();
        PurchaseDocumentExportServiceImpl.writeCsv(csv, "=1+1", "含\"引号,换行\n的内容");
        assertEquals("\"'=1+1\",\"含\"\"引号,换行\n的内容\"\r\n", csv.toString());
    }

    private void ids(List<String> content, boolean next, String after) {
        when(repository.findDocumentExportIds(anyString(), anyString(), nullable(String.class), nullable(String.class),
                anyBoolean(), anyList(), nullable(java.time.LocalDateTime.class), nullable(java.time.LocalDateTime.class),
                any(), eq(after), any())).thenReturn(new SliceImpl<>(content, PageRequest.of(0, 100), next));
    }

    private PurchaseDocumentExportRequest request(String scope) {
        var request = new PurchaseDocumentExportRequest(); request.setScope(scope); return request;
    }

    private PurchaseApplyMain main(String id, boolean accepted) {
        var main = new PurchaseApplyMain(); main.setId(id); main.setAccepted(accepted); main.setFinished(true);
        main.setCost(new BigDecimal("740.00")); main.setCreateUsername("张三"); main.setCreateDeptName("检测部");
        return main;
    }
}
