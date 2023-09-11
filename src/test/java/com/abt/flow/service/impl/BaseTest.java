package com.abt.flow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

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
}
