package com.abt.sys.util;

import com.abt.sys.model.entity.SystemFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


/**
 * 文件json转为实体类
 */
@Converter(autoApply = true)
public class SystemFileListConverter implements AttributeConverter<List<SystemFile>, String> {

    @Override
    public String convertToDatabaseColumn(List<SystemFile> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing SystemFile to json", e);
        }
    }

    @Override
    public List<SystemFile> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<SystemFile>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing SystemFile to json", e);
        }
    }
}
