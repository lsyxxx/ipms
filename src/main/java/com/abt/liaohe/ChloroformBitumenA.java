package com.abt.liaohe;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
import java.util.Optional;

/**
 * 氯仿沥青
 */
@Getter
@Setter
@Table(name = "tmp_chloroform_bitumen_A")
@Entity
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChloroformBitumenA extends RockBase{


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "DECIMAL(9,4)")
    private double chloroformBitumenA;

    public ChloroformBitumenA(List<RawData> row) {
        super(row);
        Optional<RawData> op =  row.stream().filter(rd -> rd.getTestName().contains("沥青")).findFirst();
        if (op.isPresent()) {
            try {
                this.chloroformBitumenA = Double.parseDouble(op.get().getTestValue());
            } catch (Exception e) {
                System.out.println("不能转为数字");
                e.printStackTrace();
            }
        } else {
            this.chloroformBitumenA = -9999;
        }
    }


}
