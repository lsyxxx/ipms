package com.abt.finance.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.finance.config.Constants;
import com.abt.finance.entity.ExpenseCategory;
import com.abt.finance.model.ExpenseCategoryRequestForm;
import com.abt.finance.model.ExpenseCategorySortItem;
import com.abt.finance.service.ExpenseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 费用分类维护
 */
@RestController
@Slf4j
@RequestMapping("/fi/expenseCategory")
@RequiredArgsConstructor
@Tag(name = "ExpenseCategoryController", description = "财务模块-费用分类")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    /**
     * 按ID查询费用分类
     */
    @GetMapping("/findById")
    @Operation(summary = "按ID查询费用分类")
    public R<ExpenseCategory> findById(@RequestParam String id) {
        return R.success(expenseCategoryService.findById(id), "查询成功");
    }

    /**
     * 分页查询费用分类，支持编号、名称搜索；默认返回全部（含启用与禁用）
     */
    @GetMapping("/findPage")
    @Operation(summary = "分页查询费用分类")
    public R<Page<ExpenseCategory>> findPage(@ModelAttribute ExpenseCategoryRequestForm form) {
        Page<ExpenseCategory> page = expenseCategoryService.findPage(form);
        return R.success(page, (int) page.getTotalElements(), "查询成功");
    }

    /**
     * 列表查询费用分类（不分页），支持编号、名称搜索；默认返回全部（含启用与禁用）
     */
    @GetMapping("/findList")
    @Operation(summary = "列表查询费用分类")
    public R<List<ExpenseCategory>> findList(@ModelAttribute ExpenseCategoryRequestForm form) {
        form.setNoPaging();
        List<ExpenseCategory> list = expenseCategoryService.findList(form);
        return R.success(list, list.size(), "查询成功");
    }

    /**
     * 新增或更新费用分类；新增时若未传 sortNo 则自动取当前最大排序号+1
     */
    @PostMapping("/save")
    @Operation(summary = "保存费用分类")
    public R<ExpenseCategory> save(@RequestBody @Validated({ValidateGroup.Save.class}) ExpenseCategory expenseCategory) {
        ExpenseCategory saved = expenseCategoryService.save(expenseCategory);
        return R.success(saved, "保存成功");
    }

    /**
     * 更新启用状态
     */
    @GetMapping("/update/enabled")
    @Operation(summary = "更新费用分类启用状态")
    public R<Object> updateEnabled(@RequestParam String id, @RequestParam Boolean enabled) {
        expenseCategoryService.updateEnabled(id, enabled);
        return R.success("状态更新成功");
    }

    /**
     * 批量调整排序号
     */
    @PostMapping("/update/sort")
    @Operation(summary = "批量调整费用分类排序")
    public R<Object> updateSort(@RequestBody @Validated List<ExpenseCategorySortItem> items) {
        expenseCategoryService.updateSortNo(items);
        return R.success("排序更新成功");
    }

    /**
     * 获取新增记录的推荐排序号
     */
    @GetMapping("/sortNo")
    @Operation(summary = "获取下一个排序号")
    public R<Integer> getNextSortNo() {
        return R.success(expenseCategoryService.getNextSortNo());
    }

    /**
     * 物理删除费用分类，仅管理员可用
     */
    @Secured(Constants.ROLE_FI_EXPENSE_CATEGORY_DEL)
    @GetMapping("/delete")
    @Operation(summary = "物理删除费用分类（管理员）")
    public R<Object> delete(@RequestParam String id) {
        expenseCategoryService.delete(id);
        return R.success("删除成功");
    }
}
