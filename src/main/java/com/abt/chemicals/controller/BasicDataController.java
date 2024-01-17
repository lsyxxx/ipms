package com.abt.chemicals.controller;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.service.BasicDataService;
import com.abt.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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
    public R<List<ChemicalType>> queryType(@RequestParam String word, @RequestParam int level,
                                           @RequestParam(required = false) String type1Id) {
        final List<ChemicalType> list = basicDataService.queryType(word, level, type1Id);
        return R.success(list, list.size());
    }

    @GetMapping("/type/del")
    public R<String> deleteType(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            return R.fail("类型Id不允许为空");
        }
        basicDataService.deleteType(id);
        return R.success("删除成功");
    }
}
