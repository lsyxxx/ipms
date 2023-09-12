package com.abt.common.util;

import com.abt.http.dto.WebApiDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.abt.common.model.R;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 *  JSON相关工具类
 */
public class JsonUtil {

    public static String toJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    public static ObjectMapper ObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    public static Object toObject(String json, Class clz) throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapper();
        return objectMapper.readValue(json, clz);
    }

    public static WebApiDto webApiDto(String json) throws JsonProcessingException {
        return (WebApiDto) toObject(json, WebApiDto.class);
    }



}
