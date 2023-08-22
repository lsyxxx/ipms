package com.ipms.sys.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipms.common.model.R;

/**
 *  JSON相关工具类
 */
public class JsonUtil {

    public static String toJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper.writeValueAsString(object);
    }


    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(new R().toJson());
    }

}
