package com.abt.oa.controller;

import com.abt.oa.model.CarApplyRequestForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆相关
 */
@RestController
@Slf4j
@RequestMapping("/car")
public class CarController {

    /**
     * 根据查询条件导出excel
     * @param requestForm 查询条件
     */
    public void exportExcel(@ModelAttribute CarApplyRequestForm requestForm) {


    }


}
