package com.abt.testing.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.testing.entity.Agreement;
import com.abt.testing.entity.EnumLib;
import com.abt.testing.model.AgreementRequestForm;
import com.abt.testing.model.CustomerRequestForm;
import com.abt.testing.service.AgreementService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 合同相关
 */
@RestController
@Slf4j
@RequestMapping("/agreement")
@Validated
public class AgreementController {

    private final AgreementService agreementService;

    public AgreementController(AgreementService agreementService) {
        this.agreementService = agreementService;
    }

    /**
     * 创建/更新预登记合同
     * @param agreement 合同
     */
    @PostMapping("/pre/save")
    public R<Object> savePreAgreement(@Validated({ValidateGroup.Insert.class}) @RequestBody Agreement agreement) {
        agreementService.savePreAgreement(agreement);
        return R.success("创建成功");
    }

    @PostMapping("/pre/update")
    public R<Object> updatePreAgreement(@Validated({ValidateGroup.Update.class}) @RequestBody Agreement agreement) {
        agreementService.savePreAgreement(agreement);        return R.success("创建成功");
    }

    /**
     * 根据条件查询
     * 根据“合同编号”“合同名称”“甲方”模糊搜索合同
     * 根据“合同分类”“乙方(ABT/GRD)”搜索合同
     */
    @GetMapping("/pre/find")
    public R<List<Agreement>> getPreAgreementListBy(AgreementRequestForm form) {
        final Page<Agreement> paged = agreementService.findAgreementsPagedBy(form);
        return R.success(paged.getContent(), (int)paged.getTotalElements());
    }

    @GetMapping("/pre/load")
    public R<Agreement> loadEntity(@RequestParam @NotNull String id) {
        final Agreement entity = agreementService.load(id);
        return R.success(entity);
    }

    /**
     * 删除
     */
    @GetMapping("/pre/del")
    @Validated
    public R<Object> deletePreAgreement(@RequestParam @NotNull String id) {
        agreementService.deletePreAgreement(id);
        return R.success("删除成功");
    }
    /**
     * 查询合同分类
     */
    @GetMapping("/enum/types")
    public R<List<EnumLib>> getContractType() {
        final List<EnumLib> list = agreementService.findAgreementEnumTypes();
        return R.success(list);
    }



}
