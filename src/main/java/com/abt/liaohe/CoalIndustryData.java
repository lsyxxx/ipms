package com.abt.liaohe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 煤工业分析
 * ad: airDry: 空气干燥机 ad
 * dry: 干燥基 d
 * volatile: 挥发
 * DryAshFree: 无灰干燥基 daf
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tmp_coal_industry_analysis")
public class CoalIndustryData extends RockBase{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 水分-空气干燥基Mad
     */
    private String moistureAirDry;

    /**
     * 灰分-空气干燥基 Aad
     */
    private String ashAirDry;


    /**
     * 灰分-干燥基 Ad
     */
    private String ashDry;

    /**
     * 挥发分-空气干燥基
     */
    private String volatileAirDry;

    /**
     * 挥发分-干燥基
     */
    private String volatileDry;


    /**
     * 挥发分-无灰干燥基
     */
    private String volatileDryAshFree;


    /**
     * 固定碳-空气干燥基
     */
    private String fixedCarbonAirDry;

    public CoalIndustryData(List<RawData> row) {
        super(row);
        row.stream().filter(i -> i.getTestName().contains("挥发分 %空气干燥基")).findAny().ifPresent(i -> {
            this.volatileAirDry = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("水分%空气干燥基")).findAny().ifPresent(i -> {
            this.moistureAirDry = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("挥发分 %干燥无灰基")).findAny().ifPresent(i -> {
            this.volatileDryAshFree = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("灰分%干燥基")).findAny().ifPresent(i -> {
            this.ashDry = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("灰分%空气干燥基")).findAny().ifPresent(i -> {
            this.ashAirDry = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("固定碳%空气干燥基")).findAny().ifPresent(i -> {
            this.fixedCarbonAirDry = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("挥发分 %干燥基")).findAny().ifPresent(i -> {
            this.volatileDry = i.getTestValue();
        });
    }

}
