package com.abt.material.controller;

import com.abt.common.model.R;
import com.abt.material.entity.MaterialDetail;
import com.abt.material.service.MaterialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/mtr")
@Tag(name = "MaterialController", description = "")
public class MaterialController {
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("/find/all")
    public R<List<MaterialDetail>> findAllMaterialDetails() {
        final List<MaterialDetail> all = materialService.findAll();
        return R.success(all);
    }


}
