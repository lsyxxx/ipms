package com.abt.liaohe;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 */
@Getter
@Setter
@Table(name = "tmp_ele_minor")
@Entity
@DynamicUpdate
@DynamicInsert
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

}
