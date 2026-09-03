-- 野外考勤：fw_item 补齐提交快照列；fw_atd_setting 多版本合并为一行当前预设
-- 方言：SQL Server 2014
-- 影响表：dbo.fw_item, dbo.fw_atd_setting
-- 执行前请备份上述两表
-- 可重复执行：加列有 COL_LENGTH 判断；回填只填快照仍为空的行；不覆盖 a_name/a_prod/a_meal/a_sum
-- 顺序：加列 -> 按原 a_id 回填快照 -> 每 vid 留 max(version_) -> item.a_id 改指保留行 -> 旧版本禁用/删除

-- ========== 1. fw_item 加列 ==========
IF COL_LENGTH('dbo.fw_item', 'short_name') IS NULL
    ALTER TABLE dbo.fw_item ADD short_name VARCHAR(16) NULL;

IF COL_LENGTH('dbo.fw_item', 'style_') IS NULL
    ALTER TABLE dbo.fw_item ADD style_ VARCHAR(1000) NULL;

IF COL_LENGTH('dbo.fw_item', 'bg_color') IS NULL
    ALTER TABLE dbo.fw_item ADD bg_color NVARCHAR(255) NULL;

IF COL_LENGTH('dbo.fw_item', 'is_work') IS NULL
    ALTER TABLE dbo.fw_item ADD is_work BIT NULL;

IF COL_LENGTH('dbo.fw_item', 'group_') IS NULL
    ALTER TABLE dbo.fw_item ADD group_ VARCHAR(128) NULL;
GO

-- ========== 2. 按当时配置行回填快照（不改金额、名称；已有快照值不覆盖）==========
UPDATE i
SET
    i.short_name = CASE WHEN i.short_name IS NULL THEN s.short_name ELSE i.short_name END,
    i.style_ = CASE WHEN i.style_ IS NULL THEN s.style_ ELSE i.style_ END,
    i.bg_color = CASE WHEN i.bg_color IS NULL THEN s.bg_color ELSE i.bg_color END,
    i.is_work = CASE WHEN i.is_work IS NULL THEN s.is_work ELSE i.is_work END,
    i.group_ = CASE WHEN i.group_ IS NULL THEN s.group_ ELSE i.group_ END
FROM dbo.fw_item i
INNER JOIN dbo.fw_atd_setting s ON i.a_id = s.id
WHERE i.short_name IS NULL
   OR i.style_ IS NULL
   OR i.bg_color IS NULL
   OR i.is_work IS NULL
   OR i.group_ IS NULL;

UPDATE dbo.fw_item
SET is_work = 0
WHERE is_work IS NULL;
GO

-- ========== 3. 每 vid 保留 version_ 最大的一行（并列取 create_date 较新）==========
IF OBJECT_ID('tempdb..#fw_keep') IS NOT NULL
    DROP TABLE #fw_keep;

SELECT vid, id AS keep_id
INTO #fw_keep
FROM (
    SELECT
        s.vid,
        s.id,
        ROW_NUMBER() OVER (
            PARTITION BY s.vid
            ORDER BY s.version_ DESC, s.create_date DESC
        ) AS rn
    FROM dbo.fw_atd_setting s
    WHERE s.vid IS NOT NULL
      AND LTRIM(RTRIM(s.vid)) <> ''
) t
WHERE t.rn = 1;

-- ========== 4. 历史明细 a_id 改指保留行（快照已写好，展示不变）==========
UPDATE i
SET i.a_id = k.keep_id
FROM dbo.fw_item i
INNER JOIN dbo.fw_atd_setting olds ON i.a_id = olds.id
INNER JOIN #fw_keep k ON olds.vid = k.vid
WHERE i.a_id <> k.keep_id;

-- ========== 5. 旧版本：禁用；无明细引用则删除 ==========
UPDATE s
SET s.enabled_ = 0
FROM dbo.fw_atd_setting s
INNER JOIN #fw_keep k ON s.vid = k.vid
WHERE s.id <> k.keep_id
  AND s.enabled_ = 1;

DELETE s
FROM dbo.fw_atd_setting s
INNER JOIN #fw_keep k ON s.vid = k.vid
WHERE s.id <> k.keep_id
  AND NOT EXISTS (
        SELECT 1
        FROM dbo.fw_item i
        WHERE i.a_id = s.id
    );

DROP TABLE #fw_keep;
GO
