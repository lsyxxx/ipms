package com.abt.flow.controller;

import com.abt.common.model.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程部署
 */
@RestController
@Slf4j
@RequestMapping("/flow/deploy")
@Tag(name = "DeploymentController", description = "流程部署controller")
public class DeploymentController {


    @Operation(summary = "部署流程")
    @Parameter(name = "", description = "")
    @PostMapping("/do")
    public R doDeploy() {


        return R.success();
    }

    @Operation(summary = "查看已部署流程")
    @Parameter(name = "", description = "")
    @PostMapping("/info")
    public R deployedInfo() {
        //部署，一次打包部署多个流程, 如果一次部署多个文件，自己区分每个版本(使用key_)
        // act_re_deployment 本身没有版本概念，需要自己区分
        //
        //0. engine info: act_ge_property

        //1. Table: 已部署deployed info: id, name, key_, deploy_time, dep

        //2. Table: 流程定义 act_re_procdef. 需要查看每个流程最新的版本

        //3. 流程资源文件(bpmn20.xml, png等)目录

        return R.success();
    }




}
