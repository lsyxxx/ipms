-- 采购申请：为采购明细关联主表字段增加索引
-- 方言：SQL Server 2014
-- 影响表：dbo.wf_pur_dtl
-- 用途：优化按采购主表 ID 关联明细及 EXISTS 子查询
-- 可重复执行：索引已存在时不重复创建

IF OBJECT_ID('dbo.wf_pur_dtl', 'U') IS NULL
BEGIN
    RAISERROR('表 dbo.wf_pur_dtl 不存在，无法创建索引 idx_wf_pur_dtl_m_id。', 16, 1);
END
ELSE IF COL_LENGTH('dbo.wf_pur_dtl', 'm_id') IS NULL
BEGIN
    RAISERROR('字段 dbo.wf_pur_dtl.m_id 不存在，无法创建索引 idx_wf_pur_dtl_m_id。', 16, 1);
END
ELSE IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE object_id = OBJECT_ID('dbo.wf_pur_dtl')
      AND name = 'idx_wf_pur_dtl_m_id'
)
BEGIN
    CREATE NONCLUSTERED INDEX idx_wf_pur_dtl_m_id
        ON dbo.wf_pur_dtl (m_id);
END;
GO
