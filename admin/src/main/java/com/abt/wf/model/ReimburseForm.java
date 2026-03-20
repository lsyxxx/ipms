package com.abt.wf.model;

import com.abt.common.entity.Company;
import com.abt.common.util.JsonUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemFile;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.Reimburse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.abt.wf.config.Constants.*;

/**
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ReimburseForm extends Reimburse {

    private List<String> managerList = new ArrayList<>();
    private List<SystemFile> pdfAttachments = new ArrayList<>();
    private List<SystemFile> otherAttachments = new ArrayList<>();
    private List<String> copyList = new ArrayList<>();

    //-- 提交人
    private String submitUserid;
    private String submitUsername;

    //-- 审批
    private String decision;
    private String comment;
    private boolean isApproveUser;

    //业务所属公司
    private Company bizCompany;


    private HashMap<String, Object> variableMap = new HashMap<>();

    public String decisionTranslate() {
        switch (this.decision) {
            case DECISION_REJECT -> {
                return DECISION_REJECT_DESC;
            }
            case DECISION_PASS -> {
                return DECISION_PASS_DESC;
            }
            default -> throw new BusinessException("审批决策只能为pass/reject, 实际参数: " + this.decision);
        }
    }

    //-- 流程参数key
    public static final String KEY_COST = "cost";
    public static final String KEY_IS_LEADER = "isLeader";
    public static final String KEY_MANAGER_LIST = "managerList";
    public static final String KEY_STARTER = "starter";

    public Map<String, Object> variableMap() {
        this.variableMap.clear();
        this.variableMap.put(KEY_COST, this.getCost());
        this.variableMap.put(KEY_MANAGER_LIST, this.getManagerList());
        this.variableMap.put(KEY_STARTER, this.getCreateUserid());
        this.variableMap.put(VAR_KEY_DESC, this.description());
        return this.variableMap;
    }
    public String description() {
        return this.getCreateUsername() + "提交的" + this.getServiceName();
    };

    public boolean isPass() {
        return Constants.DECISION_PASS.equals(decision);
    }

    public boolean isReject() {
        return DECISION_REJECT.equals(decision);
    }


    public void prepareEntity() {
        this.setPdfFileList(JsonUtil.convertJson(this.getPdfAttachments()));
        this.setOtherFileList(JsonUtil.convertJson(this.getOtherAttachments()));
        if (!CollectionUtils.isEmpty(this.getManagerList())) {
            this.setManagers(String.join(",", this.getManagerList()));
        }
        if (!CollectionUtils.isEmpty(this.getCopyList())) {
            this.setCopy(String.join(",", this.getCopyList()));
        }
    }


    public static ReimburseForm of(Reimburse rbs) {
        ReimburseForm form = new ReimburseForm();
        if (rbs == null) {
            return form;
        }
        form.setId(rbs.getId());
        form.setCost(rbs.getCost());
        form.setReserveLoan(rbs.getReserveLoan());
        form.setReserveRefund(rbs.getReserveRefund());
        form.setVoucherNum(rbs.getVoucherNum());
        form.setRbsDate(rbs.getRbsDate());
        form.setRbsType(rbs.getRbsType());
        form.setReason(rbs.getReason());
        form.setCompany(rbs.getCompany());
        form.setDepartmentId(rbs.getDepartmentId());
        form.setDepartmentName(rbs.getDepartmentName());
        form.setTeamId(rbs.getTeamId());
        form.setTeamName(rbs.getTeamName());
        form.setProject(rbs.getProject());
        form.setServiceName(rbs.getServiceName());
        form.setPdfFileList(rbs.getPdfFileList());
        form.setOtherFileList(rbs.getOtherFileList());
        form.setBusinessState(rbs.getBusinessState());
        form.setProcessState(rbs.getProcessState());
        form.setFinished(rbs.isFinished());
        form.setManagers(rbs.getManagers());
        form.setDeleteReason(rbs.getDeleteReason());
        form.setServiceName(rbs.getServiceName());
        form.setEndTime(rbs.getEndTime());
        form.setProcessInstanceId(rbs.getProcessInstanceId());
        form.setProcessDefinitionKey(rbs.getProcessDefinitionKey());
        form.setProcessDefinitionId(rbs.getProcessDefinitionId());
        form.setUpdateUserid(rbs.getUpdateUserid());
        form.setUpdateUsername(rbs.getUpdateUsername());
        form.setUpdateDate(rbs.getUpdateDate());
        form.setCreateDate(rbs.getCreateDate());
        form.setCreateUserid(rbs.getCreateUserid());
        form.setCreateUsername(rbs.getCreateUsername());
        form.setCopy(rbs.getCopy());

        if (StringUtils.isNotBlank(rbs.getManagers())) {
            form.setManagerList(Arrays.asList(rbs.getManagers().split(",")));
        }
        if (StringUtils.isNotBlank(rbs.getCopy())) {
            form.setCopyList(Arrays.asList(rbs.getCopy().split(",")));
        }
        try {
            if (StringUtils.isNotBlank(rbs.getPdfFileList())) {
                final List<SystemFile> pdf = JsonUtil.toObject(rbs.getPdfFileList(), new TypeReference<List<SystemFile>>() {});
                form.setPdfAttachments(pdf);
            }
            if (StringUtils.isNotBlank(rbs.getOtherFileList())) {
                final List<SystemFile> others = JsonUtil.toObject(rbs.getOtherFileList(), new TypeReference<List<SystemFile>>() {});
                form.setOtherAttachments(others);
            }
        } catch (Exception e) {
            log.error("Json转换失败");
            throw new BusinessException("Json转换失败! 错误：" +  e.getMessage());
        }
        return form;
    }


    public Reimburse newEntityInstance() {
        Reimburse rbs = new Reimburse();
        rbs.setId(this.getId());
        rbs.setCost(this.getCost());
        rbs.setVoucherNum(this.getVoucherNum());
        rbs.setRbsDate(this.getRbsDate());
        rbs.setRbsType(this.getRbsType());
        rbs.setReason(this.getReason());
        rbs.setReserveLoan(this.getReserveLoan());
        rbs.setReserveRefund(this.getReserveRefund());
        rbs.setCompany(this.getCompany());
        rbs.setDepartmentId(this.getDepartmentId());
        rbs.setDepartmentName(this.getDepartmentName());
        rbs.setTeamId(this.getTeamId());
        rbs.setTeamName(this.getTeamName());
        rbs.setProject(this.getProject());
        rbs.setServiceName(this.getServiceName());
        rbs.setBusinessState(this.getBusinessState());
        rbs.setProcessState(this.getProcessState());
        rbs.setFinished(this.isFinished());
        rbs.setPdfFileList(this.getPdfFileList());
        rbs.setOtherFileList(this.getOtherFileList());
        rbs.setManagers(this.getManagers());
        rbs.setDeleteReason(this.getDeleteReason());
        rbs.setServiceName(this.getServiceName());
        rbs.setEndTime(this.getEndTime());
        rbs.setProcessInstanceId(this.getProcessInstanceId());
        rbs.setProcessDefinitionKey(this.getProcessDefinitionKey());
        rbs.setProcessDefinitionId(this.getProcessDefinitionId());
        rbs.setCopy(this.getCopy());
        return rbs;
    }



}
