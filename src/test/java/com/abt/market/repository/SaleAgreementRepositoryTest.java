package com.abt.market.repository;

import com.abt.market.entity.SaleAgreement;
import com.abt.sys.model.CountQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SaleAgreementRepositoryTest {

    @Autowired
    private SaleAgreementRepository saleAgreementRepository;

    @Test
    void findBySignDateBetween() {

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        System.out.println(startOfWeek);
        System.out.println(endOfWeek);
        final List<SaleAgreement> list = saleAgreementRepository.findByCreateDateBetweenOrderByCreateDateDesc(startOfWeek.atStartOfDay(), endOfWeek.atStartOfDay());
        assertNotNull(list);
        list.forEach(System.out::println);
    }

    @Test
    void findMonth() {
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 获取当前年份和月份
        YearMonth currentMonth = YearMonth.of(today.getYear(), today.getMonth());
        // 获取当前月份的第一天:yyyy-mm-dd
        LocalDate startOfMonth = currentMonth.atDay(1);
        // 获取当前月份的最后一天:yyyy-mm-dd
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        System.out.println(startOfMonth);
        System.out.println(endOfMonth);
        final List<SaleAgreement> list = saleAgreementRepository.findByCreateDateBetweenOrderByCreateDateDesc(startOfMonth.atStartOfDay(), endOfMonth.atStartOfDay());
        assertNotNull(list);
        System.out.println(list.size());
        list.forEach(i -> System.out.println(i.toString()));
    }

    @Test
    void query() {
        final List<CountQuery> list = saleAgreementRepository.findPartyAWithMostContracts(PageRequest.of(0, 1));
        assertNotNull(list);
        list.forEach(System.out::println);

    }

    @Test
    void countAllDistinctByPartyA() {
        final int total = saleAgreementRepository.countAllDistinctByPartyA();
        System.out.println(total);
    }

    @Test
    void countContractsByYearMonth() {
        final List<CountQuery> countQueries = saleAgreementRepository.countContractsByYearMonth(2024);
        assertNotNull(countQueries);
        countQueries.forEach(System.out::println);
    }
}