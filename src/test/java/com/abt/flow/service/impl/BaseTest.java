package com.abt.flow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
@Slf4j
public class BaseTest {


    static void notEmpty(List list) {
        Assert.notEmpty(list, "---------- List is null!");
    }


    static void countList(List list) {
        notEmpty(list);
        log.info("------------ 列表有{}个元素", list.size());
    }

    static void logListElement(List list) {
        countList(list);
        list.forEach(i -> {
            log.info("---- simpleLog: {}", i.toString());
        });
    }

    @Test
    void test() {
        System.out.println(System.currentTimeMillis());
//        20230912 1141000231216
//                 1694496380496
    }
}
