-- 费用分类表（SQL Server 2014 兼容）
IF OBJECT_ID(N'dbo.fi_expense_category', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.fi_expense_category (
        id              VARCHAR(36)   NOT NULL,
        code_           VARCHAR(64)   NOT NULL,
        name_           VARCHAR(128)  NOT NULL,
        sort_no         INT           NOT NULL CONSTRAINT DF_fi_expense_category_sort_no DEFAULT (0),
        enabled_        BIT           NOT NULL CONSTRAINT DF_fi_expense_category_enabled DEFAULT (1),
        remark_         VARCHAR(256)  NULL,
        create_userid   VARCHAR(36)   NULL,
        create_username VARCHAR(64)   NULL,
        create_date     DATETIME      NULL,
        update_userid   VARCHAR(36)   NULL,
        update_username VARCHAR(64)   NULL,
        update_date     DATETIME      NULL,
        CONSTRAINT PK_fi_expense_category PRIMARY KEY CLUSTERED (id),
        CONSTRAINT UQ_fi_expense_category_code UNIQUE (code_),
        CONSTRAINT UQ_fi_expense_category_name UNIQUE (name_)
    );

    CREATE INDEX idx_fi_exp_cat_code ON dbo.fi_expense_category (code_);
    CREATE INDEX idx_fi_exp_cat_enabled ON dbo.fi_expense_category (enabled_);
    CREATE INDEX idx_fi_exp_cat_sort ON dbo.fi_expense_category (sort_no);
END
GO

-- 已建表场景：补充名称唯一约束
IF OBJECT_ID(N'dbo.fi_expense_category', N'U') IS NOT NULL
   AND NOT EXISTS (
       SELECT 1 FROM sys.indexes
       WHERE name = N'UQ_fi_expense_category_name'
         AND object_id = OBJECT_ID(N'dbo.fi_expense_category')
   )
BEGIN
    ALTER TABLE dbo.fi_expense_category
        ADD CONSTRAINT UQ_fi_expense_category_name UNIQUE (name_);
END
GO
