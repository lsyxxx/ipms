package com.abt.testing.service.impl;

import com.abt.testing.repository.EntrustRepository;
import com.abt.testing.service.EntrustService;
import org.springframework.stereotype.Service;

/**
 * 委托单(业务登记)
 */
@Service
public class EntrustServiceImpl implements EntrustService {

    private final EntrustRepository entrustRepository;

    public EntrustServiceImpl(EntrustRepository entrustRepository) {
        this.entrustRepository = entrustRepository;
    }
}
