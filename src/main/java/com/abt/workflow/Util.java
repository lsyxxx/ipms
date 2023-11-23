package com.abt.workflow;

import java.util.UUID;

/**
 *
 */
public class Util {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static <T> T newInstance(Class<T> clazz) throws Exception {
        return clazz.newInstance();
    }

}
