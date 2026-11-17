package com.abt.testing.converter;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** JSON 文本往返和空值兼容测试。 */
class ReportCoverStringListConverterTest {
    private final ReportCoverStringListConverter converter = new ReportCoverStringListConverter();

    @Test
    void preservesChineseModelNumbersQuotesAndNewlines() {
        List<String> values = List.of("检测设备 型号/A\n说明\"一\"", "标准 GB/T 14506.28-2010");
        assertEquals(values, converter.convertToEntityAttribute(converter.convertToDatabaseColumn(values)));
    }

    @Test
    void persistsEmptyArraysAndReadsLegacyNullsAsEmpty() {
        assertEquals("[]", converter.convertToDatabaseColumn(null));
        assertEquals("[]", converter.convertToDatabaseColumn(List.of()));
        for (String json : new String[]{null, "", " ", "[]", "null"}) {
            assertTrue(converter.convertToEntityAttribute(json).isEmpty());
        }
    }

    @Test
    void rejectsCorruptJsonInsteadOfSilentlyLosingAssociations() {
        assertThrows(RuntimeException.class, () -> converter.convertToEntityAttribute("[broken"));
    }
}
