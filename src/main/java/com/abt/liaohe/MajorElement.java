package com.abt.liaohe;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * 主量元素分析
 */
@Table(name = "tmp_ele_major")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(AuditingEntityListener.class)
public class MajorElement extends RockBase{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * SiO2
     */
    private Double SiO2;
    private Double TiO2;
    private Double Al2O3;
    private Double TFe2O3;
    private Double MnO;
    private Double MgO;
    private Double CaO;
    private Double Na2O;
    private Double K2O;
    private Double P2O5;
    private Double LOI;
    private Double total;

    public MajorElement(List<RawData> row) {
        super(row);
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("SIO2")).findAny().ifPresent(i -> {
            try {
                this.setSiO2(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("TIO2")).findAny().ifPresent(i -> {
            try {
                this.setTiO2(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("AL2O3")).findAny().ifPresent(i -> {
            try {
                this.setAl2O3(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("TFE2O3")).findAny().ifPresent(i -> {
            try {
                this.setTFe2O3(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("MNO")).findAny().ifPresent(i -> {
            try {
                this.setMnO(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("MGO")).findAny().ifPresent(i -> {
            try {
                this.setMgO(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("CAO")).findAny().ifPresent(i -> {
            try {
                this.setCaO(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("NA2O")).findAny().ifPresent(i -> {
            try {
                this.setNa2O(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> i.getTestName().toUpperCase().contains("K2O")).findAny().ifPresent(i -> {
            try {
                this.setK2O(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> Util.containsIgnoreCase(i.getTestName(), "P2O5")).findAny().ifPresent(i -> {
            try {
                this.setP2O5(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> Util.containsIgnoreCase(i.getTestName(), "LOI")).findAny().ifPresent(i -> {
            try {
                this.setLOI(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        row.stream().filter(i -> Util.containsIgnoreCase(i.getTestName(), "total")).findAny().ifPresent(i -> {
            try {
                this.setTotal(Double.parseDouble(i.getTestValue()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }



}
