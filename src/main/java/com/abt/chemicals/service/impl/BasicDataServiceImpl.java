package com.abt.chemicals.service.impl;

import com.abt.chemicals.repository.TypeRepository;
import com.abt.chemicals.service.BasicDataService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class BasicDataServiceImpl implements BasicDataService {

    private final TypeRepository typeRepository;

    public BasicDataServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public void queryType(String word, int level) {
        typeRepository.findByLevelAndNameLikeOrderBySortAsc(level, word);
    }

}
