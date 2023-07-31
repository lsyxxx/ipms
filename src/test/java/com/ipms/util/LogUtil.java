package com.ipms.util;

/**
 *
 */
public class LogUtil {

    public static void divider() {
        System.out.println("---------------------------------------------");
    }

    public static void divider(String info) {
        System.out.println(String.format("--------------------{}----------------", info));
    }
}
