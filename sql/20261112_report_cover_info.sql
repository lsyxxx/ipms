-- 报告封面信息维护；兼容 SQL Server 2014。
-- 列表保存 JSON 文本，由应用转换；不依赖 JSON 函数，不建立子表。
-- 审计列沿用 com.abt.common.model.AuditInfo。
IF OBJECT_ID(N'dbo.report_cover_info', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.report_cover_info (
        id               VARCHAR(36)    NOT NULL,
        cover_name       NVARCHAR(256)  NOT NULL,
        check_module_ids NVARCHAR(MAX)  NOT NULL CONSTRAINT DF_report_cover_info_modules DEFAULT (N'[]'),
        item_code        NVARCHAR(64)   NULL,
        sample_spec      NVARCHAR(500)  NULL,
        sample_status    NVARCHAR(200)  NULL,
        instruments      NVARCHAR(MAX)  NOT NULL CONSTRAINT DF_report_cover_info_instruments DEFAULT (N'[]'),
        standards        NVARCHAR(MAX)  NOT NULL CONSTRAINT DF_report_cover_info_standards DEFAULT (N'[]'),
        category         NVARCHAR(64)   NULL,
        version          NVARCHAR(32)   NULL,
        ownership        NVARCHAR(128)  NULL,
        remark           NVARCHAR(1000) NULL,
        status           INT            NOT NULL CONSTRAINT DF_report_cover_info_status DEFAULT (1),
        create_date      DATETIME2(0)   NULL,
        create_userid    VARCHAR(255)   NULL,
        create_username  VARCHAR(255)   NULL,
        update_date      DATETIME2(0)   NULL,
        update_userid    VARCHAR(255)   NULL,
        update_username  VARCHAR(255)   NULL,
        CONSTRAINT PK_report_cover_info PRIMARY KEY CLUSTERED (id),
        CONSTRAINT UQ_report_cover_info_cover_name UNIQUE (cover_name),
        CONSTRAINT CK_report_cover_info_status CHECK (status IN (0, 1)),
        CONSTRAINT CK_report_cover_info_cover_name CHECK (LEN(LTRIM(RTRIM(cover_name))) > 0)
    );

    -- 默认列表按创建时间、ID 倒序；名称已有唯一索引。
    CREATE INDEX IX_report_cover_info_create_date ON dbo.report_cover_info (create_date DESC, id DESC);
END;
GO

-- JSON 内项目 ID 的存在性和唯一归属由 service 在事务内校验。
-- 新增、更新、删除统一申请 sys.sp_getapplock 事务级独占锁，应用账号需要调用权限。
-- 禁止绕过应用直接写 JSON 关联，否则无法保证项目唯一归属。
