package com.abt.wf.serivce;

import com.abt.sys.model.entity.FlowSetting;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.model.ReimburseDTO;

import java.util.List;
import java.util.Optional;

/**
 * 报销业务
 */
public interface ReimburseService {

    Reimburse saveEntity(ReimburseApplyForm applyForm);

    Reimburse saveEntity(Reimburse entity);

    Optional<Reimburse> queryBy(String id);

    List<ReimburseDTO> queryByStater(String starterId, int page, int size);

    List<ReimburseDTO> queryByStater(String starterId);

    List<FlowSetting> queryRbsTypes();

    /**
     * 暂存
     * 1. 暂存表单信息(但是不校验必填信息)
     * 2. 暂存附件
     * 3. 无流程信息
     * 4. 状态为暂存
     * 5. 生成一个wf_rbs的ID
     * @param form 申请表单
     */
    String tempSave(ReimburseApplyForm form);
}
