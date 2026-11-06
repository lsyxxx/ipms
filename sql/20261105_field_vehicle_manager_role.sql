-- 野外用车负责人角色初始化（SQL Server 2014 兼容）。
-- 字段按现有 dbo.Role 实际结构核实；仅创建角色，不自动授予任何普通用户。
-- 创建后通过现有用户角色授权界面分配，并重新登录以刷新认证 authorities。
SET XACT_ABORT ON;

IF OBJECT_ID(N'dbo.Role', N'U') IS NULL
BEGIN
    THROW 50001, N'缺少现有 Role 表，请确认所连接业务数据库', 1;
END;

IF NOT EXISTS (SELECT 1 FROM dbo.Role WHERE Id = 'JS_OA_FIELD_VEHICLE_MANAGER')
BEGIN
    INSERT INTO dbo.Role (Id, Name, Status, CreateTime)
    VALUES ('JS_OA_FIELD_VEHICLE_MANAGER', N'野外用车负责人', 1, GETDATE());
END;
