package com.abt.sys;

import com.abt.sys.model.entity.SystemFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

/**
 * SystemFile 与数据库 JSON 字符串互转（单对象，与 {@link com.abt.sys.util.SystemFileListConverter} 对应）
 */
@Converter(autoApply = true)
public class SystemFileConverter implements AttributeConverter<SystemFile, String> {

    @Override
    public String convertToDatabaseColumn(SystemFile attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while serializing SystemFile to json", e);
        }
    }

    @Override
    public SystemFile convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            return objectMapper.readValue(dbData, SystemFile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while deserializing SystemFile from json", e);
        }
    }
}
