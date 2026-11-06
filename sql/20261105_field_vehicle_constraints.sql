-- 野外用车：为 Hibernate 自动建表后的现有结构补齐检查约束和外键。
-- SQL Server 2014 兼容；不修改、不删除已有业务记录，不变更字段类型。
-- 先确认已有两表字段与 20261105_field_vehicle.sql 一致；已有非法数据时整笔回滚。
-- 旧版未结束汇总为空的环境，先执行 20261105_field_vehicle_realtime_totals.sql。
-- 初建脚本已创建同名约束时，本脚本跳过；可重复执行。
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.oa_field_vehicle_use', N'U') IS NULL
       OR OBJECT_ID(N'dbo.oa_field_vehicle_fuel', N'U') IS NULL
        THROW 50001, N'野外用车主表或加油表不存在，请先执行初建脚本', 1;

    IF OBJECT_ID(N'dbo.CK_ofvu_status', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_use WITH CHECK
            ADD CONSTRAINT CK_ofvu_status CHECK (status IN (0, 1, 2));

    IF OBJECT_ID(N'dbo.CK_ofvu_start_mileage', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_use WITH CHECK
            ADD CONSTRAINT CK_ofvu_start_mileage
            CHECK (start_mileage IS NULL OR start_mileage >= 0);

    IF OBJECT_ID(N'dbo.CK_ofvu_expected_date', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_use WITH CHECK
            ADD CONSTRAINT CK_ofvu_expected_date CHECK (
                expected_return_date IS NULL OR use_date IS NULL
                OR expected_return_date >= use_date
            );

    IF OBJECT_ID(N'dbo.CK_ofvu_required', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_use WITH CHECK
            ADD CONSTRAINT CK_ofvu_required CHECK (
                status = 0
                OR (
                    car_id IS NOT NULL AND LEN(LTRIM(RTRIM(car_id))) > 0
                    AND car_no IS NOT NULL AND LEN(LTRIM(RTRIM(car_no))) > 0
                    AND purpose IS NOT NULL AND LEN(LTRIM(RTRIM(purpose))) > 0
                    AND departure_place IS NOT NULL AND LEN(LTRIM(RTRIM(departure_place))) > 0
                    AND destination IS NOT NULL AND LEN(LTRIM(RTRIM(destination))) > 0
                    AND use_date IS NOT NULL AND start_mileage IS NOT NULL
                )
            );

    IF OBJECT_ID(N'dbo.CK_ofvu_finished', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_use WITH CHECK
            ADD CONSTRAINT CK_ofvu_finished CHECK (
                (status IN (0, 1) AND end_date IS NULL AND end_mileage IS NULL
                    AND return_user_id IS NULL AND return_username IS NULL AND return_time IS NULL)
                OR
                (status = 2 AND end_date IS NOT NULL AND end_mileage IS NOT NULL
                    AND return_user_id IS NOT NULL AND return_username IS NOT NULL
                    AND return_time IS NOT NULL AND end_date >= use_date
                    AND end_mileage >= start_mileage)
            );

    IF OBJECT_ID(N'dbo.CK_ofvu_fuel_summary', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_use WITH CHECK
            ADD CONSTRAINT CK_ofvu_fuel_summary CHECK (
                fuel_total_quantity IS NOT NULL AND fuel_total IS NOT NULL AND fuel_count IS NOT NULL
                AND (status <> 0 OR fuel_count = 0)
                AND (
                    (fuel_count = 0 AND fuel_total_quantity = 0 AND fuel_total = 0)
                    OR (fuel_count > 0 AND fuel_total_quantity > 0 AND fuel_total > 0)
                )
            );

    IF OBJECT_ID(N'dbo.CK_ofvf_amount', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_fuel WITH CHECK
            ADD CONSTRAINT CK_ofvf_amount CHECK (amount > 0);

    IF OBJECT_ID(N'dbo.CK_ofvf_quantity', N'C') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_fuel WITH CHECK
            ADD CONSTRAINT CK_ofvf_quantity CHECK (fuel_quantity > 0);

    IF OBJECT_ID(N'dbo.FK_ofvf_main', N'F') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_fuel WITH CHECK
            ADD CONSTRAINT FK_ofvf_main FOREIGN KEY (m_id)
            REFERENCES dbo.oa_field_vehicle_use (id);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
