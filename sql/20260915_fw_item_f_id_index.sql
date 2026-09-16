-- 野外考勤：为补助明细关联字段增加索引
-- 方言：SQL Server 2014
-- 影响表：dbo.fw_item
-- 用途：优化 fw_record 与 fw_item 按 fw_item.f_id 进行的关联及批量明细查询
-- 可重复执行：索引已存在时不重复创建

IF OBJECT_ID('dbo.fw_item', 'U') IS NULL
BEGIN
    RAISERROR('表 dbo.fw_item 不存在，无法创建索引 idx_fw_item_f_id。', 16, 1);
END
ELSE IF COL_LENGTH('dbo.fw_item', 'f_id') IS NULL
BEGIN
    RAISERROR('字段 dbo.fw_item.f_id 不存在，无法创建索引 idx_fw_item_f_id。', 16, 1);
END
ELSE IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.fw_item')
      AND name = 'idx_fw_item_f_id'
)
BEGIN
    CREATE NONCLUSTERED INDEX idx_fw_item_f_id
        ON dbo.fw_item (f_id);
END;
GO
