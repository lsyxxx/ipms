package com.abt.safety.controller;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.http.dto.WebApiToken;
import com.abt.safety.entity.*;
import com.abt.safety.event.SafetyRecordFinishedEvent;
import com.abt.safety.model.*;
import com.abt.safety.service.SafetyConfigService;
import com.abt.sys.model.entity.Role;
import com.abt.sys.model.entity.SystemFile;

import com.abt.safety.service.SafetyRecordService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserRole;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import com.abt.sys.util.WithQueryUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.abt.safety.Constants.*;

/**
 * 安全检查
 */
@RestController
@Slf4j
@RequestMapping("/safety")
@RequiredArgsConstructor
public class SafetyController {


    private final SafetyConfigService safetyConfigService;
    private final SafetyRecordService safetyRecordService;
    private final UserService<UserView, User> sqlServerUserService;
    private final ApplicationEventPublisher publisher;


    /**
     * 获取安全检查项目配置列表
     *
     * @return 分页结果
     */
    @GetMapping("/item/config/page")
    public R<Page<SafetyItem>> getSafetyItemConfigPage(@ModelAttribute SafeItemRequestForm requestForm) {
        Page<SafetyItem> page = safetyConfigService.getSafetyItemConfigPage(requestForm);
        WithQueryUtil.build(page);
        return R.success(page, "获取安全检查项目配置列表成功");
    }

    /**
     * 添加或更新安全检查项目配置
     *
     * @param safetyItem 安全检查项目配置
     * @return 操作结果
     */
    @PostMapping("/item/config/add")
    public R<SafetyItem> addSafetyItemConfig(@RequestBody SafetyItem safetyItem) {
        final Integer nextSortNo = safetyConfigService.getNextSortNo();
        safetyItem.setSortNo(nextSortNo);
        safetyConfigService.validateBeforAdd(safetyItem);
        SafetyItem savedItem = safetyConfigService.saveSafetyItemConfig(safetyItem);
        return R.success(savedItem, "保存成功");
    }

    /**
     * 更新安全检查项目状态
     *
     * @param id      检查项目ID
     * @param enabled 是否启用
     * @return 操作结果
     */
    @GetMapping("/item/config/update/enabled")
    @Transactional
    public R<Object> updateSafetyItemEnabled(
            @RequestParam String id,
            @RequestParam Boolean enabled) {

        safetyConfigService.updateSafetyItemEnabled(id, enabled);

        return R.success("状态更新成功");
    }

    /**
     * 删除安全检查项目配置
     *
     * @param id 检查项目ID
     * @return 操作结果
     */
    @GetMapping("/item/config/delete")
    @Transactional
    public R<Object> deleteSafetyItemConfig(@RequestParam String id) {
        safetyConfigService.logicDeleteSafetyItemConfig(id);
        return R.success("删除成功");
    }

    @GetMapping("/item/config/get")
    public R<SafetyItem> getSafetyItemConfig(@RequestParam String id) {
        SafetyItem safetyItem = safetyConfigService.getSafetyItem(id);
        WithQueryUtil.build(safetyItem);
        return R.success(safetyItem, "获取安全检查项目配置成功");
    }

    @GetMapping("/item/config/check/name")
    public R<Boolean> checkDuplicateName(@RequestParam String name, String checkType) {
        boolean isDuplicate = safetyConfigService.validateDuplicateSafetyItemName(name, CheckType.fromString(checkType));
        return R.success(isDuplicate, "检查项目名称是否已存在");
    }

    @GetMapping("/item/sortNo")
    public R<Integer> getNextSortNo() {
        final Integer nextSortNo = safetyConfigService.getNextSortNo();
        return R.success(nextSortNo);
    }

    @PostMapping("/form/save")
    public R<Object> saveSafetyForm(@RequestBody SafetyForm safetyForm) {
        final boolean exists = safetyConfigService.checkSafetyFormLocationExists(safetyForm.getLocation(), safetyForm.getId(), safetyForm.getLocationType(), safetyForm.getCheckType());
        if (exists) {
            throw new BusinessException("检查地点：" + safetyForm.getLocation() + "已存在，请重新设置");
        }
        safetyConfigService.saveForm(safetyForm);
        return R.success("保存表单成功!");
    }

    @GetMapping("/form/page")
    public R<Page<SafetyForm>> findFormPage(@ModelAttribute SafetyFormRequestForm requestForm) {
        final Page<SafetyForm> page = safetyConfigService.findByQueryPageable(requestForm);
        WithQueryUtil.build(page);
        return R.success(page, "查询成功");
    }

    @GetMapping("/form/check/location")
    public R<Boolean> checkSafetyFormLocation(String location, Long id, String locationType, CheckType checkType) {
        if (StringUtils.isBlank(location)) {
            throw new BusinessException("请输入地点类型");
        }
        LocationType lt = LocationType.fromString(locationType);
        final boolean exists = safetyConfigService.checkSafetyFormLocationExists(location, id, lt, checkType);
        return R.success(exists);
    }

    @GetMapping("/form/update/enabled")
    public R<Object> updateSafetyFormEnabled(String id, boolean enabled) {
        safetyConfigService.updateSafetyFormEnabled(id, enabled);
        return R.success("更新成功");
    }

    @GetMapping("/form/delete")
    public R<Object> logicDeleteSafetyForm(@RequestParam String id) {
        safetyConfigService.logicDeleteSafetyForm(id);
        return R.success("已删除安全表单");
    }

    @GetMapping("/form/load")
    public R<SafetyForm> loadSafetyFormWithItems(@RequestParam String id, boolean withItems) {
        if (withItems) {
            return R.success(WithQueryUtil.build(safetyConfigService.loadSafetyFormWithItems(id)), "读取成功");
        } else {
            return R.success(WithQueryUtil.build(safetyConfigService.loadSafetyForm(id)), "读取成功");
        }
    }


    @GetMapping("/record/validate")
    public R<Boolean> validateSafetyFormApply(Long formId) {
        LocalDate now = LocalDate.now();
//        final boolean exist = safetyRecordService.recordExist(now, formId);
//        if (exist) {
//            throw new BusinessException(String.format("%s 已存在安全检查记录，请勿重复提交。", now));
//        }
        return R.success("验证通过");
    }

    /**
     * 新增安全检查表单
     */
    @PostMapping("/record/check")
    public R<Object> saveFormRecord(@Validated @RequestBody SafetyForm safetyForm) {
//        LocalDate now = LocalDate.now();
//        final boolean exist = safetyRecordService.recordExist(now, safetyForm.getId());
        if (safetyForm.getId() == null) {
            throw new BusinessException("请选择检查地点并填写表单后提交");
        }
//        if (exist) {
//            throw new BusinessException(String.format("%s 已存在安全检查记录，请勿重复提交。", now));
//        }
        final SafetyRecord saved = safetyRecordService.saveCheck(safetyForm);
        //直接发布事件，controller中不做任何处理
        publisher.publishEvent(new SafetyRecordFinishedEvent(this, saved, null));
        return R.success("安全检查记录保存成功!");
    }

    /**
     * 获取检查详情
     *
     * @param id id
     */
    @GetMapping("/record/load")
    public R<SafetyRecord> loadFormRecord(String id, HttpSession session) {
        final SafetyRecord record = safetyRecordService.loadRecordOnly(id);
        final List<SafetyRectify> recList = safetyRecordService.findRectifyListByRecordId(id);
        record.setSafetyRectifyList(recList);
        WithQueryUtil.build(record);
        session.setAttribute("safetyRecord" + id, record);
        return R.success(record, "读取成功");
    }

    @GetMapping("/record/page")
    public R<Page<SafetyRecord>> findFormRecordPage(@ModelAttribute SafetyRecordRequestForm requestForm) {
        //权限
        final List<UserRole> list = sqlServerUserService.getUserByRoleId(ROLE_VIEW_LIST_ALL);
        String userid = TokenUtil.getUseridFromAuthToken();
        list.stream().filter(i -> i.getId().equals(userid)).findAny().ifPresentOrElse(i -> requestForm.setUserid(null),
                () -> requestForm.setUserid(userid));

        final Page<SafetyRecord> page = safetyRecordService.findSafetyRecordPageable(requestForm);
        WithQueryUtil.build(page);
        return R.success(page, "查询成功");
    }

    /**
     * 调度人分配，只影响safetyRecord
     * 1. 若没有正在进行的rectifyRecord，那么新建一个，整改人是rectifierId
     * 2. 若存在正在进行的rectifyRecord，不影响正在进行的safetyRectify， 仅修改safetyRecord中的。
     * @param id            record id
     * @param rectifierId   负责人userid
     * @param rectifierName 负责人姓名
     */
    @Secured(ROLE_DISPATCHER)
    @GetMapping("/record/dispatch")
    public R<Object> recordDispatch(String id, String rectifierId, String rectifierName) {
        final UserView dispatcher = TokenUtil.getUserFromAuthToken();
        SafetyRecord record = safetyRecordService.loadRecordOnly(id);
        record.setDispatcherId(dispatcher.getId());
        record.setDispatcherName(dispatcher.getName());
        record.setDispatchTime(LocalDateTime.now());
        safetyRecordService.dispatch(record, rectifierId, rectifierName);
        publisher.publishEvent(new SafetyRecordFinishedEvent(this, record, record.getCurrentRectify()));
        return R.success("已分配负责人:" + rectifierName);
    }

    @PostMapping("/record/rectify")
    public R<Object> recordRectify(@RequestBody SafetyRectify safetyRectify) {
        if (StringUtils.isBlank(safetyRectify.getRecordId())) {
            throw new MissingRequiredParameterException("检查记录id");
        }
        final SafetyRecord record = safetyRecordService.loadRecordOnly(safetyRectify.getRecordId());
        final SafetyRectify rectify = safetyRecordService.findRectifyById(safetyRectify.getId());
        rectify.setRectifyRemark(safetyRectify.getRectifyRemark());
        rectify.setRectifyFiles(safetyRectify.getRectifyFiles());
        safetyRecordService.rectify(rectify, record);
        publisher.publishEvent(new SafetyRecordFinishedEvent(this, record, record.getCurrentRectify()));
        return R.success("已整改");
    }

    @GetMapping("/record/delete")
    public R<Object> recordDelete(String id) {
        final SafetyRecord record = safetyRecordService.loadRecordOnly(id);
        UserView uv = TokenUtil.getUserFromAuthToken();
        // 仅能删除自己的
        if (!record.getCreateUserid().equals(uv.getId())) {
            throw new BusinessException(String.format("无法删除该记录(编号:%s)。仅能删除自己创建的检查记录", id));
        }
        safetyRecordService.logicDelete(id);
        return R.success("删除成功!");
    }

    /**
     * 整改复核/确认完成
     */
    @Secured(ROLE_RECTIFY_COMPLETE)
    @PostMapping("/record/rectify/complete")
    public R<Object> complete(@RequestBody SafetyRectify form) {
        if (StringUtils.isBlank(form.getRecordId())) {
            throw new MissingRequiredParameterException("检查记录id");
        }
        final SafetyRecord record = safetyRecordService.loadRecordOnly(form.getRecordId());
        final SafetyRectify saved = safetyRecordService.findRectifyById(form.getId());
        if (saved.isChecked()) {
            throw new BusinessException(String.format("整改记录(id=%s)已确认。确认人: %s, 确认时间: %s",
                    saved.getId(), saved.getCheckerName(), saved.getCheckTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }
        final UserView user = TokenUtil.getUserFromAuthToken();
        saved.setComment(form.getComment());
        saved.setCheckerId(user.getId());
        saved.setCheckerName(user.getName());
        if (RectifyResult.pass.equals(form.getCheckResult())) {
            safetyRecordService.rectifyPass(saved);
            safetyRecordService.complete(record);
        } else if (RectifyResult.reject.equals(form.getCheckResult())) {
            safetyRecordService.rectifyReject(saved);
            safetyRecordService.dispatch(record, form.getRectifierId(), form.getRectifierName());
            publisher.publishEvent(new SafetyRecordFinishedEvent(this, record, record.getCurrentRectify()));
        }
        return R.success("整改复核完成");
    }

    /**
     * 修改整改人
     * @param rectifyId 整改记录id
     * @param rectifierId 整改人id
     * @param rectifierName 整改人name
     */
    @GetMapping("/record/rectify/update")
    public R<Object> updateRectifier(int rectifyId, String rectifierId, String rectifierName) {
        final SafetyRectify rectify = safetyRecordService.findRectifyById(rectifyId);
        if (rectify.isChecked()) {
            throw new BusinessException(String.format("整改记录(id=%s)已确认。确认人: %s, 确认时间: %s",
                    rectify.getId(), rectify.getCheckerName(), rectify.getCheckTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }
        final UserView user = TokenUtil.getUserFromAuthToken();
        safetyRecordService.updateRectifyUser(rectifyId, rectifierId, rectifierName, user.getId(), user.getName());
        return R.success("修改整改人成功!");
    }

    /**
     * 当前用户是否有权限确认整改
     */
    @GetMapping("/record/rectify/checkrole")
    public R<Boolean> getRectifyCompleteRole() {
        final UserView uv = TokenUtil.getUserFromAuthToken();
        final Set<Role> authorities = uv.getAuthorities();
        final Optional<Role> any = authorities.stream().filter(i -> ROLE_RECTIFY_COMPLETE.equals(i.getId())).findAny();
        return R.success(any.isPresent());
    }

    /**
     * 下载图片
     * @param id recordId
     * @param fileId file id
     * @param type item(检查)/rectify(整改)
     * @param session session
     */
    @GetMapping("/record/image")
    public ResponseEntity<byte[]> downloadImage(String id, String fileId, String type, HttpSession session) throws IOException {
        SafetyRecord record = (SafetyRecord) session.getAttribute("safetyRecord" + id);
        if (record == null) {
            record = safetyRecordService.loadRecordOnly(id);
            final List<SafetyRectify> recList = safetyRecordService.findRectifyListByRecordId(id);
            record.setSafetyRectifyList(recList);
            WithQueryUtil.build(record);
            session.setAttribute("safetyRecord" + id, record);
        }
        String fullUrl = "";

        if ("item".equalsIgnoreCase(type)) {
            if (record.getCheckFormInstance() != null && record.getCheckFormInstance().getItems() != null) {
                for (SafetyFormItem item : record.getCheckFormInstance().getItems()) {
                    if (item.getFileList() != null) {
                        for (SystemFile file : item.getFileList()) {
                            if (file.getId().equals(fileId)) {
                                fullUrl = file.getUrl();
                                break;
                            }
                        }
                    }
                }
            }
        } else if ("rectify".equalsIgnoreCase(type)) {
            if (record.getSafetyRectifyList() != null && record.getSafetyRectifyList().size() > 0) {
                for (SafetyRectify rectify : record.getSafetyRectifyList()) {
                    if (rectify.getRectifyFiles() != null && rectify.getRectifyFiles().size() > 0) {
                        for (SystemFile file : rectify.getRectifyFiles()) {
                            if (file.getId().equals(fileId)) {
                                fullUrl = file.getUrl();
                                break;
                            }
                        }
                    }
                }
            }
        }

        Path path = Paths.get(fullUrl);
        byte[] imageBytes = Files.readAllBytes(path);

        // 设置HTTP响应头
        HttpHeaders headers = new HttpHeaders();

        String contentType = Files.probeContentType(path); // 自动判断 MIME 类型
        if (contentType == null) {
            contentType = "application/octet-stream"; // 兜底类型
        }
        headers.setContentType(MediaType.parseMediaType(contentType));

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    /**
     * 催办
     */
    @GetMapping("/record/remind")
    public R<Object> remind(String id) {
        final SafetyRecord record = safetyRecordService.loadRecordOnly(id);
        // 校验，是否允许催办
        if (record.getState().equals(RecordStatus.COMPLETED)) {
            throw new BusinessException(String.format("检查已结束(编号:%s), 无需催办", id));
        }
        final UserView user = TokenUtil.getUserFromAuthToken();

        // 判断催办人
        if (RecordStatus.SUBMITTED.equals(record.getState())) {
            // 1. 已提交检查，催办调度人
            final List<UserRole> dispatcherUsers = sqlServerUserService.getUserByRoleId(ROLE_DISPATCHER);
            for (UserRole dispatcher : dispatcherUsers) {
                String msg = record.getCheckType().getDescription() + "检查已完成, 请及时调度，催办人: " + user.getName();
                safetyRecordService.remind(dispatcher.getId(), dispatcher.getUsername(), user.getUsername(), record, msg);
            }
            return R.success("已催办调度人" + dispatcherUsers.size() + "人");

        } else if (RecordStatus.DISPATCHED.equals(record.getState())) {
            // 2. 已调度，催办整改负责人
            // 当前正在进行的整改
            final Optional<SafetyRectify> rectifyOptional = safetyRecordService.findRunningRectify(id);
            if (rectifyOptional.isPresent()) {
                final SafetyRectify rectify = rectifyOptional.get();
                safetyRecordService.remind(rectify.getRectifierId(), rectify.getRectifierName(), TokenUtil.getUseridFromAuthToken(), record, null);
                return R.success("已催办" + rectify.getRectifierName());
            } else {
                throw new BusinessException("无正在进行的整改(检查记录编号:" + id + ")");
            }
        }
        return R.success("无催办人");
    }

}