package com.abt.liaohe;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

/**
 *
 */
@Getter
@Setter
@Table(name = "tmp_ele_minor")
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MinorElement extends RockBase{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    /**
     * 元素名称
     */
    private String element;

    /**
     * 含量
     */
    private Double content;

    public MinorElement(List<RawData> row) {
        super(row);
    }

    public void setData(RawData rawData) {
        String testName = rawData.getTestName();
        //如果全是字母则认为是微量元素
        final boolean allLetters = StringUtils.isAlpha(testName);
        if (allLetters) {
            setElement(testName);
            try {
                this.setContent(Double.parseDouble(rawData.getTestValue()));
            } catch (Exception e) {
                System.out.println("非数字，不写入");
                System.out.println(e.getMessage());
            }
        }
    }
}
