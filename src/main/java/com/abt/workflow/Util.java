package com.abt.workflow;

import java.lang.reflect.Constructor;
import java.util.UUID;

/**
 *
 */
public class Util {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static <T> T newInstance(Class<T> clazz) throws Exception {
        final Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

}
