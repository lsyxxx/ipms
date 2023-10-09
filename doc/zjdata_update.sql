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