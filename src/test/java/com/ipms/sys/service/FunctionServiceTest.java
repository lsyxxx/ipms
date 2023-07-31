package com.ipms.sys.service;

import com.ipms.sys.model.entity.Function;
import com.ipms.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
@Slf4j
class FunctionServiceTest {

    @Autowired
    private FunctionService service;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAll() {
        List<Function> list = service.findAll();
        LogUtil.divider();
        System.out.println(list.size());
        LogUtil.divider();

    }

    @Test
    void delete() {
        service.delete(new Function().setId(3L));
    }

    @Test
    void findById() {
    }

    @Test
    void insert() {
        Function function = new Function();
        function.setPath("1.1.3")
                .setName("系统管理-删除用户")
                .setPid(1L)
                .setUrl("/sys/u/del")
                .setRemark("删除用户")
                .setComponent("/sys/u")
                .setState(true)
                .setSort("113")
                .setEnabled(true)
        ;
        Long id = service.insert(function);
        LogUtil.divider(id.toString());

    }

    @Test
    void update() {
    }

    @Test
    void enabled() {
        service.enabled(new Function().setId(4L));
    }

    @Test
    void disabled() {
        service.disabled(new Function().setId(4L));
    }
}