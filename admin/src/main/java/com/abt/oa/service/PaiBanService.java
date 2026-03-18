package com.abt.oa.service;

import com.abt.oa.entity.PaiBan;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
public interface PaiBanService {
    List<PaiBan> findBetween(LocalDate start, LocalDate end);
}
