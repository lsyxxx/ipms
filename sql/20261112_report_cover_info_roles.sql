-- 报告封面编辑、物理删除角色初始化，兼容 SQL Server 2014。
-- 仅创建角色；通过现有用户角色授权界面分配，重新登录刷新 authorities。
SET XACT_ABORT ON;

IF OBJECT_ID(N'dbo.Role', N'U') IS NULL
BEGIN
    THROW 50001, N'缺少现有 Role 表，请确认所连接业务数据库', 1;
END;

IF NOT EXISTS (SELECT 1 FROM dbo.Role WHERE Id = 'JS_REPORT_COVER_EDIT')
BEGIN
    INSERT INTO dbo.Role (Id, Name, Status, CreateTime)
    VALUES ('JS_REPORT_COVER_EDIT', N'报告封面信息编辑', 1, GETDATE());
END;

IF NOT EXISTS (SELECT 1 FROM dbo.Role WHERE Id = 'JS_REPORT_COVER_DEL')
BEGIN
    INSERT INTO dbo.Role (Id, Name, Status, CreateTime)
    VALUES ('JS_REPORT_COVER_DEL', N'报告封面信息删除', 1, GETDATE());
END;
