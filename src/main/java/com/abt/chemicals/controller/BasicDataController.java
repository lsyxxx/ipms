package com.abt.chemicals.controller;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.entity.Company;
import com.abt.chemicals.service.BasicDataService;
import com.abt.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/test/chm")
public class BasicDataController {

    private final BasicDataService basicDataService;

    public BasicDataController(BasicDataService basicDataService) {
        this.basicDataService = basicDataService;
    }

    /**
     * 查询分类
     * @param word: 搜索内容
     * @param level: 搜索类型层级1/2
     * @param type1Id: 搜索类型层级1的id(没有则搜索所有)
     * TODO: 高级搜索
     */
    @GetMapping("/type/search")
    public R<List<ChemicalType>> queryType(@RequestParam(required = false) String word, @RequestParam int level,
                                           @RequestParam(required = false) String type1Id) {
        final List<ChemicalType> list = basicDataService.queryType(word, level, type1Id);
        return R.success(list, list.size());
    }

    @GetMapping("/type/all")
    public R<List<ChemicalType>> queryAllType(@RequestParam(required = false) boolean enabled) {
        final List<ChemicalType> list;
        if (enabled) {
            list = basicDataService.queryAllTypesEnabled();
        } else {
            list = basicDataService.queryAllTypes();
        }
        return R.success(list, list.size());
    }

    @GetMapping("/type/del")
    public R<Long> deleteType(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            return R.fail("类型Id不允许为空");
        }
        final long deleted = basicDataService.deleteType(id);
        return R.success(deleted);
    }

    /**
     * 编辑/新增化学品类型
     * @param typeForm 表单
     */
    @PostMapping("/type/edit")
    public R<ChemicalType> editType(@RequestBody ChemicalType typeForm) {
        final ChemicalType type = basicDataService.editType(typeForm);
        return R.success(type);
    }

    @GetMapping("/com/all")
    public R<List<Company>> queryAllCompanyByType(@RequestParam String type) {
        final List<Company> companies = basicDataService.queryAllCompanyByType(type);
        return R.success(companies, companies.size());
    }

    @GetMapping("/com/search")
    public R<List<Company>> queryCompany(@RequestParam String type, @RequestParam String name) {
        final List<Company> list = basicDataService.queryCompany(type, name);
        return R.success(list, list.size());
    }

    @PostMapping("/com/edit")
    public R<Company> editCompany(@RequestBody Company form) {
        final Company company = basicDataService.editCompany(form);
        return R.success(company);
    }

    @PostMapping("/com/add")
    public R<Company> addCompany(@RequestBody @Validated(ValidateGroup.Insert.class) Company form) {
        final Company company = basicDataService.editCompany(form);
        return R.success(company);
    }

    @GetMapping("/com/del")
    public R deleteCompany(@RequestParam String id) {
        basicDataService.deleteCompany(id);
        return R.success();
    }



}
