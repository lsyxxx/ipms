package com.abt.testing.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.testing.config.Constants;
import com.abt.testing.model.ReportCoverInfoRequest;
import com.abt.testing.model.ReportCoverInfoRequestForm;
import com.abt.testing.model.ReportCoverInfoVO;
import com.abt.testing.service.ReportCoverInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 报告封面基础信息维护接口，沿用系统统一认证。 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/report-cover-info")
@Tag(name = "ReportCoverInfoController", description = "报告封面信息维护")
public class ReportCoverInfoController {
    private final ReportCoverInfoService service;

    @GetMapping("/findById")
    @Operation(summary = "查询报告封面详情及关联检测项目名称")
    public R<ReportCoverInfoVO> findById(@RequestParam @NotBlank(message = "封面配置ID不能为空")
                                       @Size(max = 36, message = "封面配置ID不能超过36个字符") String id) {
        return R.success(service.findById(id), "查询成功");
    }

    @GetMapping("/findPage")
    @Operation(summary = "分页查询报告封面信息")
    public R<Page<ReportCoverInfoVO>> findPage(@Valid @ModelAttribute ReportCoverInfoRequestForm form) {
        Page<ReportCoverInfoVO> page = service.findPage(form);
        return R.success(page, (int) page.getTotalElements(), "查询成功");
    }

    @GetMapping("/findList")
    @Operation(summary = "查询报告封面信息列表")
    public R<List<ReportCoverInfoVO>> findList(@Valid @ModelAttribute ReportCoverInfoRequestForm form) {
        List<ReportCoverInfoVO> list = service.findList(form);
        return R.success(list, list.size(), "查询成功");
    }

    @PostMapping("/save")
    @Operation(summary = "新增报告封面配置")
    public R<ReportCoverInfoVO> save(@RequestBody @Validated({Default.class, ValidateGroup.Insert.class})
                                   ReportCoverInfoRequest request) {
        return R.success(service.save(request), "新增成功");
    }

    @PostMapping("/update")
    @Secured(Constants.ROLE_REPORT_COVER_EDIT)
    @Operation(summary = "更新报告封面配置")
    public R<ReportCoverInfoVO> update(@RequestBody @Validated({Default.class, ValidateGroup.Update.class})
                                     ReportCoverInfoRequest request) {
        return R.success(service.update(request), "更新成功");
    }

    @DeleteMapping("/delete")
    @Secured(Constants.ROLE_REPORT_COVER_DEL)
    @Operation(summary = "物理删除报告封面配置")
    public R<Object> delete(@RequestParam @NotBlank(message = "封面配置ID不能为空")
                            @Size(max = 36, message = "封面配置ID不能超过36个字符") String id) {
        service.delete(id);
        return R.success("删除成功");
    }
}
