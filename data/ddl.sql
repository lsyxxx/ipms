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


-- 野外考勤补贴项目

-- ----------------------------
-- Records of fw_atd_setting
-- ----------------------------
INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'1', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'流体', N'30.00', N'流体2人作业负责人', N'180.00', N'1', N'background-color:#C5D9F1;', N'210.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'10', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'科研项目', N'30.00', N'科研项目2人作业负责人', N'170.00', N'10', N'background-color:#E6B8B7;', N'200.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'11', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'科研项目', N'30.00', N'科研项目2人作业组员', N'140.00', N'11', N'background-color:#E6B8B7;', N'170.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'12', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'科研项目', N'30.00', N'科研项目1人作业', N'180.00', N'12', N'background-color:#E6B8B7;', N'210.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'13', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'解析', N'30.00', N'解吸2人作业负责人', N'170.00', N'13', N'background-color:#B7DEE8;', N'200.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'14', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'解析', N'30.00', N'解吸2人作业组员', N'140.00', N'14', N'background-color:#B7DEE8;', N'170.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'15', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'解析', N'30.00', N'解吸1人作业', N'180.00', N'15', N'background-color:#B7DEE8;', N'210.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'16', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心扫描(小于40米)', N'30.00', N'岩心扫描负责人(小于40米)', N'120.00', N'16', N'background-color:#CCC0DA;', N'150.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'17', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心扫描(小于40米)', N'30.00', N'岩心扫描组员(小于40米)', N'90.00', N'17', N'background-color:#CCC0DA;', N'120.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'18', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心扫描(40-50米)', N'30.00', N'岩心扫描负责人(40-50米)', N'180.00', N'18', N'background-color:#FCD5B4;', N'210.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'19', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心扫描(40-50米)', N'30.00', N'岩心扫描组员(40-50米)', N'150.00', N'19', N'background-color:#FCD5B4;', N'180.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'2', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'流体', N'30.00', N'流体2人作业组员', N'150.00', N'2', N'background-color:#C5D9F1;', N'180.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'20', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心扫描(51-60米)', N'30.00', N'岩心扫描负责人(51-60米)', N'200.00', N'20', N'background-color:#EBF1DE;', N'230.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'21', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心扫描(51-60米)', N'30.00', N'岩心扫描组员(51-60米)', N'170.00', N'21', N'background-color:#EBF1DE;', N'200.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'22', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'其他', N'30.00', N'其他野外工作', N'170.00', N'22', N'background-color:#F79646;', N'200.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'23', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'其他', N'30.00', N'办理业务或室内工作', N'70.00', N'23', N'background-color:#FCD5B4;', N'100.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'24', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'其他', N'30.00', N'兼职司机人员接送或物资现场配合', N'170.00', N'24', N'background-color:#C5D9F1;', N'200.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'25', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'其他', N'0.00', N'兼职司机出勤', N'15.00', N'25', N'background-color:#92D050;', N'15.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'26', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'其他', N'30.00', N'基地调休', N'0.00', N'26', N'background-color:#00B050;', N'30.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'27', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', N'首创食堂就餐无餐补', N'0', N'西安', N'0.00', N'西安（首创）', N'5.00', N'27', NULL, N'5.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'28', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'西安', N'30.00', N'西安（草滩）', N'5.00', N'28', NULL, N'35.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'3', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'流体', N'30.00', N'流体1人作业', N'180.00', N'3', N'background-color:#C5D9F1;', N'210.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'4', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心', N'30.00', N'岩心2人作业负责人', N'180.00', N'4', N'background-color:#FCD5B4;', N'210.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'5', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心', N'30.00', N'岩心2人作业组员', N'150.00', N'5', N'background-color:#FCD5B4;', N'180.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'6', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'岩心', N'30.00', N'岩心1人作业', N'180.00', N'6', N'background-color:#FCD5B4;', N'210.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'7', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'压裂', N'30.00', N'压裂2人作业负责人', N'170.00', N'7', N'background-color:#F2DCDB;', N'200.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'8', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'压裂', N'30.00', N'压裂2人作业组员', N'150.00', N'8', N'background-color:#F2DCDB;', N'180.00', N'元/天/人')
GO

INSERT INTO [dbo].[fw_atd_setting] ([id], [create_date], [create_userid], [create_username], [update_date], [update_userid], [update_username], [com_type], [desc_], [enabled_], [group_], [meal_allowance], [name], [prod_allowance], [sort], [style_], [sum_allowance], [unit_]) VALUES (N'9', N'2024-07-26 11:25:59.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'2024-07-26 11:26:23.000000', N'621faa40-f45c-4da8-9a8f-65b0c5353f40', N'刘宋菀', N'1', NULL, N'0', N'压裂', N'30.00', N'压裂1人作业', N'150.00', N'9', N'background-color:#F2DCDB;', N'180.00', N'元/天/人')
GO


