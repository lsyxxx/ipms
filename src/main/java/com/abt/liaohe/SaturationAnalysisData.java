package com.abt.liaohe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 *
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tmp_rock_str")
public class SaturationAnalysisData extends RockBase{

    public SaturationAnalysisData(List<RawData> row) {
        super(row);
//        row.stream().filter(i -> i.getTestName().contains("油")).findAny().ifPresent(i -> {
//            this.oil = i.getTestValue();
//        });
//        row.stream().filter(i -> i.getTestName().contains("气")).findAny().ifPresent(i -> {
//            this.gas = i.getTestValue();
//        });
//        row.stream().filter(i -> i.getTestName().contains("水")).findAny().ifPresent(i -> {
//            this.water = i.getTestValue();
//        });

        //覆压数据
        row.stream().filter(i -> i.getTestName().contains("脉冲")).findAny().ifPresent(i -> {
            this.oil = i.getTestValue();
        });
        row.stream().filter(i -> i.getTestName().contains("克氏")).findAny().ifPresent(i -> {
            this.gas = i.getTestValue();
        });
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 饱和度-气
     * 克氏
     */
    private String gas;

    /**
     * 饱和度-水
     */
    private String water;

    /**
     * 饱和度-流体
     */
    private String liquid;

    /**
     * 饱和度-油
     * 脉冲
     */
    private String oil;


}
