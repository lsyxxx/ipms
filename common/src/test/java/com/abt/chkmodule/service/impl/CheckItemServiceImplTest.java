package com.abt.chkmodule.service.impl;

import com.abt.chkmodule.entity.CheckItem;
import com.abt.chkmodule.repository.CheckItemRepository;
import com.abt.chkmodule.service.CheckItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CheckItemServiceImplTest {

    @Mock
    private CheckItemRepository checkItemRepository;

    @Test
    void saveItem() {
        CheckItem checkItem = new CheckItem();
        checkItem.setAliasName("别名");
        checkItem.setName("cehsi1");
        checkItem.setCode("123");
        System.out.println(checkItem);

        final CheckItem save = checkItemRepository.save(checkItem);
        System.out.println(save);
        System.out.println(checkItem.getId());

    }
}