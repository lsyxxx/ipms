package com.abt.safety.service;

import com.abt.safety.entity.SafetyForm;
import com.abt.safety.entity.SafetyRecord;
import com.abt.safety.model.SafetyRecordRequestForm;
import com.abt.sys.model.entity.SystemFile;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface SafetyRecordService {
    boolean recordExist(LocalDate checkDate, Long formId);

    /**
     * 安全检查人提交表单
     */
    SafetyRecord saveCheck(SafetyForm form);

    SafetyRecord loadRecord(String id);

    Page<SafetyRecord> findSafetyRecordPageable(SafetyRecordRequestForm requestForm);

    SafetyRecord loadRecordOnly(String id);

    /**
     * 检查分配给负责人
     * @param id 记录Id
     * @param dispatcherId 调度人userid
     * @param dispatcherName 调度人姓名
     * @param rectifierId 负责人工号
     * @param rectifierName 负责人姓名
     */
    SafetyRecord dispatch(String id, String dispatcherId, String dispatcherName, String rectifierId, String rectifierName);

    /**
     * 整改
     * @param id 记录Id
     * @param rectifyRemark 整改说明
     * @param systemFiles 上传文件
     */
    SafetyRecord rectified(String id, String rectifyRemark, List<SystemFile> systemFiles);

    void logicDelete(String id);
}
