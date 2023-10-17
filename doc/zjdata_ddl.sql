------------------------------------
--- FlowScheme添加ProcDefId字段
------------------------------------
ALTER TABLE [dbo].[FlowScheme] DROP COLUMN [ProcDefId]
    GO

ALTER TABLE [dbo].[FlowScheme] ADD [ProcDefId] varchar(128) NULL
    GO

    EXEC sp_addextendedproperty
    'MS_Description', N'Flowable对应procdef:id',
    'SCHEMA', N'dbo',
    'TABLE', N'FlowScheme',
    'COLUMN', N'ProcDefId'


------------------------------------
--- FlowScheme添加Service字段
------------------------------------
ALTER TABLE [dbo].[FlowScheme] DROP COLUMN [Service]
    GO

ALTER TABLE [dbo].[FlowScheme] ADD [Service] varchar(255) NULL
    GO

    EXEC sp_addextendedproperty
    'MS_Description', N'业务对应服务',
    'SCHEMA', N'dbo',
    'TABLE', N'FlowScheme',
    'COLUMN', N'Service'




---------------------------------------
-- Table structure for t_flow_setting
---------------------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[t_flow_setting]') AND type IN ('U'))
DROP TABLE [dbo].[t_flow_setting]
    GO

CREATE TABLE [dbo].[t_flow_setting] (
    [id] varchar(255) COLLATE Chinese_PRC_CI_AS  NOT NULL,
    [create_date] datetime2(6)  NULL,
    [create_userid] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
    [create_username] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
    [update_date] datetime2(6)  NULL,
    [update_userid] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
    [update_username] varchar(255) COLLATE Chinese_PRC_CI_AS  NULL,
    [key_] varchar(128) COLLATE Chinese_PRC_CI_AS  NULL,
    [remark] varchar(128) COLLATE Chinese_PRC_CI_AS  NULL,
    [type_] varchar(128) COLLATE Chinese_PRC_CI_AS  NULL,
    [value_] varchar(128) COLLATE Chinese_PRC_CI_AS  NULL,
    [description] varchar(128) COLLATE Chinese_PRC_CI_AS  NULL
    )
    GO

ALTER TABLE [dbo].[t_flow_setting] SET (LOCK_ESCALATION = TABLE)
    GO