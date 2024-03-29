package com.abt.wf.controller;

import com.abt.common.model.R;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 差旅报销
 */
@RestController
@Slf4j
@RequestMapping("/wf/trip")
public class TripController {

    @PostMapping("/apply")
    public void apply(@Validated @RequestBody TripReimburseForm form) {

    }

    @PostMapping("/approve")
    public void approve(@RequestBody TripReimburseForm form) {

    }

    /**
     * 差旅报销记录
     * @param criteria 搜索条件
     */
    @PostMapping("/all")
    public void getAll(@ModelAttribute TripRequestForm criteria) {

    }

    @GetMapping("/load/{entityId}")
    public R<TripReimburseForm> load(@PathVariable String entityId) {

        return null;
    }




}
