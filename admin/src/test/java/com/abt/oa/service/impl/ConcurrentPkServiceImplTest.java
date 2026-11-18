package com.abt.oa.service.impl;

import com.abt.oa.reposity.PkNumberRepository;
import com.abt.oa.reposity.PkNumberRepository.Counter;
import com.abt.oa.reposity.PkNumberRepository.Rule;
import com.abt.sys.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** 隔离验证单进程并发、编号规则、写入校验及提交失败，不连接业务库。 */
class ConcurrentPkServiceImplTest {
    private final PkNumberRepository repository = mock(PkNumberRepository.class);
    private final PlatformTransactionManager manager = mock(PlatformTransactionManager.class);
    private final Map<String, Counter> counters = new HashMap<>();
    private ConcurrentPkServiceImpl service;
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 18, 12, 0);

    @BeforeEach
    void setup() {
        when(manager.getTransaction(any())).thenAnswer(call -> new SimpleTransactionStatus());
        when(repository.findRules("YWYC_ORDERNO")).thenReturn(List.of(
                new Rule("YWYC", "1", "1", "0", "0", BigDecimal.valueOf(4), "0")));
        when(repository.findCounters(anyString(), any(), any(), any())).thenAnswer(call -> {
            Counter value = counters.get(call.<String>getArgument(2));
            return value == null ? List.of() : List.of(value);
        });
        when(repository.insert(anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString(), any()))
                .thenAnswer(call -> {
                    counters.put(call.getArgument(3), new Counter(call.getArgument(0), call.getArgument(5), call.getArgument(6)));
                    return 1;
                });
        when(repository.update(any(), any(), anyString(), any())).thenAnswer(call -> {
            Counter previous = call.getArgument(0);
            String year = counters.entrySet().stream().filter(e -> e.getValue().id().equals(previous.id())).findFirst().orElseThrow().getKey();
            counters.put(year, new Counter(previous.id(), call.getArgument(1), call.getArgument(2)));
            return 1;
        });
        when(repository.matches(anyString(), any(), anyString())).thenReturn(true);
        service = new ConcurrentPkServiceImpl(repository, manager);
    }

    @Test
    void parallelFirstAllocationIsUniqueAndSequential() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Callable<String>> tasks = new ArrayList<>();
            for (int i = 0; i < 48; i++) tasks.add(() -> service.nextNumber("ywyc_orderno"));
            Set<String> values = new HashSet<>();
            for (Future<String> future : pool.invokeAll(tasks)) values.add(future.get(10, TimeUnit.SECONDS));
            assertEquals(48, values.size());
            Counter latest = counters.values().iterator().next();
            assertEquals(0, BigDecimal.valueOf(48).compareTo(latest.number()));
            verify(manager, times(48)).commit(any());
        } finally { pool.shutdownNow(); }
    }

    @Test
    void annualCountersAndMoreThanFourDigitsArePreserved() {
        assertEquals("YWYC20260001", service.allocate("YWYC_ORDERNO", NOW));
        counters.put("2026", new Counter("C", BigDecimal.valueOf(9999), "old"));
        assertEquals("YWYC202610000", service.allocate("YWYC_ORDERNO", NOW));
        assertEquals("YWYC20270001", service.allocate("YWYC_ORDERNO", NOW.plusYears(1)));
        assertEquals("YWYC202610001", service.allocate("YWYC_ORDERNO", NOW));
    }

    @Test
    void insertExceptionAndCommitExceptionNeverReturnNumber() {
        when(repository.insert(anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString(), any()))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("write failed"));
        assertThrows(BusinessException.class, () -> service.nextNumber("YWYC_ORDERNO"));
        verify(manager).rollback(any()); verify(manager, never()).commit(any());
        reset(repository);
        when(repository.findRules(anyString())).thenReturn(List.of(new Rule("YWYC", "1", "1", "0", "0", BigDecimal.valueOf(4), "0")));
        when(repository.findCounters(anyString(), any(), any(), any())).thenReturn(List.of());
        when(repository.insert(anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString(), any())).thenReturn(1);
        when(repository.matches(anyString(), any(), anyString())).thenReturn(true);
        doThrow(new org.springframework.transaction.TransactionSystemException("commit failed")).when(manager).commit(any());
        assertThrows(BusinessException.class, () -> service.nextNumber("YWYC_ORDERNO"));
    }

    @Test
    void zeroRowsAndSilentWriteAreRejected() {
        when(repository.insert(anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString(), any())).thenReturn(0);
        assertThrows(BusinessException.class, () -> service.nextNumber("YWYC_ORDERNO"));
        verify(repository, never()).matches(anyString(), any(), anyString());
        when(repository.insert(anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString(), any())).thenReturn(1);
        when(repository.matches(anyString(), any(), anyString())).thenReturn(false);
        assertThrows(BusinessException.class, () -> service.nextNumber("YWYC_ORDERNO"));
        verify(manager, times(2)).rollback(any());
        verify(manager, never()).commit(any());
    }

    @Test
    void duplicateCountersAndMissingRuleFailBeforeWriting() {
        assertThrows(BusinessException.class, () -> service.nextNumber("MISSING"));
        when(repository.findCounters(anyString(), any(), any(), any())).thenReturn(List.of(
                new Counter("A", BigDecimal.ONE, "a"), new Counter("B", BigDecimal.ONE, "b")));
        assertThrows(BusinessException.class, () -> service.nextNumber("YWYC_ORDERNO"));
        verify(repository, never()).insert(anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString(), any());
        verify(repository, never()).update(any(), any(), anyString(), any());
    }
}
