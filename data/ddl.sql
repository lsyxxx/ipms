-- 业务表中流程关联字段类型，和ACT_表保持一致
alter table wf_inv alter COLUMN proc_inst_id nvarchar(64);
alter table wf_inv alter COLUMN proc_def_id nvarchar(64);
alter table wf_inv alter COLUMN proc_def_key nvarchar(64);
alter table wf_loan alter COLUMN proc_inst_id nvarchar(64);
alter table wf_loan alter COLUMN proc_def_id nvarchar(64);
alter table wf_loan alter COLUMN proc_def_key nvarchar(64);
alter table wf_pay_voucher alter COLUMN proc_inst_id nvarchar(64);
alter table wf_pay_voucher alter COLUMN proc_def_id nvarchar(64);
alter table wf_pay_voucher alter COLUMN proc_def_key nvarchar(64);
alter table wf_rbs alter COLUMN proc_inst_id nvarchar(64);
alter table wf_rbs alter COLUMN proc_def_id nvarchar(64);
alter table wf_rbs alter COLUMN proc_def_key nvarchar(64);
alter table wf_trip alter COLUMN proc_inst_id nvarchar(64);
alter table wf_trip alter COLUMN proc_def_id nvarchar(64);
alter table wf_trip alter COLUMN proc_def_key nvarchar(64);
ALTER TABLE T_car_Info add FilePath varchar(max);
ALTER TABLE T_EmployeeInfo ADD ContractFile varchar(max);
ALTER TABLE T_EmployeeInfo ADD IsExit BIT;
ALTER TABLE T_EmployeeInfo ADD Company varchar(32);

-- 设置离职人员标记
update t_employeeinfo set IsExit = 1 where Id = '0af3dce6-3dc6-4d15-a0b1-adc563c867b8';
update t_employeeinfo set IsExit = 1 where Id = '21cfab33-7bba-4887-b966-b6673f5434e3';
update t_employeeinfo set IsExit = 1 where Id = '691114b4-7a36-4747-bdc0-7fc3c6fd14e5';
update t_employeeinfo set IsExit = 1 where Id = '8a3eba49-8e86-4481-bc68-6ef6a1ed7eda';
update t_employeeinfo set IsExit = 1 where Id = 'fba4bd45-f08a-4346-a084-8c524f013efc';
update t_employeeinfo set IsExit = 1 where Id = 'f539520b-631a-4b3a-b59f-aef4fa1e57a9';
update t_employeeinfo set IsExit = 1 where Id = '47941593-d7da-4555-b556-9fa3ccffd888';
update t_employeeinfo set IsExit = 1 where Id = '72017e78-c764-4229-9055-797aa06f6da3';
update t_employeeinfo set IsExit = 1 where Id = 'd3110626-0ebe-490f-a1b7-935c00e32310';
update t_employeeinfo set IsExit = 1 where Id = '54af05a4-3b8d-4d26-9bfb-b856bb3c90ee';
update t_employeeinfo set IsExit = 1 where Id = 'e727f64a-727d-4255-82a1-bebc88b199bb';
update t_employeeinfo set IsExit = 1 where Id = '2c298817-e0b2-4648-b2bd-33137d297061';
update t_employeeinfo set IsExit = 1 where Id = '1a0c0fc4-22e4-4999-88b1-9df20935cd88';
update t_employeeinfo set IsExit = 1 where Id = 'a046b267-0e71-40e9-aa8b-984781461938';
update t_employeeinfo set IsExit = 1 where Id = '98c41af5-2c48-4ac1-a35d-e26b12abc34e';
update t_employeeinfo set IsExit = 1 where Id = 'c29da34b-cf9e-4adb-894b-f3168b649084';
update t_employeeinfo set IsExit = 1 where Id = 'ed025c4c-200a-4950-b5ff-421ed24d92a4';
update t_employeeinfo set IsExit = 1 where Id = '8d1fe13f-c40d-4802-adfc-2158148e8757';
update t_employeeinfo set IsExit = 1 where Id = '773722fb-b87b-4ce4-b4b6-ee28e5fe793f';
update t_employeeinfo set IsExit = 1 where Id = 'd19fd0cd-998f-4c8f-ae0a-93fe1b2ed794';
update t_employeeinfo set IsExit = 1 where Id = '051d0b3c-a447-4f26-82bc-a9974e6a2e6c';
update t_employeeinfo set IsExit = 1 where Id = '8ed2331f-1698-48a7-aa17-ca3da9030310';
update t_employeeinfo set IsExit = 1 where Id = '606a0ea2-2da1-481d-8a86-8fb1a9fa2a2a';
update t_employeeinfo set IsExit = 1 where Id = 'b433edfb-4c59-41ea-9373-79effea686f6';
update t_employeeinfo set IsExit = 1 where Id = 'e0efc41d-d49f-4dc7-aef4-4cef5435fd68';
update t_employeeinfo set IsExit = 1 where Id = 'ea19c32b-e839-4aad-99b0-aa957ce1e228';
update t_employeeinfo set IsExit = 1 where Id = '3105923f-635c-467f-b19e-f4257d971882';
update t_employeeinfo set IsExit = 1 where Id = '3eab69c8-11ab-4ff1-9fe7-aaf7153ecb7b';
update t_employeeinfo set IsExit = 1 where Id = 'ee2090a1-c106-42c9-95d6-107c19bad6bf';
update t_employeeinfo set IsExit = 1 where Id = 'a831652a-ca4b-4b93-b192-1a5946abf503';
update t_employeeinfo set IsExit = 1 where Id = '5a990809-a174-4d50-93fe-9dc5e1baacb9';
update t_employeeinfo set IsExit = 1 where Id = '50d3c0d4-931e-4a5c-aff7-2b588e724123';
update t_employeeinfo set IsExit = 1 where Id = '20c1b4d1-610e-494d-b772-53e643380e80';
update t_employeeinfo set IsExit = 1 where Id = 'dedf3882-0f75-440b-b359-271c32a9cb9e';
update t_employeeinfo set IsExit = 1 where Id = 'e19dd706-3cd9-4abc-811b-7914a92630d9';



--- T_ENUMLIB中新增开口合同/闭口合同类型
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT]) VALUES (N'CONTYPE1', '1', N'contractPriceType', N'开口合同', N'合同定价类型', NULL, NULL, NULL);
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT]) VALUES (N'CONTYPE2', '2', N'contractPriceType', N'闭口合同', N'合同定价类型', NULL, NULL, NULL);
-- T_ENUMLIB中新增合同乙方类型
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT]) VALUES (N'PB1', '1', N'contractPartyB', N'阿伯塔', N'合同乙方', NULL, NULL, NULL);
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT]) VALUES (N'PB2', '2', N'contractPartyB', N'吉瑞达', N'合同乙方', NULL, NULL, NULL);
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT]) VALUES (N'PB3', '3', N'contractPartyB', N'西北大学', N'合同乙方', NULL, NULL, NULL);


-- 20240527
alter table T_ENUMLIB alter column EXTEND VARCHAR(255);
-- 修改提醒时间
-- 身份证
update T_EmployeeInfo_ListUpload set tixingDay = 0 where ZsFtypeId = 1;
-- 身份证反面
update T_EmployeeInfo_ListUpload set tixingDay = 0 where ZsFtypeId = 2;
-- 学历证
update T_EmployeeInfo_ListUpload set tixingDay = 0 where ZsFtypeId = 3;
-- 学位证
update T_EmployeeInfo_ListUpload set tixingDay = 0 where ZsFtypeId = 4;
-- 上岗证
update T_EmployeeInfo_ListUpload set tixingDay = 0 where ZsFtypeId = 5;
-- 职称证
update T_EmployeeInfo_ListUpload set tixingDay = 0 where ZsFtypeId = 8;
-- 个人照
update T_EmployeeInfo_ListUpload set tixingDay = 0 where ZsFtypeId = 7;

-- 增加字典：行政通知状态
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT], [EXTEND]) VALUES (N'EnumStatus_0', '0', N'EnumPublishStatus', N'草稿', N'通知状态', NULL, NULL, NULL, 'int');
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT], [EXTEND]) VALUES (N'EnumStatus_1', '1', N'EnumPublishStatus', N'已发布', N'通知状态', NULL, NULL, NULL, 'int');
-- 增加字典：是否回复
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT], [EXTEND]) VALUES (N'EnumHFType_1', '1', N'EnumHFType', N'已回复', N'是否回复', NULL, NULL, NULL, NULL);
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT], [EXTEND]) VALUES (N'EnumHFType_2', '2', N'EnumHFTtype', N'未回复', N'是否回复', NULL, NULL, NULL, NULL);


alter table wf_trip_main alter COLUMN proc_inst_id nvarchar(64);
alter table wf_trip_main alter COLUMN proc_def_id nvarchar(64);
alter table wf_trip_main alter COLUMN proc_def_key nvarchar(64);


-- 行政通知添加 文件类型
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT], [EXTEND]) VALUES (NEWID(), '4', N'EnumFileType', N'会议纪要', N'文件类型', NULL, NULL, NULL, NULL);
INSERT INTO [dbo].[T_ENUMLIB] ([ID], [FID], [FTYPEID], [FNAME], [FDESC], [OPERATOR], [OPERATEDATE], [OPERATEDEPT], [EXTEND]) VALUES (NEWID(), '5', N'EnumFileType', N'规章制度', N'文件类型', NULL, NULL, NULL, NULL);

-- 考勤设置
INSERT INTO [dbo].[T_SystemSetting] ([Id], [ftypeid], [Fvalue]) VALUES ('wot_deadline', '加班提交截止日期', '5');
INSERT INTO [dbo].[T_SystemSetting] ([Id], [ftypeid], [Fvalue]) VALUES ('attendance_start', '月考勤开始日期(包含)', '26');
INSERT INTO [dbo].[T_SystemSetting] ([Id], [ftypeid], [Fvalue]) VALUES ('attendance_end', '月考勤结束日期(包含)', '25');

-- 添加野外考勤-组件类型
INSERT INTO [dbo].[CategoryType] ([Id], [Name], [CreateTime]) VALUES ('FieldWork_ComponentType', N'野外考勤-组件类型', '2024-07-26 11:50:17.030');

-- 野外考勤-组件类型-单选/多选
INSERT INTO [ZJData].[dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES ('c8c86cf5-45e4-4296-93cb-0aa81d08191b', '单选', 'fw_radio', '1', '1', 0, '单选', 'FieldWork_ComponentType', '2024-07-26 11:50:40.0517350', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-07-26 11:50:40.0514570', '', '');
INSERT INTO [ZJData].[dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES ('e840ac08-ec25-42de-9f7f-1c287768248b', '多选', 'fw_checkbox', '2', '1', 1, '多选', 'FieldWork_ComponentType', '2024-07-26 11:50:55.0797642', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-07-26 11:50:55.0797627', '', '');

-----------------------------
-- 固定资产：配置
-----------------------------

-- 固定资产-增加方式
INSERT INTO [dbo].[CategoryType] ([Id], [Name], [CreateTime]) VALUES ('fixedAssets_addType', N'固定资产-增加方式', '2024-09-02 13:37:55.613');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES ('007186c7-ff56-42d4-9dcc-aaeff16b1683', '无票', 'fixedAssets_addType_3', '3', '1', 3, '', 'fixedAssets_addType', '2024-09-02 13:49:23.1620381', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:49:23.1620364', '', '');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES ('069d6ae0-c952-403b-936d-4a7cc2e49156', '新购入', 'fixedAssets_addType_1', '1', '1', 0, '', 'fixedAssets_addType', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES ('72513d60-ade4-4caa-8085-4c022bce7d06', '其他', 'fixedAssets_addType_4', '4', '1', 4, '', 'fixedAssets_addType', '2024-09-02 13:49:44.8039414', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:49:44.8039407', '', '');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES ('c48ad371-8e30-46a6-926f-f2d109439124', '二手购入', 'fixedAssets_addType', '2', '1', 2, '', 'fixedAssets_addType', '2024-09-02 13:48:22.6398303', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:22.6398295', '', '');

--固定资产-经济用途
INSERT INTO [dbo].[CategoryType] ([Id], [Name], [CreateTime]) VALUES ('fixedAssets_usageType', N'固定资产-经济用途', '2024-09-02 13:37:55.613');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '生产', 'fixedAssets_usageType_1', '1', '1', 1, '', 'fixedAssets_usageType', '2024-09-02 13:49:23.1620381', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:49:23.1620364', '', '');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '管理', 'fixedAssets_usageType_2', '2', '1', 2, '', 'fixedAssets_usageType', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '其他', 'fixedAssets_addType_3', '3', '1', 3, '', 'fixedAssets_usageType', '2024-09-02 13:49:44.8039414', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:49:44.8039407', '', '');

-- 固定资产-使用状况
INSERT INTO [dbo].[CategoryType] ([Id], [Name], [CreateTime]) VALUES ('fixedAssets_enabled', N'固定资产-使用状况', '2024-09-02 13:37:55.613');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '启用', 'fixedAssets_enabled_1', '1', '1', 1, '', 'fixedAssets_enabled', '2024-09-02 13:49:23.1620381', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:49:23.1620364', '', '');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '停用', 'fixedAssets_enabled_2', '0', '1', 2, '', 'fixedAssets_enabled', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');

-- 固定资产-计量单位
INSERT INTO [dbo].[CategoryType] ([Id], [Name], [CreateTime]) VALUES ('fixedAssets_unit', N'固定资产-计量单位', '2024-09-02 13:37:55.613');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '个', 'fixedAssets_unit_1', '1', '1', 1, '', 'fixedAssets_unit', '2024-09-02 13:49:23.1620381', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:49:23.1620364', '', '');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '台', 'fixedAssets_unit_2', '2', '1', 2, '', 'fixedAssets_unit', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '套', 'fixedAssets_unit_3', '3', '1', 3, '', 'fixedAssets_unit', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '部', 'fixedAssets_unit_4', '4', '1', 4, '', 'fixedAssets_unit', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');

-- 固定资产-资产分类
INSERT INTO [dbo].[CategoryType] ([Id], [Name], [CreateTime]) VALUES ('fixedAssets_assetType', N'固定资产-资产分类', '2024-09-02 13:37:55.613');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '房屋建筑物', 'fixedAssets_assetType_1', '1', '1', 1, '', 'fixedAssets_assetType', '2024-09-02 13:49:23.1620381', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:49:23.1620364', '', '');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '实验设备', 'fixedAssets_assetType_2', '2', '1', 2, '', 'fixedAssets_assetType', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '器具工具家具', 'fixedAssets_assetType_3', '3', '1', 3, '', 'fixedAssets_assetType', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '运输工具', 'fixedAssets_assetType_4', '4', '1', 4, '', 'fixedAssets_assetType', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');
INSERT INTO [dbo].[Category] ([Id], [Name], [DtCode], [DtValue], [Enable], [SortNo], [Description], [TypeId], [CreateTime], [CreateUserId], [CreateUserName], [UpdateTime], [UpdateUserId], [UpdateUserName]) VALUES (NEWID(), '电子办公设备', 'fixedAssets_assetType_5', '5', '1', 5, '', 'fixedAssets_assetType', '2024-09-02 13:38:11.1912165', '00000000-0000-0000-0000-000000000000', '超级管理员', '2024-09-02 13:48:58.8217500', '00000000-0000-0000-0000-000000000000', '超级管理员');


--- 新增野外考勤配置
INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_], [bg_color], [is_work], [short_name], [symbol_], [version_], [vid]) VALUES ('32', '1900-01-01 09:42:56.927067', '621faa40-f45c-4da8-9a8f-65b0c5353f40', '刘宋菀', '1900-01-01 09:42:56.927067', '621faa40-f45c-4da8-9a8f-65b0c5353f40', '刘宋菀', 1, '现场岩心扫描负责人', '1', '现场岩心扫描', 30.00, '现场岩心扫描负责人', 180.00, 32, 'background-color:#FABF8F;', 210.00, '元/天/人', '#FABF8F', '1', 'XY1', NULL, 1, '32');
INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_], [bg_color], [is_work], [short_name], [symbol_], [version_], [vid]) VALUES ('33', '1900-01-01 09:42:56.927067', '621faa40-f45c-4da8-9a8f-65b0c5353f40', '刘宋菀', '1900-01-01 09:42:56.927067', '621faa40-f45c-4da8-9a8f-65b0c5353f40', '刘宋菀', 1, '现场岩心扫描组员', '1', '现场岩心扫描', 30.00, '现场岩心扫描组员', 150.00, 33, 'background-color:#FABF8F;', 180.00, '元/天/人', '#FABF8F', '1', 'XY2', NULL, 1, '33');




--- 插入u_sig userid
UPDATE u_sig
SET user_id = (
    SELECT u.id
    FROM [user] u
WHERE u.empnum = u_sig.job_number
    )
WHERE EXISTS (
    SELECT 1
    FROM [user] u
    WHERE u.empnum = u_sig.job_number
    );

-- 插入u_sig userid 指定用户
UPDATE u_sig
SET user_id = (
    SELECT u.id
    FROM [user] u
WHERE u.empnum = '186'
    )
WHERE EXISTS (
    SELECT 1
    FROM [user] u
    WHERE u.empnum = '186'
    );


---- 更新final_id, final_name
update wf_pur_main set purchaser_id = 'U20230406026' where (purchaser_id is null or purchaser_id = '');

update wf_pur_dtl set final_id = 'U20230406026' where final_id is null or final_id = '';
update wf_pur_dtl set final_name = '耿丽珍' where final_name is null or final_name = '';
update wf_pur_dtl set accept_items = '品名、规格、等级、生产日期、有效期、成分、包装、外观、贮存、数量、合格证明、说明书、保修卡' where accept_items is null or accept_items = '';