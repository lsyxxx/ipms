package com.abt.common.util;

import com.abt.http.dto.WebApiDto;
import com.abt.sys.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.abt.common.model.R;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

/**
 *  JSON相关工具类
 */
@Slf4j
public class JsonUtil {

    public static String toJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapper();
        // 设置 SerializationFeature.WRITE_NULL_MAP_VALUES 为 false
        return objectMapper.writeValueAsString(object);

    }

    public static <T> T toObject(String json, TypeReference<T> valueTypeRef) {
        ObjectMapper objectMapper = ObjectMapper();
        try {
            return objectMapper.readValue(json, valueTypeRef);
        } catch (Exception e) {
            log.error("JSON转对象失败, ", e);
            throw new BusinessException(e);
        }
    }

    public static String convertJson(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return toJson(object);
        } catch (Exception e) {
            log.info("Json转换失败!", e);
            throw new BusinessException(e.getMessage());
        }
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
