package com.abt.flow.repository;

import com.abt.flow.model.entity.FlowScheme;

import java.util.List;

/**
 *
 */
public interface FlowSchemeRepository{

    FlowScheme findById(String id);

    List<FlowScheme> findAllEnabled();
}
