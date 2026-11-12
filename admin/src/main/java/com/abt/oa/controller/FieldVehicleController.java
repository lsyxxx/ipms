package com.abt.oa.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.oa.model.*;
import com.abt.oa.service.FieldVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 野外用车入口；权限、车辆占用及结算由 Service 实现。 */
@RestController
@RequestMapping("/field-vehicle")
@RequiredArgsConstructor
@Validated
@Tag(name = "野外用车")
public class FieldVehicleController {
    private final FieldVehicleService service;

    @GetMapping("/findPage")
    @Operation(summary = "野外用车分页，含草稿")
    public R<Page<FieldVehicleListVO>> findPage(@Valid @ModelAttribute FieldVehicleRequestForm form) {
        return R.success(service.findPage(form));
    }

    @GetMapping("/my/findPage")
    @Operation(summary = "本人创建（派车）或办理还车的记录分页，含草稿")
    public R<Page<FieldVehicleListVO>> findMyPage(@Valid @ModelAttribute FieldVehicleRequestForm form) {
        return R.success(service.findMyPage(form));
    }

    @GetMapping("/findById")
    @Operation(summary = "详情与还车预览，返回主表实时维护的加油合计")
    public R<FieldVehicleDetailVO> findById(
            @RequestParam @NotBlank(message = "用车记录标识不能为空") @Size(max = 50, message = "记录标识不能超过50个字符") String id) {
        return R.success(service.findById(id));
    }

    @GetMapping("/car/findList")
    @Operation(summary = "按启用状态查询车辆，返回使用状态与起始里程提示")
    public R<List<FieldVehicleCarOptionVO>> findList(@Valid @ModelAttribute CarOptionRequestForm form) {
        return R.success(service.findList(form));
    }

    @PostMapping("/draft/save")
    @Operation(summary = "保存草稿，不占车，不校验正式必填项")
    public R<FieldVehicleDetailVO> saveDraft(@RequestBody @Validated({Default.class, ValidateGroup.Temp.class}) FieldVehicleSaveRequest form) {
        return R.success(service.saveDraft(form));
    }

    @PostMapping("/delete")
    @Operation(summary = "物理删除本人创建的草稿，其他状态不允许删除")
    public R<Void> delete(@RequestBody @Valid FieldVehicleDeleteRequest form) {
        service.delete(form);
        return R.success("删除成功");
    }

    @PostMapping("/save")
    @Operation(summary = "正式派车或将草稿提交，占用车辆")
    public R<FieldVehicleDetailVO> save(@RequestBody @Validated({Default.class, ValidateGroup.Apply.class}) FieldVehicleSaveRequest form) {
        return R.success(service.save(form));
    }

    @PostMapping("/update")
    @Operation(summary = "修改使用中的人员、用途、出发地和目的地")
    public R<FieldVehicleDetailVO> update(@RequestBody @Validated({Default.class, ValidateGroup.Apply.class}) FieldVehicleUpdateRequest form) {
        return R.success(service.update(form));
    }

    @PostMapping("/return/update")
    @Operation(summary = "还车，后端重算加油汇总并冻结记录")
    public R<FieldVehicleDetailVO> updateReturn(@RequestBody @Valid FieldVehicleReturnRequest form) {
        return R.success(service.updateReturn(form));
    }

    @PostMapping("/fuel/save")
    @Operation(summary = "新增加油明细")
    public R<FieldVehicleFuelVO> saveFuel(@RequestBody @Valid FieldVehicleFuelSaveRequest form) {
        return R.success(service.saveFuel(form));
    }

    @PostMapping("/fuel/update")
    @Operation(summary = "修改加油明细")
    public R<FieldVehicleFuelVO> updateFuel(@RequestBody @Valid FieldVehicleFuelUpdateRequest form) {
        return R.success(service.updateFuel(form));
    }

    @PostMapping("/fuel/delete")
    @Operation(summary = "删除加油明细")
    public R<Void> deleteFuel(@RequestBody @Valid FieldVehicleFuelDeleteRequest form) {
        service.deleteFuel(form);
        return R.success("删除成功");
    }
}
