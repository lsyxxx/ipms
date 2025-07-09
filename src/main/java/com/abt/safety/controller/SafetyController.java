package com.abt.safety.controller;

import com.abt.common.model.R;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.http.dto.WebApiToken;
import com.abt.safety.entity.SafetyForm;
import com.abt.safety.entity.SafetyItem;
import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.entity.SafetyFormItem;
import com.abt.safety.event.SafetyRecordFinishedEvent;
import com.abt.safety.model.*;
import com.abt.safety.service.SafetyConfigService;
import com.abt.sys.model.entity.SystemFile;

import com.abt.safety.service.SafetyRecordService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserRole;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.service.UserService;
import com.abt.sys.util.WithQueryUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.abt.safety.Constants.ROLE_DISPATCHER;
import static com.abt.safety.Constants.ROLE_VIEW_LIST_ALL;

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
    private final UserService<UserView, WebApiToken> userService;


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
        final boolean exists = safetyConfigService.checkSafetyFormLocationExists(safetyForm.getLocation(), safetyForm.getId(), safetyForm.getLocationType());
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
    public R<Boolean> checkSafetyFormLocation(String location, Long id, String locationType) {
        if (StringUtils.isBlank(location)) {
            throw new BusinessException("请输入地点类型");
        }
        LocationType lt = LocationType.fromString(locationType);
        final boolean exists = safetyConfigService.checkSafetyFormLocationExists(location, id, lt);
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
        publisher.publishEvent(new SafetyRecordFinishedEvent(this, saved));
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
     * 调度人分配
     *
     * @param id            record id
     * @param rectifierId   负责人userid
     * @param rectifierName 负责人姓名
     */
    @Secured(ROLE_DISPATCHER)
    @GetMapping("/record/dispatch")
    public R<Object> recordDispatch(String id, String rectifierId, String rectifierName) {
        final UserView dispatcher = TokenUtil.getUserFromAuthToken();
        final SafetyRecord record = safetyRecordService.dispatch(id, dispatcher.getId(), dispatcher.getName(), rectifierId, rectifierName);
        publisher.publishEvent(new SafetyRecordFinishedEvent(this, record));
        return R.success("已分配负责人:" + rectifierName);
    }

    @PostMapping("/record/rectify")
    public R<Object> recordRectify(@RequestBody RectifyRequest rectifyRequest) {
        final SafetyRecord record = safetyRecordService.rectified(rectifyRequest.getId(), rectifyRequest.getRectifyRemark(), rectifyRequest.getFiles());
        publisher.publishEvent(new SafetyRecordFinishedEvent(this, record));
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
            if (record.getRectifyFiles() != null && record.getRectifyFiles().size() > 0) {
                for (SystemFile file : record.getRectifyFiles()) {
                    if (file.getId().equals(fileId)) {
                        fullUrl = file.getUrl();
                        break;
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

}