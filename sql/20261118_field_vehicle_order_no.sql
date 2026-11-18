-- 野外用车单号升级；SQL Server 2014 兼容。停服务后在 ZJData 手工执行，完成后再部署应用。
-- 保留原 ID；历史记录按创建年份、创建时间、ID 补号；不回收、不重置已使用流水。
SET NOCOUNT ON;
SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRANSACTION;
    IF OBJECT_ID(N'dbo.oa_field_vehicle_use', N'U') IS NULL
        THROW 50000, N'缺少野外用车主表。', 1;
    IF OBJECT_ID(N'dbo.T_PK_CONFIG', N'U') IS NULL OR OBJECT_ID(N'dbo.T_PK', N'U') IS NULL
        THROW 50000, N'缺少既有编号配置表或流水表。', 1;
    IF COL_LENGTH(N'dbo.oa_field_vehicle_use', N'order_no') IS NULL
        ALTER TABLE dbo.oa_field_vehicle_use ADD order_no VARCHAR(32) NULL;

    IF (SELECT COUNT(*) FROM dbo.T_PK_CONFIG WHERE Tablename = 'YWYC_ORDERNO') > 1
        THROW 50000, N'野外用车单号规则重复。', 1;
    IF NOT EXISTS (SELECT 1 FROM dbo.T_PK_CONFIG WHERE Tablename = 'YWYC_ORDERNO')
        INSERT dbo.T_PK_CONFIG (Id, Fid, Tablename, Prefix, Isprefix, Isdate, Isserverid, Isguid, Flen, isMonthDay)
        -- Fid 是内部规则标识，既有字段为 NVARCHAR(3)；单号前缀由 Prefix 决定。
        VALUES (CONVERT(VARCHAR(50), NEWID()), 'YWC', 'YWYC_ORDERNO', 'YWYC', '1', '1', '0', '0', 4, '0');
    IF EXISTS (SELECT 1 FROM dbo.T_PK_CONFIG WHERE Tablename = 'YWYC_ORDERNO' AND
        (ISNULL(Prefix, '') <> 'YWYC' OR ISNULL(Isprefix, '') <> '1' OR ISNULL(Isdate, '') <> '1'
         OR ISNULL(Isserverid, '') <> '0' OR ISNULL(Isguid, '') <> '0' OR ISNULL(Flen, 0) <> 4 OR ISNULL(isMonthDay, '') <> '0'))
        THROW 50000, N'已有野外用车规则与预期不同，请先核实，脚本不会覆盖规则。', 1;

    -- 新增列后的使用放进独立编译批次。
    EXEC sys.sp_executesql N'
    IF EXISTS (SELECT 1 FROM dbo.oa_field_vehicle_use WHERE NULLIF(LTRIM(RTRIM(order_no)), '''') IS NULL AND create_date IS NULL)
        THROW 50000, N''历史记录缺少创建时间，无法确定补号年份。'', 1;
    IF EXISTS (SELECT 1 FROM dbo.oa_field_vehicle_use WHERE NULLIF(LTRIM(RTRIM(order_no)), '''') IS NOT NULL AND
        (LEN(order_no) < 12 OR LEFT(order_no, 4) <> ''YWYC'' OR SUBSTRING(order_no, 5, 28) COLLATE Latin1_General_BIN2 LIKE ''%[^0-9]%''
         OR TRY_CONVERT(INT, SUBSTRING(order_no, 5, 4)) NOT BETWEEN 1 AND 9999
         OR ISNULL(TRY_CONVERT(BIGINT, SUBSTRING(order_no, 9, 24)), 0) < 1))
        THROW 50000, N''已有单号格式异常，请先核实。'', 1;
    IF EXISTS (SELECT order_no FROM dbo.oa_field_vehicle_use WHERE NULLIF(LTRIM(RTRIM(order_no)), '''') IS NOT NULL GROUP BY order_no HAVING COUNT(*) > 1)
        THROW 50000, N''已有单号重复，请先核实。'', 1;
    IF EXISTS (SELECT Curdate FROM dbo.T_PK WHERE Tablename = ''YWYC_ORDERNO'' AND Prefix = ''YWYC'' GROUP BY Curdate HAVING COUNT(*) > 1)
        THROW 50000, N''野外用车存在重复年度流水，请先核实。'', 1;
    IF EXISTS (SELECT 1 FROM dbo.T_PK WHERE Tablename = ''YWYC_ORDERNO'' AND Prefix = ''YWYC'' AND
        (LEN(ISNULL(Curdate, '''')) <> 4 OR Curdate COLLATE Latin1_General_BIN2 LIKE ''%[^0-9]%'' OR
         TRY_CONVERT(INT, Curdate) NOT BETWEEN 1 AND 9999 OR SN IS NULL OR SN < 0 OR SN <> FLOOR(SN) OR TRY_CONVERT(BIGINT, SN) IS NULL))
        THROW 50000, N''已有年度流水无效，请先核实。'', 1;

    SELECT NumberYear, MAX(CurrentValue) AS CurrentValue INTO #YearCounters FROM (
        SELECT SUBSTRING(order_no, 5, 4) AS NumberYear, CONVERT(BIGINT, SUBSTRING(order_no, 9, 24)) AS CurrentValue
        FROM dbo.oa_field_vehicle_use WHERE NULLIF(LTRIM(RTRIM(order_no)), '''') IS NOT NULL
        UNION ALL
        SELECT Curdate, CONVERT(BIGINT, SN) FROM dbo.T_PK WHERE Tablename = ''YWYC_ORDERNO'' AND Prefix = ''YWYC''
    ) AS Used GROUP BY NumberYear;

    ;WITH Missing AS (
        SELECT id, RIGHT(''0000'' + CONVERT(VARCHAR(4), YEAR(create_date)), 4) AS NumberYear,
            ROW_NUMBER() OVER (PARTITION BY YEAR(create_date) ORDER BY create_date, id) AS RowNumber
        FROM dbo.oa_field_vehicle_use WHERE NULLIF(LTRIM(RTRIM(order_no)), '''') IS NULL
    ), Assigned AS (
        SELECT m.id, m.NumberYear, CONVERT(VARCHAR(24), ISNULL(c.CurrentValue, 0) + m.RowNumber) AS Serial
        FROM Missing m LEFT JOIN #YearCounters c ON c.NumberYear = m.NumberYear
    )
    UPDATE u SET order_no = ''YWYC'' + a.NumberYear + CASE WHEN LEN(a.Serial) < 4 THEN REPLICATE(''0'', 4 - LEN(a.Serial)) ELSE '''' END + a.Serial
    FROM dbo.oa_field_vehicle_use u INNER JOIN Assigned a ON a.id = u.id;

    SELECT SUBSTRING(order_no, 5, 4) AS NumberYear, MAX(CONVERT(BIGINT, SUBSTRING(order_no, 9, 24))) AS CurrentValue
    INTO #FinalCounters FROM dbo.oa_field_vehicle_use GROUP BY SUBSTRING(order_no, 5, 4);
    UPDATE p SET SN = CASE WHEN p.SN < f.CurrentValue THEN f.CurrentValue ELSE p.SN END,
        MYROWID = REPLACE(CONVERT(VARCHAR(36), NEWID()), ''-'', ''''), OPERATEDATE = GETDATE(), UPFLAG = ''1''
    FROM dbo.T_PK p INNER JOIN #FinalCounters f ON f.NumberYear = p.Curdate
    WHERE p.Tablename = ''YWYC_ORDERNO'' AND p.Prefix = ''YWYC'';
    INSERT dbo.T_PK (FID, TABLENAME, PREFIX, CURDATE, SERVERID, SN, MYROWID, OPERATEDATE, UPFLAG)
    SELECT REPLACE(CONVERT(VARCHAR(36), NEWID()), ''-'', ''''), ''YWYC_ORDERNO'', ''YWYC'', f.NumberYear, '''', f.CurrentValue,
        REPLACE(CONVERT(VARCHAR(36), NEWID()), ''-'', ''''), GETDATE(), ''0''
    FROM #FinalCounters f WHERE NOT EXISTS (SELECT 1 FROM dbo.T_PK p WHERE p.Tablename = ''YWYC_ORDERNO'' AND p.Prefix = ''YWYC'' AND p.Curdate = f.NumberYear);

    IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N''dbo.oa_field_vehicle_use'') AND name = ''order_no'' AND is_nullable = 1)
        ALTER TABLE dbo.oa_field_vehicle_use ALTER COLUMN order_no VARCHAR(32) NOT NULL;
    IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID(N''dbo.oa_field_vehicle_use'') AND name = ''UX_field_vehicle_order_no'')
        CREATE UNIQUE INDEX UX_field_vehicle_order_no ON dbo.oa_field_vehicle_use(order_no);
    ';
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
