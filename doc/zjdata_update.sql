/*
 Navicat Premium Data Transfer

 Source Server         : sqlserver_local
 Source Server Type    : SQL Server
 Source Server Version : 16001000 (16.00.1000)
 Source Host           : localhost:1433
 Source Catalog        : ZJData
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 16001000 (16.00.1000)
 File Encoding         : 65001

 Date: 29/08/2023 15:38:59
*/


-- ----------------------------
-- Table structure for T_biz_flow_ref
-- 业务-流程关系表
-- 方便查询
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[T_biz_flow_ref]') AND type IN ('U'))
    DROP TABLE [dbo].[T_biz_flow_ref]
GO

CREATE TABLE [dbo].[T_biz_flow_ref] (
    [Id] [dbo].[PrimaryKey]  NOT NULL,
    [biz_cat] varchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
    [biz_name] varchar(200) COLLATE Chinese_PRC_CI_AS  NOT NULL,
    [proc_id] varchar(128) COLLATE Chinese_PRC_CI_AS    NULL,
    [starter_id] varchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
    [starter_name] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
    [start_date] datetime  NULL,
    [procdef_id] varchar(128) COLLATE Chinese_PRC_CI_AS  NOT NULL,
    [state] smallint  NOT NULL,
    [task_id] varchar(128) COLLATE Chinese_PRC_CI_AS  NULL,
    [end_date] datetime  NULL,
    [del_reason] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
    [del_user] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
    [del_date] datetime  NULL
)
GO
ALTER TABLE [dbo].[T_biz_flow_ref] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
     'MS_Description', N'PK',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'业务类型ID，对应FlowScheme: id',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'biz_cat'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'业务类型Name，对应FlowScheme: Name',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'biz_name'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'流程实例id，对应流程引擎中的proc_inst_id',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'proc_id'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'启动流程用户id，对应User: id',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'starter_id'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'启动流程用户name, 对应User:Name',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'starter_name'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'启动流程时间',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'start_date'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'流程定义id, 对应流程引擎中act_re_procdef: id',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'procdef_id'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'自定义流程状态, 1进行中，0完成，2已删除, 3暂存',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'state'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'当前正在进行的流程task，对应act_ru_task: id',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'task_id'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'流程完成时间',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'end_date'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'流程删除原因',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'del_reason'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'流程删除用户',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'del_user'
GO

EXEC sp_addextendedproperty
     'MS_Description', N'流程删除时间',
     'SCHEMA', N'dbo',
     'TABLE', N'T_biz_flow_ref',
     'COLUMN', N'del_date'
GO


-- ----------------------------
-- Primary Key structure for table T_biz_flow_ref
-- ----------------------------
ALTER TABLE [dbo].[T_biz_flow_ref] ADD CONSTRAINT [PK_BIZ_FLOW_REF] PRIMARY KEY CLUSTERED ([Id])
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
GO

