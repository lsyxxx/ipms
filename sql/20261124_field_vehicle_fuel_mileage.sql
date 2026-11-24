-- 野外用车加油明细增加可空公里数。SQL Server 2014 兼容。
-- 部署写入公里数的后端前，在目标业务库执行；历史记录保持 NULL。
-- 新环境若已由更新后的初建脚本创建 mileage 与 CK_ofvf_mileage，本脚本跳过。可重复执行。
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.oa_field_vehicle_fuel', N'U') IS NULL
        THROW 50001, N'加油明细表不存在，请先执行初建脚本', 1;

    IF COL_LENGTH(N'dbo.oa_field_vehicle_fuel', N'mileage') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_fuel ADD mileage DECIMAL(18,2) NULL;

    -- 新增列后的约束放进独立编译批次，避免同批编译看不到新列。
    EXEC sys.sp_executesql N'
    IF OBJECT_ID(N''dbo.CK_ofvf_mileage'', N''C'') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_fuel WITH CHECK
            ADD CONSTRAINT CK_ofvf_mileage CHECK (mileage IS NULL OR mileage >= 0);
    ';

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
