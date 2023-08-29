package com.abt.flow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.io.Serializable;

/**
 * (T_biz_flow_ref)业务-流程关系 实体类
 *
 * @since 2023-08-29 16:24:26
 */
@Data
@Accessors(chain = true)
@TableName("T_biz_flow_ref")
public class BizFlowRelation implements Serializable {
    private static final long serialVersionUID = 864872892508644917L;
    /**
     * PK, newid() 自动生成
     */
    @TableId
    private String id;

    /**
     * 业务类型ID，对应FlowScheme: id
     */
    @TableField("biz_cat")
    private String bizCategory;

    /**
     * 业务类型Name，对应FlowScheme: Name
     */
    private String bizName;

    /**
     * 流程实例id，对应流程引擎中的proc_inst_id
     */
    @TableField("proc_id")
    private String processInstId;

    /**
     * 启动流程用户id，对应User: id
     */
    private String starterId;

    /**
     * 启动流程用户name, 对应User:Name
     */
    private String starterName;

    /**
     * 启动流程时间
     */
    private Date startDate;

    /**
     * 流程定义id, 对应流程引擎中act_re_procdef: id
     */
    @TableField("procdef_id")
    private String processDefinitionId;

    /**
     * 自定义流程状态, 1进行中，0完成，2已删除, 6暂存
     * 对应ProcessState
     */
    private Integer state;

    /**
     * 当前正在进行的流程task，对应act_ru_task: id
     */
    private String taskId;

    /**
     * 流程完成时间
     */
    private Date endDate;

    /**
     * 流程删除原因
     */
    private String delReason;

    /**
     * 流程删除用户
     */
    private String delUser;

}

