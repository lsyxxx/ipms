package com.abt.util;

import com.abt.common.util.JsonUtil;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.model.entity.FlowScheme;
import com.abt.flow.model.entity.Reimburse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 *
 */
public class JsonUtilTest {

    public static void main(String[] args) throws JsonProcessingException {
//        Reimburse rbs = new Reimburse();
//        FlowScheme flowScheme = new FlowScheme();
        ReimburseApplyForm f = new ReimburseApplyForm(13.6, 2, new Date(), "报销测试123");
        System.out.println(JsonUtil.toJson(f));

    }

    void mapToObject() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    }
}
