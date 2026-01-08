package com.abt.oa.controller;

import com.abt.common.model.R;
import com.abt.oa.entity.PaiBan;
import com.abt.oa.model.PaibanRequestForm;
import com.abt.oa.service.PaiBanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 排班
 */
@RestController
@Slf4j
@RequestMapping("/paiban")
@Tag(name = "PaibanController", description = "")
public class PaibanController {

    private final PaiBanService paibanService;

    public PaibanController(PaiBanService paibanService) {
        this.paibanService = paibanService;
    }


    @GetMapping("/find/list")
    public R<List<PaiBan>> findList(PaibanRequestForm form) {
        final List<PaiBan> list = paibanService.findBetween(form.getLocalStartDate(), form.getLocalEndDate());
        return R.success(list);
    }
}
