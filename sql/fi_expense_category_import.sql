/*
用途：导入桌面《费用分类.xls》中的费用分类主数据到 dbo.fi_expense_category。
来源：编码 + 费用分类，共 33 条，按 Excel 行序写入 sort_no。
兼容：SQL Server 2014。

注意：
1. 请先执行 sql/fi_expense_category.sql 建表（含 code_/name_ 唯一约束）。
2. 本脚本按 code_ 幂等：已存在同编号记录则跳过，不更新。
3. 执行前可按实际操作人修改 @Operator、@OperatorName。
*/

SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @Operator VARCHAR(36) = '00000000-0000-0000-0000-000000000000';
DECLARE @OperatorName VARCHAR(64) = '超级管理员';
DECLARE @Now DATETIME = GETDATE();

DECLARE @Source TABLE
(
    sort_no INT NOT NULL PRIMARY KEY,
    code_   VARCHAR(64) NOT NULL,
    name_   VARCHAR(128) NOT NULL
);

INSERT INTO @Source (sort_no, code_, name_) VALUES
(1,  '01', '办公用品费'),
(2,  '02', '差旅费'),
(3,  '03', '业务招待费'),
(4,  '04', '人员工资'),
(5,  '05', '人员社会保险费'),
(6,  '06', '实验室检测耗材费'),
(7,  '07', '市内交通费'),
(8,  '08', '人员公积金'),
(9,  '09', '试验设备设施维护费'),
(10, '10', '安全生产措施费'),
(11, '11', '车辆汽杂费'),
(12, '12', '外送检测费'),
(13, '13', '劳保费'),
(14, '14', '水电物业费'),
(15, '15', '员工福利费'),
(16, '16', '税费'),
(17, '17', '其他'),
(18, '18', '专利资质费'),
(19, '19', '技术服务费'),
(20, '20', '培训费'),
(21, '21', '咨询服务费'),
(22, '22', '委外工程劳务费'),
(23, '23', '维修改造费'),
(24, '24', '安全保险费'),
(25, '25', '房屋租赁费'),
(26, '26', '工会经费'),
(27, '27', '广告宣传费'),
(28, '28', '运费'),
(29, '29', '投标费'),
(30, '30', '设备采购费'),
(31, '31', '市场备用金'),
(32, '32', '市场劳务费'),
(33, '33', '项目评审费');

BEGIN TRAN;

INSERT INTO dbo.fi_expense_category
(
    id,
    code_,
    name_,
    sort_no,
    enabled_,
    remark_,
    create_userid,
    create_username,
    create_date,
    update_userid,
    update_username,
    update_date
)
SELECT
    CONVERT(VARCHAR(36), NEWID()),
    s.code_,
    s.name_,
    s.sort_no,
    1,
    NULL,
    @Operator,
    @OperatorName,
    @Now,
    @Operator,
    @OperatorName,
    @Now
FROM @Source s
WHERE NOT EXISTS (
    SELECT 1
    FROM dbo.fi_expense_category e
    WHERE e.code_ = s.code_
);

DECLARE @Inserted INT = @@ROWCOUNT;

COMMIT TRAN;

SELECT
    @Inserted AS inserted_count,
    (SELECT COUNT(1) FROM dbo.fi_expense_category) AS total_count;

SELECT
    e.code_,
    e.name_,
    e.sort_no,
    e.enabled_
FROM dbo.fi_expense_category e
ORDER BY e.sort_no, e.code_;
GO
