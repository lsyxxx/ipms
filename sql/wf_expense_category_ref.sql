-- 业务单据费用分类快照字段（SQL Server 2014 兼容）
-- 仅持久化 code/name，不存主数据 id

IF COL_LENGTH('dbo.wf_rbs', 'exp_cat_code') IS NULL
BEGIN
    ALTER TABLE dbo.wf_rbs ADD exp_cat_code VARCHAR(64) NULL;
END
GO

IF COL_LENGTH('dbo.wf_rbs', 'exp_cat_name') IS NULL
BEGIN
    ALTER TABLE dbo.wf_rbs ADD exp_cat_name VARCHAR(128) NULL;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_wf_rbs_exp_cat_code' AND object_id = OBJECT_ID('dbo.wf_rbs'))
BEGIN
    CREATE INDEX idx_wf_rbs_exp_cat_code ON dbo.wf_rbs (exp_cat_code);
END
GO

IF COL_LENGTH('dbo.wf_pay_voucher', 'exp_cat_code') IS NULL
BEGIN
    ALTER TABLE dbo.wf_pay_voucher ADD exp_cat_code VARCHAR(64) NULL;
END
GO

IF COL_LENGTH('dbo.wf_pay_voucher', 'exp_cat_name') IS NULL
BEGIN
    ALTER TABLE dbo.wf_pay_voucher ADD exp_cat_name VARCHAR(128) NULL;
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_wf_pay_voucher_exp_cat_code' AND object_id = OBJECT_ID('dbo.wf_pay_voucher'))
BEGIN
    CREATE INDEX idx_wf_pay_voucher_exp_cat_code ON dbo.wf_pay_voucher (exp_cat_code);
END
GO
