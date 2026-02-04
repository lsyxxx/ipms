package com.abt.wf;

import com.abt.Parent;
import com.abt.SaleAgreement;
import com.abt.common.model.User;
import com.abt.sys.model.entity.CustomerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 测试类
 *  用于测试XXX
 */
@Getter
@Setter
public class Test extends Parent {

    private String id;

    private String name;

    private int age;

    private String address;

    private User user;

    private CustomerInfo customerInfo;

    public void func1(String param) {
        //code
        System.out.println("func1: param: " + param);
    }
//    public Test() {
//        super();//继承父类的构造方法
//        System.out.println(this.getName());
//    }


//    // Constructor
//    public Test(String count, String name) {
////        this.id = id;
//        this.name = name;
//        System.out.println("有参构造");
//        System.out.println("id:" + id + ", name: " + name);
//
//        // 生成合同唯一编号
//    }




    public static void main(String[] args) {

        SaleAgreement agreement = SaleAgreement.create(1);

        SaleAgreement a1 = new SaleAgreement();

//        SaleAgreement saleAgreement = new SaleAgreement(1);
//        System.out.println(saleAgreement.getId());
        // 保存数据

    }

}
