-- 野外用车 V1.4：初始化 DDL，2026-11-05。
-- 部署前在目标业务库执行；应用 ddl-auto 不能代替本脚本的条件约束和过滤索引。
-- 仅两张业务表，保留 oa_ 前缀；司机及同行人为纯文本。
-- 草稿允许必填业务列为空；正式状态由应用完整校验并以 CHECK 兜底。
-- 用车日期 use_date；派车时间使用通用审计 create_date。
-- 加油表主关联 m_id（Java mainId）；日期和说明可空，升数必填。
-- 每次加油新增、修改、删除重算主表升数、费用、次数，空记录为零，结束后冻结。
-- 兼容 SQL Server 2014（12.x），在 IPMS 实际业务库执行，不硬编码 USE。
-- 不修改 T_car_Info 或旧里程；车辆 IsUse 由实施后的 ServiceImpl 事务更新。
-- 本脚本用于初建，已有同名但不同结构的表不会自动升级。
-- 若实际执行过 V1.0/V1.1/V1.2，必须另行提供经过审阅的数据迁移脚本。
-- 已有 V1.3 环境升级实时汇总，执行 20261105_field_vehicle_realtime_totals.sql。

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET NUMERIC_ROUNDABORT OFF;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.oa_field_vehicle_use', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.oa_field_vehicle_use (
            id                      VARCHAR(50)     NOT NULL,
            car_id                  VARCHAR(50)     NULL,
            car_no                  NVARCHAR(20)    NULL,
            drivers                 NVARCHAR(MAX)   NULL,
            peers                   NVARCHAR(MAX)   NULL,
            purpose                 NVARCHAR(500)   NULL,
            departure_place         NVARCHAR(200)   NULL,
            destination             NVARCHAR(200)   NULL,
            use_date                DATE            NULL,
            expected_return_date    DATE            NULL,
            remark                  NVARCHAR(1000)  NULL,
            start_mileage           DECIMAL(18,2)   NULL,
            end_date                DATE            NULL,
            end_mileage             DECIMAL(18,2)   NULL,
            fuel_total_quantity     DECIMAL(38,3)   NOT NULL
                CONSTRAINT DF_ofvu_total_quantity DEFAULT (0),
            fuel_total              DECIMAL(38,2)   NOT NULL
                CONSTRAINT DF_ofvu_total_amount DEFAULT (0),
            fuel_count              INT             NOT NULL
                CONSTRAINT DF_ofvu_fuel_count DEFAULT (0),
            status                  INT             NOT NULL,
            return_user_id          VARCHAR(50)     NULL,
            return_username         NVARCHAR(255)   NULL,
            return_time             DATETIME2(3)    NULL,
            create_userid           VARCHAR(255)    NULL,
            create_username         VARCHAR(255)    NULL,
            create_date             DATETIME2(3)    NULL,
            update_userid           VARCHAR(255)    NULL,
            update_username         VARCHAR(255)    NULL,
            update_date             DATETIME2(3)    NULL,
            CONSTRAINT PK_oa_field_vehicle_use PRIMARY KEY CLUSTERED (id),
            CONSTRAINT CK_ofvu_status CHECK (status IN (0, 1, 2)),
            CONSTRAINT CK_ofvu_start_mileage
                CHECK (start_mileage IS NULL OR start_mileage >= 0),
            CONSTRAINT CK_ofvu_expected_date CHECK (
                expected_return_date IS NULL OR use_date IS NULL
                OR expected_return_date >= use_date
            ),
            CONSTRAINT CK_ofvu_required CHECK (
                status = 0
                OR (
                    car_id IS NOT NULL AND LEN(LTRIM(RTRIM(car_id))) > 0
                    AND car_no IS NOT NULL AND LEN(LTRIM(RTRIM(car_no))) > 0
                    AND purpose IS NOT NULL AND LEN(LTRIM(RTRIM(purpose))) > 0
                    AND departure_place IS NOT NULL AND LEN(LTRIM(RTRIM(departure_place))) > 0
                    AND destination IS NOT NULL AND LEN(LTRIM(RTRIM(destination))) > 0
                    AND use_date IS NOT NULL AND start_mileage IS NOT NULL
                )
            ),
            CONSTRAINT CK_ofvu_finished CHECK (
                (status IN (0, 1) AND end_date IS NULL AND end_mileage IS NULL
                    AND return_user_id IS NULL AND return_username IS NULL
                    AND return_time IS NULL)
                OR
                (status = 2 AND end_date IS NOT NULL AND end_mileage IS NOT NULL
                    AND return_user_id IS NOT NULL AND return_username IS NOT NULL
                    AND return_time IS NOT NULL AND end_date >= use_date
                    AND end_mileage >= start_mileage)
            ),
            CONSTRAINT CK_ofvu_fuel_summary CHECK (
                fuel_total_quantity IS NOT NULL AND fuel_total IS NOT NULL AND fuel_count IS NOT NULL
                AND (status <> 0 OR fuel_count = 0)
                AND (
                    (fuel_count = 0 AND fuel_total_quantity = 0 AND fuel_total = 0)
                    OR (fuel_count > 0 AND fuel_total_quantity > 0 AND fuel_total > 0)
                )
            )
        );
    END;

    IF OBJECT_ID(N'dbo.oa_field_vehicle_fuel', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.oa_field_vehicle_fuel (
            id                  VARCHAR(50)     NOT NULL,
            m_id                VARCHAR(50)     NOT NULL,
            fuel_date           DATE            NULL,
            amount              DECIMAL(18,2)   NOT NULL,
            fuel_quantity       DECIMAL(18,3)   NOT NULL,
            remark              NVARCHAR(1000)  NULL,
            create_userid       VARCHAR(255)    NULL,
            create_username     VARCHAR(255)    NULL,
            create_date         DATETIME2(3)    NULL,
            update_userid       VARCHAR(255)    NULL,
            update_username     VARCHAR(255)    NULL,
            update_date         DATETIME2(3)    NULL,
            CONSTRAINT PK_oa_field_vehicle_fuel PRIMARY KEY CLUSTERED (id),
            CONSTRAINT FK_ofvf_main FOREIGN KEY (m_id)
                REFERENCES dbo.oa_field_vehicle_use (id),
            CONSTRAINT CK_ofvf_amount CHECK (amount > 0),
            CONSTRAINT CK_ofvf_quantity CHECK (fuel_quantity > 0)
        );
    END;

    -- 草稿不占车；此索引仅兜底野外用车内部的正式占用唯一性。
    -- 不约束旧 T_card_apply，也不代替 ServiceImpl 更新共享 CarInfo.IsUse。
    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE object_id = OBJECT_ID(N'dbo.oa_field_vehicle_use')
          AND name = N'UX_ofvu_car_using'
    )
        CREATE UNIQUE NONCLUSTERED INDEX UX_ofvu_car_using
            ON dbo.oa_field_vehicle_use (car_id)
            WHERE status = 1;

    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE object_id = OBJECT_ID(N'dbo.oa_field_vehicle_use')
          AND name = N'IX_ofvu_use_date'
    )
        CREATE NONCLUSTERED INDEX IX_ofvu_use_date
            ON dbo.oa_field_vehicle_use (use_date DESC, create_date DESC, id DESC);

    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE object_id = OBJECT_ID(N'dbo.oa_field_vehicle_use')
          AND name = N'IX_ofvu_car_use_date'
    )
        CREATE NONCLUSTERED INDEX IX_ofvu_car_use_date
            ON dbo.oa_field_vehicle_use (car_id, use_date DESC, id DESC);

    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE object_id = OBJECT_ID(N'dbo.oa_field_vehicle_use')
          AND name = N'IX_ofvu_car_return'
    )
        CREATE NONCLUSTERED INDEX IX_ofvu_car_return
            ON dbo.oa_field_vehicle_use (car_id, status, return_time DESC, id DESC)
            INCLUDE (end_mileage);

    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE object_id = OBJECT_ID(N'dbo.oa_field_vehicle_fuel')
          AND name = N'IX_ofvf_main_date'
    )
        CREATE NONCLUSTERED INDEX IX_ofvf_main_date
            ON dbo.oa_field_vehicle_fuel (m_id, fuel_date, id)
            INCLUDE (amount, fuel_quantity);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION;
    THROW;
END CATCH;

-- 以下由 ServiceImpl 保证：
-- 0. 加油新增、修改、删除与按 m_id 重算三项主表汇总在同一主记录锁及事务内完成。
-- 1. 草稿不占车、不可登记费用；正式提交完整校验且事务内更新 IsUse=1。
-- 2. 还车在主记录锁内按 m_id 聚合全部有效加油行：SUM(fuel_quantity)、SUM(amount)、COUNT(id)。
--    空日期行照常计入，空集合写 0.000/0.00/0；三项写主表后同一事务结束记录并写 IsUse=0。
--    结束后禁止所有业务修改，三项汇总快照不能手工改写或实时重算覆盖。
-- 3. fuel_date 可空；有值不得早于 use_date，还车不得早于任一非空 fuel_date。
-- 4. 正式司机字符串必填；同行人可空；两者原样保存前端字符串。
-- 5. 费用只能挂在使用中主记录上，m_id 不允许换绑；升数和金额正数校验。
-- 6. 当前认证人产生通用审计，客户端不能指定审计；只允许负责人写入。
