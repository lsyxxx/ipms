package com.abt.finance.controller;

import com.abt.common.model.R;
import com.abt.finance.entity.FixedAsset;
import com.abt.finance.model.FixedAssetRequestForm;
import com.abt.finance.service.FixedAssetsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/fi/fixedAssets")
@Tag(name = "FixedAssetsController", description = "固定资产")
public class FixedAssetsController {

    private final FixedAssetsService fixedAssetsService;

    public FixedAssetsController(FixedAssetsService fixedAssetsService) {
        this.fixedAssetsService = fixedAssetsService;
    }

    @GetMapping("/find")
    public R<List<FixedAsset>> findBy(@ModelAttribute FixedAssetRequestForm form) {
        final Page<FixedAsset> page = fixedAssetsService.findListByQuery(form);
        return R.success(page.getContent(), (int) page.getTotalElements());
    }

    @PostMapping("/save")
    public R<Object> save(@Validated FixedAsset fixedAsset) {
        fixedAssetsService.save(fixedAsset);
        return R.success("保存成功");
    }

    @GetMapping("/del")
    public R<Object> delete(Long id) {
        fixedAssetsService.delete(id);
        return R.success("删除成功");
    }

    @GetMapping("/code")
    public R<String> codeGenerator() {
        final String code = fixedAssetsService.codeGenerator();
        return R.success(code, "");
    }

}
