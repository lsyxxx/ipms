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


------------------------------------
--- 导入t_flow_setting: 常用审批人预设
------------------------------------
INSERT INTO [dbo].[t_flow_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [key_], [remark], [type_], [value_]) VALUES ('3d0874de-fef2-42d8-81e0-f858687c3e44', '2023-10-16 10:13:12.364369', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', '2023-10-16 10:13:12.364369', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', 'cashier', '叶冼婷', 'defaultAuditor', 'U20230406007');
INSERT INTO [dbo].[t_flow_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [key_], [remark], [type_], [value_]) VALUES ('8698aaa3-cd1d-4456-b467-0a9a345a5db1', '2023-10-16 10:13:12.360346', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', '2023-10-16 10:13:12.360346', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', 'accountancy', '叶冼婷', 'defaultAuditor', 'U20230406007');
INSERT INTO [dbo].[t_flow_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [key_], [remark], [type_], [value_]) VALUES ('b9263a7d-071e-42f8-a97d-74a04a52d549', '2023-10-16 10:13:12.313954', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', '2023-10-16 10:13:12.313954', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', 'fiManager', '冯雅琴', 'defaultAuditor', 'U20230406013');
INSERT INTO [dbo].[t_flow_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [key_], [remark], [type_], [value_]) VALUES ('c3c3843d-5ac9-470f-aae8-a3282cd50b48', '2023-10-16 10:13:12.357352', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', '2023-10-16 10:13:12.357352', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', 'taxOfficer', '叶冼婷', 'defaultAuditor', 'U20230406007');
INSERT INTO [dbo].[t_flow_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [key_], [remark], [type_], [value_]) VALUES ('e534175b-e50f-452a-86c2-9d05e8670f08', '2023-10-16 10:13:12.354842', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', '2023-10-16 10:13:12.354842', '45af5ac3-9c89-4244-9f8d-ddc056b0e7b1', '阿伯塔管理员', 'ceo', '何爱平', 'defaultAuditor', 'U20230406002');

------------------------------------
--- 导入t_flow_setting: 预设领导
------------------------------------