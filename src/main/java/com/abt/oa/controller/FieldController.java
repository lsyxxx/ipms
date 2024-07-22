package com.abt.oa.controller;

import com.abt.common.model.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 野外相关
 */
@RestController
@Slf4j
@RequestMapping("/field")
public class FieldController {

    /**
     * 删除一个野外补助选项
     */
    @GetMapping("/del")
    public R<Object> deleteFieldOption(String id) {
        return R.success("删除成功");
    }

}
