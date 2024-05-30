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


