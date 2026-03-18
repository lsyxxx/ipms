package com.abt.safety.service;

import com.abt.safety.entity.SafetyForm;
import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.entity.SafetyRectify;
import com.abt.safety.model.SafetyRecordRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SafetyRecordService {
    boolean recordExist(LocalDate checkDate, Long formId);

    /**
     * 安全检查人提交表单
     */
    SafetyRecord saveCheck(SafetyForm form);

    SafetyRecord loadRecord(String id);

    Page<SafetyRecord> findSafetyRecordPageable(SafetyRecordRequestForm requestForm);

    SafetyRecord loadRecordOnly(String id);

    SafetyRectify findRectifyById(int id);

    List<SafetyRectify> findRectifyListByRecordId(String recordId);

//    /**
//     * 检查分配给负责人
//     * @param record 检查记录
//     * @param dispatcherId 调度人userid
//     * @param dispatcherName 调度人姓名
//     * @param rectifierId 负责人工号
//     * @param rectifierName 负责人姓名
//     * @return SafetyRecord record 检查记录，包含正在进行的整改记录(currentRectify)
//     */
//    SafetyRecord dispatch(SafetyRecord record, String dispatcherId, String dispatcherName, String rectifierId, String rectifierName);

    /**
     * 整改人整改
     *
     * @param safetyRectify 整改记录
     * @param safetyRecord  检查记录
     */
    SafetyRecord rectify(SafetyRectify safetyRectify, SafetyRecord safetyRecord);

    /**
     * 整改通过
     */
    void rectifyPass(SafetyRectify safetyRectify);

    /**
     * 检查通过，结束
     * @param safetyRecord 检查记录
     */
    void complete(SafetyRecord safetyRecord);

    /**
     * 整改不通过s
     */
    void rectifyReject(SafetyRectify safetyRectify);

    void logicDelete(String id);

    /**
     * 调度人分配，只影响safetyRecord
     * 1. 若没有正在进行的rectifyRecord，那么新建一个，整改人是rectifierId
     * 2. 若存在正在进行的rectifyRecord，不影响正在进行的safetyRectify， 仅修改safetyRecord中的。
     * @param safetyRecord 检查记录
     * */
    void dispatch(SafetyRecord safetyRecord, String rectifierId, String rectifierName);

    SafetyRectify createAndSaveRectifyRecord(String recordId, String rectifierId, String rectifierName, String dispatcherId, String dispatcherName);

    /**
     * 创建一条整改记录
     */
    SafetyRectify createRectifyRecord(String recordId, String rectifierId, String rectifierName, String dispatcherId, String dispatcherName);

    /**
     * 催办
     * @param toUserid 被催办人id
     * @param toUsername 被催办人Name
     */
    void remind(String toUserid, String toUsername, String fromUsername, SafetyRecord record, String msg);

    /**
     * 查询正在进行的整改
     * @param recordId 安全检查记录id
     */
    Optional<SafetyRectify> findRunningRectify(String recordId);

    /**
     * 修改整改记录的整改人
     * @param id 整改记录id
     * @param rectifierId 整改人id
     * @param rectifierName 整改人姓名
     * @param dispatcherId 调度人id
     * @param dispatcherName 调度人姓名
     */
    void updateRectifyUser(int id, String rectifierId, String rectifierName, String dispatcherId, String dispatcherName);
}
