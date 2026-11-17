package com.abt.wf.service.impl;

import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseDocumentExportRequest;
import com.abt.wf.model.PurchaseDocumentExportVO;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.PurchaseDocumentExportService;
import com.abt.wf.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.abt.wf.model.PurchaseDocumentExportStatus.*;

/**
 * 采购单据导出：编号快照落盘、每批最多 100 个主表编号、逐单生成并写入 ZIP。
 * 任务元数据保存在专用目录，服务重启后可查看已完成结果；中断任务明确标记失败。
 */
@Service
@Slf4j
public class PurchaseDocumentExportServiceImpl implements PurchaseDocumentExportService {
    private static final int BATCH_SIZE = 100;
    private static final long RETENTION_MILLIS = Duration.ofHours(24).toMillis();
    private final PurchaseApplyMainRepository repository;
    private final PurchaseService purchaseService;
    private final Executor executor;
    private final ObjectMapper mapper;
    private final Path root;
    private final Map<String, TaskData> tasks = new ConcurrentHashMap<>();

    public PurchaseDocumentExportServiceImpl(PurchaseApplyMainRepository repository, PurchaseService purchaseService,
            @Qualifier("purchaseDocumentExportExecutor") Executor executor, ObjectMapper mapper,
            @Value("${abt.temp.dir}") String tempDir,
            @Value("${abt.pur.export.dir:}") String exportDir) {
        this.repository = repository;
        this.purchaseService = purchaseService;
        this.executor = executor;
        this.mapper = mapper;
        this.root = (StringUtils.isBlank(exportDir) ? Path.of(tempDir, "purchase-document-exports")
                : Path.of(exportDir)).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void initialize() throws IOException {
        Files.createDirectories(root);
        try (var directories = Files.list(root)) {
            for (Path directory : directories.filter(Files::isDirectory).toList()) {
                // 只处理自身创建的 UUID 目录，禁止跟随符号链接或扫描其他临时文件。
                if (!directory.getFileName().toString().matches("[a-f0-9-]{36}") || Files.isSymbolicLink(directory)) {
                    continue;
                }
                try {
                    TaskData task = mapper.readValue(directory.resolve("task.json").toFile(), TaskData.class);
                    if (!directory.getFileName().toString().equals(task.getResult().getId())) {
                        throw new IOException("任务编号与目录不一致");
                    }
                    if (!task.getResult().getStatus().isTerminal()) {
                        task.getResult().setStatus(FAILED);
                        task.getResult().setMessage("服务已重启，本次导出中断，请重新导出");
                        task.getResult().setExpiresAt(System.currentTimeMillis() + RETENTION_MILLIS);
                        persist(task);
                    }
                    tasks.put(task.getResult().getId(), task);
                    removeIntermediates(task);
                } catch (Exception e) {
                    log.error("恢复采购导出任务失败，目录={}", directory.getFileName(), e);
                }
            }
        }
        cleanExpired();
    }

    @Override
    public synchronized PurchaseDocumentExportVO save(PurchaseDocumentExportRequest request) {
        String owner = TokenUtil.getUseridFromAuthToken();
        validate(request);
        if (tasks.values().stream().anyMatch(task -> owner.equals(task.getOwnerId())
                && !snapshot(task).getStatus().isTerminal())) {
            throw new BusinessException("已有采购单据正在导出，请在导出任务中查看进度");
        }
        PurchaseDocumentExportRequest criteria = new PurchaseDocumentExportRequest();
        BeanUtils.copyProperties(request, criteria);
        // 身份取自当前登录用户，忽略客户端传入的 userid、deptIgnore、deptList。
        criteria.setUserid(owner);
        criteria.convertDeptList();
        TaskData task = new TaskData();
        task.setOwnerId(owner);
        task.setCriteria(criteria);
        task.setSnapshotTime(LocalDateTime.now());
        PurchaseDocumentExportVO result = new PurchaseDocumentExportVO();
        result.setId(UUID.randomUUID().toString());
        result.setScope(criteria.getScope());
        result.setStatus(QUEUED);
        result.setCreatedAt(System.currentTimeMillis());
        result.setFileName("采购单据-" + result.getId() + ".zip");
        result.setMessage("已提交，正在等待生成单据");
        task.setResult(result);
        try {
            Files.createDirectory(directory(task));
            persist(task);
            tasks.put(result.getId(), task);
            executor.execute(() -> run(task));
            log.info("创建采购单据导出任务，任务={}，范围={}", result.getId(), result.getScope());
        } catch (RejectedExecutionException e) {
            fail(task, "导出任务较多，请稍后重新导出", e);
            throw new BusinessException("导出任务较多，请稍后重新导出");
        } catch (IOException e) {
            log.error("创建采购导出任务目录失败", e);
            throw new BusinessException("无法创建导出任务，请联系管理员检查导出目录");
        }
        return snapshot(task);
    }

    private void validate(PurchaseDocumentExportRequest request) {
        if (request == null || !Set.of("MY", "TODO", "DONE", "ALL").contains(StringUtils.defaultString(request.getScope()))) {
            throw new BusinessException("采购单据导出范围不正确");
        }
        try {
            LocalDateTime start = TimeUtil.toLocalDateTime(request.getStartDate());
            LocalDateTime end = TimeUtil.toLocalDateTime(request.getEndDate());
            if (start != null && end != null && start.isAfter(end)) {
                throw new BusinessException("开始日期不能晚于结束日期");
            }
        } catch (DateTimeParseException e) {
            throw new BusinessException("日期格式不正确，请使用 yyyy-MM-dd");
        }
    }

    @Override
    public List<PurchaseDocumentExportVO> findList() {
        String owner = TokenUtil.getUseridFromAuthToken();
        return tasks.values().stream().filter(task -> owner.equals(task.getOwnerId()))
                .map(this::snapshot).filter(this::notExpired)
                .sorted(Comparator.comparingLong(PurchaseDocumentExportVO::getCreatedAt).reversed())
                .limit(50).toList();
    }

    @Override
    public PurchaseDocumentExportVO findById(String id) {
        return snapshot(ownedTask(id));
    }

    @Override
    public Path findZip(String id) throws IOException {
        TaskData task = ownedTask(id);
        if (snapshot(task).getStatus() != COMPLETED || !Files.isRegularFile(directory(task).resolve("documents.zip"))) {
            throw new BusinessException("导出文件尚未生成或已失效，请重新导出");
        }
        return directory(task).resolve("documents.zip");
    }

    private TaskData ownedTask(String id) {
        TaskData task = tasks.get(id);
        if (task == null || !notExpired(snapshot(task))) {
            throw new BusinessException("导出任务不存在或已过期，请重新导出");
        }
        if (!task.getOwnerId().equals(TokenUtil.getUseridFromAuthToken())) {
            throw new AccessDeniedException("只能查看或下载本人创建的导出任务");
        }
        return task;
    }

    private boolean notExpired(PurchaseDocumentExportVO result) {
        return result.getExpiresAt() == 0 || result.getExpiresAt() > System.currentTimeMillis();
    }

    private void run(TaskData task) {
        try {
            update(task, result -> { result.setStatus(QUERYING); result.setMessage("正在查询符合搜索条件的采购单"); });
            collectIds(task);
            if (snapshot(task).getTotal() == 0) {
                throw new BusinessException("没有符合搜索条件的采购单");
            }
            update(task, result -> { result.setStatus(GENERATING); result.setMessage("正在生成单据，请稍候"); });
            generateZip(task);
            long size = Files.size(directory(task).resolve("documents.zip"));
            update(task, result -> {
                result.setStatus(COMPLETED);
                result.setFileSize(size);
                result.setExpiresAt(System.currentTimeMillis() + RETENTION_MILLIS);
                result.setMessage("导出完成：共 " + result.getTotal() + " 张，成功 " + result.getSuccess()
                        + " 张，其中 " + result.getWithoutAcceptance() + " 张无验收记录；失败 " + result.getFailed() + " 张");
            });
            log.info("采购单据导出完成，任务={}，成功={}，失败={}", task.getResult().getId(),
                    task.getResult().getSuccess(), task.getResult().getFailed());
        } catch (Exception e) {
            fail(task, e instanceof BusinessException ? e.getMessage() : "导出失败，请重新导出或联系管理员", e);
        } finally {
            removeIntermediates(task);
        }
    }

    /** 扫描搜索结果的编号并落盘，生成阶段不再翻页查询变化中的列表。 */
    private void collectIds(TaskData task) throws IOException {
        PurchaseDocumentExportRequest form = task.getCriteria();
        List<String> departments = form.isDeptIgnore() ? List.of("") : form.getDeptList();
        String afterId = "";
        long total = 0;
        try (BufferedWriter writer = Files.newBufferedWriter(directory(task).resolve("ids.txt"), StandardCharsets.UTF_8)) {
            Slice<String> page;
            do {
                checkInterrupted();
                page = repository.findDocumentExportIds(form.getScope(), task.getOwnerId(), form.getQuery(), form.getState(),
                        form.isDeptIgnore(), departments, TimeUtil.toLocalDateTime(form.getStartDate()),
                        TimeUtil.toLocalDateTime(form.getEndDate()), task.getSnapshotTime(), afterId, PageRequest.of(0, BATCH_SIZE));
                for (String id : page.getContent()) {
                    writer.write(id);
                    writer.newLine();
                    afterId = id;
                    total++;
                }
                long count = total;
                update(task, result -> { result.setTotal(count); result.setMessage("正在查询采购单，已找到 " + count + " 张"); });
            } while (page.hasNext());
        }
    }

    /** 流式清单与 ZIP；每次只保留一个 PDF 的中间文件。 */
    private void generateZip(TaskData task) throws Exception {
        Path directory = directory(task);
        Path partial = directory.resolve("documents.zip.part");
        Path csv = directory.resolve("results.csv");
        try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(partial)), StandardCharsets.UTF_8);
             BufferedWriter report = Files.newBufferedWriter(csv, StandardCharsets.UTF_8);
             BufferedReader ids = Files.newBufferedReader(directory.resolve("ids.txt"), StandardCharsets.UTF_8)) {
            report.write('\ufeff');
            writeCsv(report, "单据编号", "总金额", "申请人", "部门", "导出结果", "验收记录", "说明", "文件名");
            List<String> batch = new ArrayList<>(BATCH_SIZE);
            String id;
            while ((id = ids.readLine()) != null) {
                batch.add(id);
                if (batch.size() == BATCH_SIZE) {
                    writeBatch(task, List.copyOf(batch), zip, report);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                writeBatch(task, List.copyOf(batch), zip, report);
            }
            report.flush();
            update(task, result -> { result.setStatus(PACKAGING); result.setMessage("单据生成完成，正在打包 ZIP"); });
            addFile(zip, csv, "导出结果清单.csv");
            PurchaseDocumentExportVO result = snapshot(task);
            zip.putNextEntry(new ZipEntry("导出说明.txt"));
            zip.write(("共 " + result.getTotal() + " 张，成功 " + result.getSuccess() + " 张，失败 " + result.getFailed()
                    + " 张。其中 " + result.getWithoutAcceptance() + " 张无验收记录，仅包含采购单。\r\n"
                    + "明细请查看导出结果清单.csv。文件生成后保留24小时。\r\n").getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        Files.move(partial, directory.resolve("documents.zip"), StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeBatch(TaskData task, List<String> ids, ZipOutputStream zip, BufferedWriter report) throws Exception {
        checkInterrupted();
        Map<String, PurchaseApplyMain> mains = repository.findDocumentExportBatch(ids).stream()
                .collect(Collectors.toMap(PurchaseApplyMain::getId, main -> main));
        for (String id : ids) {
            checkInterrupted();
            PurchaseApplyMain main = mains.get(id);
            Path work = directory(task).resolve("current");
            String filename = main == null ? "" : documentFileName(main);
            String error = null;
            Path pdf = null;
            try {
                if (main == null) {
                    throw new BusinessException("单据已删除或不存在");
                }
                pdf = purchaseService.createDocumentPdf(main, work.toString()).toPath();
            } catch (Exception e) {
                // 生成失败可以隔离；ZIP 写入失败必须终止，不能继续返回损坏的压缩包。
                log.error("生成采购单据失败，任务={}，单据={}", task.getResult().getId(), id, e);
                error = e instanceof BusinessException ? StringUtils.defaultIfBlank(e.getMessage(), "单据生成失败")
                        : "单据生成失败，请联系管理员检查模板、签名或数据";
            }
            try {
                boolean missing = main != null && (!main.isAccepted() || !main.isFinished());
                String reason = main == null ? "" : !main.isAccepted() ? "未验收，仅导出采购单"
                        : !main.isFinished() ? "流程未完成，仅导出采购单" : "";
                if (error == null) {
                    addFile(zip, pdf, filename);
                }
                writeCsv(report, id, main == null ? "" : amount(main), main == null ? "" : main.getCreateUsername(),
                        main == null ? "" : main.getCreateDeptName(), error == null ? "成功" : "失败",
                        missing ? "无验收记录" : main == null ? "" : "有验收记录", error == null ? reason : error,
                        error == null ? filename : "");
                synchronized (task) {
                    PurchaseDocumentExportVO result = task.getResult();
                    result.setProcessed(result.getProcessed() + 1);
                    if (error == null) {
                        result.setSuccess(result.getSuccess() + 1);
                        if (missing) result.setWithoutAcceptance(result.getWithoutAcceptance() + 1);
                    } else {
                        result.setFailed(result.getFailed() + 1);
                    }
                }
            } finally {
                deleteDirectory(work);
            }
        }
        update(task, result -> result.setMessage("正在生成单据：" + result.getProcessed() + " / " + result.getTotal()));
    }

    static String documentFileName(PurchaseApplyMain main) {
        return safeName(main.getId()) + "-" + amount(main) + "-" + safeName(main.getCreateUsername())
                + "-" + safeName(main.getCreateDeptName()) + ".pdf";
    }

    private static String amount(PurchaseApplyMain main) {
        return Objects.requireNonNullElse(main.getCost(), BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String safeName(String value) {
        String name = StringUtils.defaultIfBlank(value, "未填写").replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_")
                .replaceAll("[. ]+$", "");
        return StringUtils.defaultIfBlank(name, "未填写").substring(0, Math.min(name.length(), 60));
    }

    static void writeCsv(Writer writer, String... values) throws IOException {
        List<String> cells = new ArrayList<>();
        for (String value : values) {
            String text = StringUtils.defaultString(value);
            if (text.matches("(?s)^[\\s]*[=+@-].*")) text = "'" + text;
            cells.add("\"" + text.replace("\"", "\"\"") + "\"");
        }
        writer.write(String.join(",", cells) + "\r\n");
    }

    private void addFile(ZipOutputStream zip, Path file, String name) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        Files.copy(file, zip);
        zip.closeEntry();
    }

    private void checkInterrupted() throws InterruptedIOException {
        if (Thread.currentThread().isInterrupted()) throw new InterruptedIOException("导出任务已中断");
    }

    private PurchaseDocumentExportVO snapshot(TaskData task) {
        synchronized (task) {
            PurchaseDocumentExportVO result = new PurchaseDocumentExportVO();
            BeanUtils.copyProperties(task.getResult(), result);
            return result;
        }
    }

    private void update(TaskData task, Consumer<PurchaseDocumentExportVO> change) throws IOException {
        synchronized (task) {
            change.accept(task.getResult());
            persist(task);
        }
    }

    private void fail(TaskData task, String message, Exception exception) {
        log.error("采购单据导出任务失败，任务={}", task.getResult().getId(), exception);
        try {
            update(task, result -> {
                result.setStatus(FAILED);
                result.setMessage(message);
                result.setExpiresAt(System.currentTimeMillis() + RETENTION_MILLIS);
            });
        } catch (IOException e) {
            log.error("保存采购导出失败状态失败，任务={}", task.getResult().getId(), e);
        }
    }

    private void persist(TaskData task) throws IOException {
        Path temporary = directory(task).resolve("task.json.tmp");
        mapper.writeValue(temporary.toFile(), task);
        try {
            Files.move(temporary, directory(task).resolve("task.json"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporary, directory(task).resolve("task.json"), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Path directory(TaskData task) {
        return root.resolve(task.getResult().getId());
    }

    private void removeIntermediates(TaskData task) {
        deleteDirectory(directory(task).resolve("current"));
        for (String file : List.of("ids.txt", "results.csv", "documents.zip.part")) {
            try {
                Files.deleteIfExists(directory(task).resolve(file));
            } catch (IOException e) {
                log.error("清理采购导出中间文件失败，任务={}", task.getResult().getId(), e);
            }
        }
    }

    /** 已结束任务与 ZIP 保留 24 小时，执行中的任务不按创建时间删除。 */
    @Scheduled(initialDelay = 60000, fixedDelay = 3600000)
    public void cleanExpired() {
        tasks.forEach((id, task) -> {
            synchronized (task) {
                if (task.getResult().getStatus().isTerminal() && !notExpired(task.getResult())) {
                    if (deleteDirectory(directory(task))) tasks.remove(id, task);
                }
            }
        });
        // 首次保存元数据失败或元数据损坏的目录也要回收，避免长期遗留文件。
        try (var directories = Files.list(root)) {
            for (Path directory : directories.filter(Files::isDirectory).toList()) {
                String id = directory.getFileName().toString();
                if (id.matches("[a-f0-9-]{36}") && !tasks.containsKey(id) && !Files.isSymbolicLink(directory)
                        && Files.getLastModifiedTime(directory).toMillis() < System.currentTimeMillis() - RETENTION_MILLIS) {
                    deleteDirectory(directory);
                }
            }
        } catch (IOException e) {
            log.error("清理采购导出遗留目录失败", e);
        }
    }

    private boolean deleteDirectory(Path directory) {
        Path target = directory.toAbsolutePath().normalize();
        if (target.equals(root) || !target.startsWith(root) || Files.isSymbolicLink(target)) {
            throw new IllegalArgumentException("非法的采购导出清理目录");
        }
        try {
            FileUtils.deleteDirectory(target.toFile());
            return true;
        } catch (IOException e) {
            log.error("清理采购导出目录失败，目录={}", target.getFileName(), e);
            return false;
        }
    }

    /** 仅用于本机任务元数据持久化，不作为接口返回对象。 */
    @Data
    public static class TaskData {
        private String ownerId;
        private PurchaseDocumentExportRequest criteria;
        private LocalDateTime snapshotTime;
        private PurchaseDocumentExportVO result;
    }
}
