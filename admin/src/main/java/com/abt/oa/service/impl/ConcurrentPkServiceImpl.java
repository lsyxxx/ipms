package com.abt.oa.service.impl;

import com.abt.oa.reposity.PkNumberRepository;
import com.abt.oa.reposity.PkNumberRepository.Counter;
import com.abt.oa.reposity.PkNumberRepository.Rule;
import com.abt.oa.service.ConcurrentPkService;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** 对应 C# BLLConcurrentPK 的 Java 独立入口，静态锁覆盖取号事务提交或回滚。 */
@Service
@Slf4j
public class ConcurrentPkServiceImpl implements ConcurrentPkService {
    private static final Object NUMBER_LOCK = new Object();
    private final PkNumberRepository numbers;
    private final TransactionTemplate transactions;

    public ConcurrentPkServiceImpl(PkNumberRepository numbers, PlatformTransactionManager manager) {
        this.numbers = numbers;
        transactions = new TransactionTemplate(manager);
        transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactions.setTimeout(30);
    }

    @Override
    public String nextNumber(String ruleName) {
        if (ruleName == null || ruleName.isBlank()) throw new BusinessException("编号规则名称不能为空");
        synchronized (NUMBER_LOCK) {
            try {
                return transactions.execute(status -> allocate(ruleName.trim().toUpperCase(Locale.ROOT),
                        LocalDateTime.now(ZoneId.of("Asia/Shanghai"))));
            } catch (RuntimeException e) {
                log.error("编号分配失败 rule={}", ruleName, e);
                if (e instanceof BusinessException) throw e;
                throw new BusinessException("编号流水写入或提交失败，本次保存已终止");
            }
        }
    }

    /** 包内时间参数便于验证跨年，运行入口统一采用北京时间。 */
    String allocate(String name, LocalDateTime now) {
        List<Rule> rules = numbers.findRules(name);
        if (rules.size() != 1) throw new BusinessException("编号规则必须且只能配置一条：" + name);
        Rule rule = rules.get(0);
        boolean usePrefix = enabled(rule.usePrefix()), useDate = enabled(rule.useDate()), useServer = enabled(rule.useServer());
        if ("1".equals(rule.useGuid())) throw new BusinessException("流水取号不支持 GUID 规则");
        if (rule.length() == null || rule.length().compareTo(BigDecimal.ONE) < 0 ||
                rule.length().compareTo(BigDecimal.valueOf(128)) > 0 || rule.length().stripTrailingZeros().scale() > 0)
            throw new BusinessException("流水号长度必须为1到128的整数");
        String prefix = usePrefix && rule.prefix() != null ? rule.prefix() : "";
        String date = now.format(DateTimeFormatter.ofPattern("1".equals(rule.monthDay()) ? "yyyyMMdd" : "yyyy"));
        String server = "";
        if (useServer) {
            List<String> servers = numbers.findCurrentServers();
            if (servers.size() != 1 || servers.get(0) == null || servers.get(0).isBlank())
                throw new BusinessException("必须且只能设置一个有效的当前服务器节点");
            server = servers.get(0);
        }
        List<Counter> counters = numbers.findCounters(name, usePrefix ? prefix : null, useDate ? date : null, useServer ? server : null);
        if (counters.size() > 1) throw new BusinessException("存在重复流水记录，请先核实：" + name);
        Counter previous = counters.isEmpty() ? null : counters.get(0);
        if (previous != null && (previous.number() == null || previous.number().signum() < 0 || previous.number().stripTrailingZeros().scale() > 0))
            throw new BusinessException("当前流水必须为非负整数");
        BigDecimal next = previous == null ? BigDecimal.ONE : previous.number().add(BigDecimal.ONE);
        String id = previous == null ? UUID.randomUUID().toString().replace("-", "") : previous.id();
        String rowId = UUID.randomUUID().toString().replace("-", "");
        int affected = previous == null ? numbers.insert(id, name, prefix, date, server, next, rowId, now)
                : numbers.update(previous, next, rowId, now);
        if (affected != 1 || !numbers.matches(id, next, rowId))
            throw new BusinessException("编号流水写入失败，本次保存已终止");
        String serial = next.toBigIntegerExact().toString();
        serial = "0".repeat(Math.max(0, rule.length().intValueExact() - serial.length())) + serial;
        return prefix + server + (useDate ? date : "") + serial;
    }

    private boolean enabled(String flag) {
        if ("1".equals(flag)) return true;
        if ("0".equals(flag)) return false;
        throw new BusinessException("编号规则开关必须为0或1");
    }
}
