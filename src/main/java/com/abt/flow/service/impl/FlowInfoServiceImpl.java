package com.abt.flow.service.impl;

import com.abt.flow.model.entity.BizFlowCategory;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.repository.BizFlowCategoryRepository;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.service.FlowInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class FlowInfoServiceImpl implements FlowInfoService {

    public static final String ORDERBY_CRTDATE = "createDate";
    public static final String ORDERBY_SORT= "sortCode";

    private final BizFlowCategoryRepository bizFlowCategoryRepository;

    private final Example<BizFlowCategory> enabledExample;

    private final BizFlowRelationRepository bizFlowRelationRepository;

    @Value("${com.abt.flow.service.impl.FlowInfoServiceImpl.queryType.disposed}")
    private String queryTypeDisposed;

    @Value("${com.abt.flow.service.impl.FlowInfoServiceImpl.queryType.wait}")
    private String queryTypeWait;

    public FlowInfoServiceImpl(BizFlowCategoryRepository bizFlowCategoryRepository, Example<BizFlowCategory> enabledExample, BizFlowRelationRepository bizFlowRelationRepository) {
        this.bizFlowCategoryRepository = bizFlowCategoryRepository;
        this.enabledExample = enabledExample;
        this.bizFlowRelationRepository = bizFlowRelationRepository;
    }


    @Override
    public List<BizFlowRelation> getUserApplyFlows(String userId, int page, int size, String query) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, ORDERBY_CRTDATE);

        Page<BizFlowRelation> all = bizFlowRelationRepository.findByStarterIdOrderByStartDateDesc(userId, pageRequest);
        return all.getContent();
    }


    @Override
    public List<BizFlowCategory> findAllEnabled(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, ORDERBY_CRTDATE, ORDERBY_SORT);
        log.info("example == {}", enabledExample.toString());
        Page<BizFlowCategory> all = bizFlowCategoryRepository.findAll(enabledExample, pageRequest);
        if (all.hasContent()) {
            return all.getContent();
        }
        return List.of();
    }


    @Override
    public List<BizFlowRelation> find(String userId, int page, int size, String query, String type) {
        return getInstanceListByUser(userId, page, size, query, type);
    }

    @Override
    public List<BizFlowRelation> getTodoFlows(String userId, int page, int size, String query) {
        return null;
    }

    @Override
    public List<BizFlowRelation> getInvokedFlows(String userId, int page, int size, String query) {
        return null;
    }

    @Override
    public List<BizFlowRelation> getFlows(String userId, int page, int size, String query, String type) {
        return null;
    }


    private List<BizFlowRelation> getInstanceListByUser(String userId, int page, int size, String query, String type) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, ORDERBY_CRTDATE);
        Page<BizFlowRelation> all = bizFlowRelationRepository.findByStarterIdAndCustomNameContaining(userId, query, pageRequest);

        return all.getContent();
    }

}
