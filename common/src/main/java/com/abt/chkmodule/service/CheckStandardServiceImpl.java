package com.abt.chkmodule.service;

import com.abt.chkmodule.repository.CheckStandardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
@AllArgsConstructor
public class CheckStandardServiceImpl implements CheckStandardService {

    private final CheckStandardRepository checkStandardRepository;



}
