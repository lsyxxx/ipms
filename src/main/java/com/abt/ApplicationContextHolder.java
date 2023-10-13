package com.abt;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 *
 */
public class ApplicationContextHolder {

    private static ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String beanName) {
        return getApplicationContext().getBean(beanName);
    }
}
