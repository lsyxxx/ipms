/*
用途：将“需要新增的项目.xlsx”中的 26 条项目/子参数写入 ZJDATA。
目标表：dbo.T_checkModule、dbo.T_checkItem、dbo.T_checkmodule_checkitem。
兼容：SQL Server 2014。

注意：
1. 本脚本不会由生成者执行，请由数据库管理员审核后手工执行。
2. 项目与子参数必须通过 dbo.T_checkmodule_checkitem 建立关系，否则系统中无法得到项目-子参数映射。
3. 执行前请按实际操作人修改 @Operator、@OperatorName；当前默认使用“超级管理员”。
4. Excel 中“是否认证=否”映射为认证字段值 '2'，“是否启用=是”映射为 IsActive='1'。
5. Excel 中“分类”仅作核对，不对应上述三张表的字段；实际分类使用 CheckunitId。
*/

USE [ZJDATA];
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @Operator varchar(50) = '00000000-0000-0000-0000-000000000000';
DECLARE @OperatorName nvarchar(50) = N'超级管理员';
DECLARE @Now datetime = GETDATE();
DECLARE @ModuleBase int;
DECLARE @ItemBase int;
DECLARE @DuplicateMessage nvarchar(2048);

DECLARE @Source TABLE
(
    RowNo int NOT NULL PRIMARY KEY,
    Category nvarchar(50) NOT NULL,
    CheckunitId varchar(50) NOT NULL,
    ModuleName nvarchar(200) NOT NULL,
    ItemName nvarchar(200) NOT NULL,
    BreName nvarchar(200) NOT NULL,
    IsCertified varchar(2) NOT NULL,
    IsActive varchar(2) NOT NULL
);

INSERT INTO @Source
(
    RowNo,
    Category,
    CheckunitId,
    ModuleName,
    ItemName,
    BreName,
    IsCertified,
    IsActive
)
VALUES
    (1,  N'岩石矿物',   '001', N'光片拍照',                           N'光片拍照',                           N'光片拍照',                           '2', '1'),
    (2,  N'岩石矿物',   '001', N'普通薄片拍照',                       N'普通薄片拍照',                       N'普通薄片拍照',                       '2', '1'),
    (3,  N'岩石矿物',   '001', N'铸体薄片拍照',                       N'铸体薄片拍照',                       N'铸体薄片拍照',                       '2', '1'),
    (4,  N'岩石矿物',   '001', N'生物化石鉴定',                       N'生物化石鉴定',                       N'生物化石鉴定',                       '2', '1'),
    (5,  N'岩石矿物',   '001', N'岩心伤害实验',                       N'岩心伤害实验',                       N'岩心伤害实验',                       '2', '1'),
    (6,  N'岩石矿物',   '001', N'滑脱系数',                           N'滑脱系数',                           N'滑脱系数',                           '2', '1'),
    (7,  N'岩石矿物',   '001', N'变形参数（割线模量、弹性模量、泊松比）', N'变形参数（割线模量、弹性模量、泊松比）', N'变形参数',                           '2', '1'),
    (8,  N'岩石矿物',   '001', N'气体渗透率及其各向异性',             N'气体渗透率及其各向异性',             N'气体渗透率及其各向异性',             '2', '1'),
    (9,  N'岩石矿物',   '001', N'渗析实验',                           N'渗析实验',                           N'渗析实验',                           '2', '1'),
    (10, N'岩石矿物',   '001', N'突破压力',                           N'突破压力',                           N'突破压力',                           '2', '1'),
    (11, N'煤炭类',     '010', N'煤灰黏度',                           N'煤灰黏度',                           N'煤灰黏度',                           '2', '1'),
    (12, N'煤炭类',     '010', N'结渣性',                             N'结渣性',                             N'结渣性',                             '2', '1'),
    (13, N'煤炭类',     '010', N'碳酸盐二氧化碳',                     N'碳酸盐二氧化碳',                     N'碳酸盐二氧化碳',                     '2', '1'),
    (14, N'煤炭类',     '010', N'对二氧化碳化学反应性',               N'对二氧化碳化学反应性',               N'对二氧化碳化学反应性',               '2', '1'),
    (15, N'煤炭类',     '010', N'自燃倾向性',                         N'自燃倾向性',                         N'自燃倾向性',                         '2', '1'),
    (16, N'煤炭类',     '010', N'抗碎强度',                           N'抗碎强度',                           N'抗碎强度',                           '2', '1'),
    (17, N'岩石矿物',   '001', N'多温阶热解',                         N'多温阶热解',                         N'多温阶热解',                         '2', '1'),
    (18, N'同位素',     '006', N'铅同位素',                           N'铅同位素',                           N'铅同位素',                           '2', '1'),
    (19, N'同位素',     '006', N'钕同位素',                           N'钕同位素',                           N'钕同位素',                           '2', '1'),
    (20, N'石油天然气', '004', N'原油高分辨质谱分析',                 N'原油高分辨质谱分析',                 N'原油高分辨质谱分析',                 '2', '1'),
    (21, N'石油天然气', '004', N'中性含氮化合物分离',                 N'中性含氮化合物分离',                 N'中性含氮化合物分离',                 '2', '1'),
    (22, N'石油天然气', '004', N'中性含氮化合物气相色谱-质谱分析',     N'中性含氮化合物气相色谱-质谱分析',     N'中性含氮化合物气相色谱-质谱分析',     '2', '1'),
    (23, N'岩石矿物',   '001', N'磷灰石裂变径迹',                     N'磷灰石裂变径迹',                     N'磷灰石裂变径迹',                     '2', '1'),
    (24, N'岩石矿物',   '001', N'反射光拍照',                         N'反射光拍照',                         N'反射光拍照',                         '2', '1'),
    (25, N'岩石矿物',   '001', N'K-Ar法测年',                         N'K-Ar法测年',                         N'K-Ar法测年',                         '2', '1'),
    (26, N'岩石矿物',   '001', N'Ar-Ar法测年',                        N'Ar-Ar法测年',                        N'Ar-Ar法测年',                        '2', '1');

BEGIN TRY
    BEGIN TRANSACTION;

    IF EXISTS
    (
        SELECT 1
        FROM @Source s
        WHERE DATALENGTH(CONVERT(varchar(max), s.ModuleName)) > 50
           OR DATALENGTH(CONVERT(varchar(max), s.ItemName)) > 50
           OR DATALENGTH(CONVERT(varchar(max), s.BreName)) > 50
    )
    BEGIN
        THROW 50001, N'存在超过目标 varchar(50) 字段长度的数据，请先检查名称或常用名称。', 1;
    END;

    IF EXISTS
    (
        SELECT 1
        FROM @Source s
        INNER JOIN dbo.T_checkModule m
            ON m.Fname = CONVERT(varchar(50), s.ModuleName)
           AND ISNULL(m.CheckunitId, '') = s.CheckunitId
           AND ISNULL(m.delFlag, '') <> '1'
    )
    BEGIN
        SELECT
            N'项目已存在' AS ErrorType,
            s.RowNo,
            s.ModuleName,
            s.CheckunitId,
            m.Id,
            m.Fid
        FROM @Source s
        INNER JOIN dbo.T_checkModule m
            ON m.Fname = CONVERT(varchar(50), s.ModuleName)
           AND ISNULL(m.CheckunitId, '') = s.CheckunitId
           AND ISNULL(m.delFlag, '') <> '1';

        SELECT @DuplicateMessage = LEFT
        (
            N'项目重复：' +
            (
                SELECT
                    N'INSERT第' + CONVERT(nvarchar(10), s.RowNo) + N'条' +
                    N'（Excel第' + CONVERT(nvarchar(10), s.RowNo + 1) + N'行）' +
                    N'，项目名称=' + s.ModuleName +
                    N'，CheckunitId=' + CONVERT(nvarchar(50), s.CheckunitId) +
                    N'，数据库Fid=' + CONVERT(nvarchar(50), m.Fid) + N'；'
                FROM @Source s
                INNER JOIN dbo.T_checkModule m
                    ON m.Fname = CONVERT(varchar(50), s.ModuleName)
                   AND ISNULL(m.CheckunitId, '') = s.CheckunitId
                   AND ISNULL(m.delFlag, '') <> '1'
                ORDER BY s.RowNo
                FOR XML PATH(''), TYPE
            ).value('.', 'nvarchar(max)'),
            2048
        );

        THROW 50002, @DuplicateMessage, 1;
    END;

    IF EXISTS
    (
        SELECT 1
        FROM @Source s
        INNER JOIN dbo.T_checkItem i
            ON i.Fname = s.ItemName
           AND ISNULL(i.CheckunitId, '') = s.CheckunitId
           AND ISNULL(i.delFlag, '') <> '1'
    )
    BEGIN
        SELECT
            N'子参数已存在' AS ErrorType,
            s.RowNo,
            s.ItemName,
            s.CheckunitId,
            i.Id,
            i.Fid
        FROM @Source s
        INNER JOIN dbo.T_checkItem i
            ON i.Fname = s.ItemName
           AND ISNULL(i.CheckunitId, '') = s.CheckunitId
           AND ISNULL(i.delFlag, '') <> '1';

        SELECT @DuplicateMessage = LEFT
        (
            N'子参数重复：' +
            (
                SELECT
                    N'INSERT第' + CONVERT(nvarchar(10), s.RowNo) + N'条' +
                    N'（Excel第' + CONVERT(nvarchar(10), s.RowNo + 1) + N'行）' +
                    N'，子参数名称=' + s.ItemName +
                    N'，CheckunitId=' + CONVERT(nvarchar(50), s.CheckunitId) +
                    N'，数据库Fid=' + CONVERT(nvarchar(50), i.Fid) + N'；'
                FROM @Source s
                INNER JOIN dbo.T_checkItem i
                    ON i.Fname = s.ItemName
                   AND ISNULL(i.CheckunitId, '') = s.CheckunitId
                   AND ISNULL(i.delFlag, '') <> '1'
                ORDER BY s.RowNo
                FOR XML PATH(''), TYPE
            ).value('.', 'nvarchar(max)'),
            2048
        );

        THROW 50003, @DuplicateMessage, 1;
    END;

    /* 锁定编号范围，避免并发执行时生成重复 Fid。 */
    SELECT @ModuleBase = ISNULL(MAX(TRY_CONVERT(int, SUBSTRING(Fid, 2, 49))), 0)
    FROM dbo.T_checkModule WITH (UPDLOCK, HOLDLOCK)
    WHERE Fid LIKE 'X%'
      AND TRY_CONVERT(int, SUBSTRING(Fid, 2, 49)) IS NOT NULL;

    SELECT @ItemBase = ISNULL(MAX(TRY_CONVERT(int, SUBSTRING(Fid, 2, 49))), 0)
    FROM dbo.T_checkItem WITH (UPDLOCK, HOLDLOCK)
    WHERE Fid LIKE 'C%'
      AND TRY_CONVERT(int, SUBSTRING(Fid, 2, 49)) IS NOT NULL;

    IF @ModuleBase + (SELECT COUNT(1) FROM @Source) > 99999
       OR @ItemBase + (SELECT COUNT(1) FROM @Source) > 99999
    BEGIN
        THROW 50004, N'自动编号超过 5 位数字上限，已终止导入。', 1;
    END;

    DECLARE @Prepared TABLE
    (
        RowNo int NOT NULL PRIMARY KEY,
        ModuleFid varchar(50) NOT NULL,
        ItemFid varchar(50) NOT NULL,
        CheckunitId varchar(50) NOT NULL,
        ModuleName nvarchar(200) NOT NULL,
        ItemName nvarchar(200) NOT NULL,
        BreName nvarchar(200) NOT NULL,
        IsCertified varchar(2) NOT NULL,
        IsActive varchar(2) NOT NULL
    );

    INSERT INTO @Prepared
    (
        RowNo,
        ModuleFid,
        ItemFid,
        CheckunitId,
        ModuleName,
        ItemName,
        BreName,
        IsCertified,
        IsActive
    )
    SELECT
        s.RowNo,
        'X' + RIGHT('00000' + CONVERT(varchar(10), @ModuleBase + s.RowNo), 5),
        'C' + RIGHT('00000' + CONVERT(varchar(10), @ItemBase + s.RowNo), 5),
        s.CheckunitId,
        s.ModuleName,
        s.ItemName,
        s.BreName,
        s.IsCertified,
        s.IsActive
    FROM @Source s;

    INSERT INTO dbo.T_checkModule
    (
        Id,
        Fid,
        Fname,
        CheckunitId,
        IsActive,
        Operator,
        OperatorName,
        CreateUserId,
        CreateUserName,
        CreateDate,
        Operatedate,
        BreName,
        shifourenzheng
    )
    SELECT
        p.ModuleFid,
        p.ModuleFid,
        CONVERT(varchar(50), p.ModuleName),
        p.CheckunitId,
        p.IsActive,
        @Operator,
        CONVERT(varchar(50), @OperatorName),
        @Operator,
        CONVERT(varchar(50), @OperatorName),
        @Now,
        @Now,
        CONVERT(varchar(50), p.BreName),
        p.IsCertified
    FROM @Prepared p;

    INSERT INTO dbo.T_checkItem
    (
        Id,
        Fid,
        Fname,
        OrgPrice,
        Curprice,
        IsActive,
        Operator,
        OperatorName,
        CreateUserId,
        CreateUserName,
        CreateDate,
        Operatedate,
        CheckunitId,
        BreName,
        SaveFloat,
        showOrder,
        shifourenzhengcanshu
    )
    SELECT
        p.ItemFid,
        p.ItemFid,
        CONVERT(nvarchar(100), p.ItemName),
        0.00,
        0.00,
        p.IsActive,
        @Operator,
        CONVERT(varchar(50), @OperatorName),
        @Operator,
        CONVERT(varchar(50), @OperatorName),
        @Now,
        @Now,
        p.CheckunitId,
        CONVERT(nvarchar(400), p.BreName),
        0,
        0,
        p.IsCertified
    FROM @Prepared p;

    INSERT INTO dbo.T_checkmodule_checkitem
    (
        Id,
        CheckModuleID,
        CheckmoduleName,
        CheckItemid,
        CheckItemName,
        delFlag
    )
    SELECT
        REPLACE(CONVERT(varchar(36), NEWID()), '-', ''),
        p.ModuleFid,
        CONVERT(nvarchar(200), p.ModuleName),
        p.ItemFid,
        CONVERT(varchar(50), p.ItemName),
        NULL
    FROM @Prepared p;

    IF @@ROWCOUNT <> (SELECT COUNT(1) FROM @Source)
    BEGIN
        THROW 50005, N'项目-子参数关联写入数量不正确，已终止导入。', 1;
    END;

    SELECT
        p.RowNo,
        p.ModuleFid,
        p.ModuleName,
        p.ItemFid,
        p.ItemName,
        p.CheckunitId,
        p.BreName,
        p.IsCertified,
        p.IsActive
    FROM @Prepared p
    ORDER BY p.RowNo;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
    BEGIN
        ROLLBACK TRANSACTION;
    END;

    THROW;
END CATCH;
GO
