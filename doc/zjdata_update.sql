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