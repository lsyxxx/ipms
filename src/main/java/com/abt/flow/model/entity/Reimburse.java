package com.abt.flow.model.entity;

import com.abt.common.model.AuditInfo;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.sys.model.dto.UserView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 报销流程业务库
 */
@Data
@Schema(description = "T_reimburse 报销流程数据表")
@Table(name = "T_reimburse")
@Comment("报销流程数据表")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reimburse extends FlowBusinessBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1054545668262126831L;

    /**
     * PK
     * 关联流程引擎businessKey
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;

    /**
     * 报销事由
     */
    @Schema(description = "报销事由")
    @Column(columnDefinition = "VARCHAR(255)")
    private String reason;

    /**
     * 报销费用
     */
    @Schema(description = "报销费用")
    @Column(columnDefinition = "DECIMAL(18,2)")
    private double cost;

    /**
     * 票据数量
     */
    @Schema(description = "票据数量")
    @Column(name = "vch_num", columnDefinition = "SMALLINT")
    private int voucherNum;

    /**
     * 关联项目
     */
    @Schema(description = "关联项目")
    @Column(columnDefinition = "VARCHAR(255)")
    private String project;

    /**
     * 报销日期
     */
    @Schema(description = "报销日期")
    @Column(name = "rbs_date", columnDefinition = "VARCHAR(255)")
    private Date reimburseDate;


    public Reimburse create(ReimburseApplyForm form, UserView user) {
        Reimburse rbs = new Reimburse();
        rbs.setCost(form.getCost());
        rbs.setProject(form.getProject());
        rbs.setReimburseDate(form.getRbsDate());
        rbs.setReason(form.getReason());
        rbs.setVoucherNum(form.getVoucherNum());
        rbs.setReimburseDate(form.getRbsDate());

        rbs.setCategoryId(form.getFlowType().getId());
        rbs.setCategoryCode(form.getFlowType().getCode());
        rbs.setCategoryName(form.getFlowType().getName());

        create(user.getId(), user.getUsername());

        return rbs;
    }


}
