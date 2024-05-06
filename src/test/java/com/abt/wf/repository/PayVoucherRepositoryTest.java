package com.abt.wf.repository;

import com.abt.wf.entity.PayVoucher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PayVoucherRepositoryTest {

    @Autowired
    private PayVoucherTaskRepository payVoucherTaskRepository;;
    @Autowired PayVoucherRepository payVoucherRepository;

    @Test
    void findDoneList() {
        final List<PayVoucher> list = payVoucherTaskRepository.findPayVoucherDoneList(1, 20, "621faa40-f45c-4da8-9a8f-65b0c5353f40", "刘宋菀",
                null, null, null, null, null, null, null);
        assertNotNull(list);
        System.out.println(list.size());
        list.forEach(item -> System.out.println(item.toString()));
    }

    @Test
    void findTodoList() {
        final List<PayVoucher> list = payVoucherTaskRepository.findPayVoucherTodoList(1, 20, "621faa40-f45c-4da8-9a8f-65b0c5353f40", "刘宋菀",
                null, null, null, null, null, null, null);
        assertNotNull(list);
        System.out.println(list.size());
        list.forEach(item -> System.out.println(item.toString()));
    }

//    void findById() {
//        payVoucherRepository.findById("");
//    }

}