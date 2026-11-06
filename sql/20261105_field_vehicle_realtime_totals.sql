-- 野外用车加油汇总实时落库升级，兼容 SQL Server 2014。
-- 复用既有 fuel_total_quantity、fuel_total、fuel_count，不重复新增同义列。
-- 部署新版本前暂停加油写操作，先执行本脚本，再启用新版服务。
-- 仅按现有明细回填草稿／使用中的汇总，保留已结束结算值及全部明细、审计和车辆状态。
-- 失败整笔回滚；可以重复执行，不连接或修改其他数据库。
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.oa_field_vehicle_use', N'U') IS NULL
       OR OBJECT_ID(N'dbo.oa_field_vehicle_fuel', N'U') IS NULL
        THROW 50001, N'野外用车两表不存在，请先执行初建脚本', 1;

    -- 与应用统一先主表、后明细的锁顺序，避免迁移期间明细变动导致汇总过期。
    DECLARE @MainRows BIGINT, @FuelRows BIGINT;
    SELECT @MainRows = COUNT_BIG(*) FROM dbo.oa_field_vehicle_use WITH (TABLOCKX, HOLDLOCK);
    SELECT @FuelRows = COUNT_BIG(*) FROM dbo.oa_field_vehicle_fuel WITH (TABLOCKX, HOLDLOCK);

    IF OBJECT_ID(N'dbo.CK_ofvu_fuel_summary', N'C') IS NOT NULL
        ALTER TABLE dbo.oa_field_vehicle_use DROP CONSTRAINT CK_ofvu_fuel_summary;

    UPDATE m
    SET fuel_total_quantity = COALESCE(f.total_quantity, 0),
        fuel_total = COALESCE(f.total_amount, 0),
        fuel_count = COALESCE(f.total_count, 0)
    FROM dbo.oa_field_vehicle_use m
    LEFT JOIN (
        SELECT m_id, SUM(fuel_quantity) AS total_quantity, SUM(amount) AS total_amount,
               COUNT_BIG(*) AS total_count
        FROM dbo.oa_field_vehicle_fuel
        GROUP BY m_id
    ) f ON f.m_id = m.id
    WHERE m.status IN (0, 1)
      AND (m.fuel_total_quantity IS NULL OR m.fuel_total IS NULL OR m.fuel_count IS NULL
           OR m.fuel_total_quantity <> COALESCE(f.total_quantity, 0)
           OR m.fuel_total <> COALESCE(f.total_amount, 0)
           OR m.fuel_count <> COALESCE(f.total_count, 0));

    ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN fuel_total_quantity DECIMAL(38,3) NOT NULL;
    ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN fuel_total DECIMAL(38,2) NOT NULL;
    ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN fuel_count INT NOT NULL;

    IF NOT EXISTS (SELECT 1 FROM sys.default_constraints d INNER JOIN sys.columns c
                   ON c.object_id = d.parent_object_id AND c.column_id = d.parent_column_id
                   WHERE d.parent_object_id = OBJECT_ID(N'dbo.oa_field_vehicle_use') AND c.name = N'fuel_total_quantity')
        ALTER TABLE dbo.oa_field_vehicle_use ADD CONSTRAINT DF_ofvu_total_quantity DEFAULT (0) FOR fuel_total_quantity;
    IF NOT EXISTS (SELECT 1 FROM sys.default_constraints d INNER JOIN sys.columns c
                   ON c.object_id = d.parent_object_id AND c.column_id = d.parent_column_id
                   WHERE d.parent_object_id = OBJECT_ID(N'dbo.oa_field_vehicle_use') AND c.name = N'fuel_total')
        ALTER TABLE dbo.oa_field_vehicle_use ADD CONSTRAINT DF_ofvu_total_amount DEFAULT (0) FOR fuel_total;
    IF NOT EXISTS (SELECT 1 FROM sys.default_constraints d INNER JOIN sys.columns c
                   ON c.object_id = d.parent_object_id AND c.column_id = d.parent_column_id
                   WHERE d.parent_object_id = OBJECT_ID(N'dbo.oa_field_vehicle_use') AND c.name = N'fuel_count')
        ALTER TABLE dbo.oa_field_vehicle_use ADD CONSTRAINT DF_ofvu_fuel_count DEFAULT (0) FOR fuel_count;

    ALTER TABLE dbo.oa_field_vehicle_use WITH CHECK ADD CONSTRAINT CK_ofvu_fuel_summary CHECK (
        fuel_total_quantity IS NOT NULL AND fuel_total IS NOT NULL AND fuel_count IS NOT NULL
        AND (status <> 0 OR fuel_count = 0)
        AND (
            (fuel_count = 0 AND fuel_total_quantity = 0 AND fuel_total = 0)
            OR (fuel_count > 0 AND fuel_total_quantity > 0 AND fuel_total > 0)
        )
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
