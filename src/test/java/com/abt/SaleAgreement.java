package com.abt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 合同类
 */
@Getter
@Setter
public class SaleAgreement {

    /**
     * 合同ID
     */
    private String id;


    //合同名称
    private String name;

//    public SaleAgreement(int count) {
//        this.id = generateId(count);
//    }


    //设计模式 -- 静态工厂

    // 设计模式

    public static SaleAgreement create(int count) {
        SaleAgreement  saleAgreement = new SaleAgreement();
        saleAgreement.setId(generateId(count));

        //
        //
        //
        return saleAgreement;
    }


    public static String generateId(int count) {
        //1.

        //2.


        //3.

        return "HT" + (count + 1);
    }


}
