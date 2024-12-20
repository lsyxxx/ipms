-- 处理物性报告数据
update [dbo].[tmp_raw_data] set testName = '饱和度（%）水' where testName = '水';

update [dbo].[tmp_raw_data] set testName = '渗透率（mD）垂直' where testName = '垂直';

update [dbo].[tmp_raw_data] set testName = '岩心描述' where testName = '岩性';

delete  from [dbo].[tmp_raw_data] where testName = '序号';

update [dbo].[tmp_raw_data] set testName = '渗透率（mD）水平' where testName = '渗透率（mD）';

-- 删除空数据
delete  from [dbo].[tmp_raw_data] where (testName is null or testName = '') and (testValue is null or testValue = '');

update [dbo].[tmp_raw_data]  set testName = '饱和度（%）油'  where testName = '油';
 update [dbo].[tmp_raw_data]  set testName = '饱和度（%）油'  where testName = '含油饱和度%';
 update [dbo].[tmp_raw_data]  set testName = '饱和度（%）水'  where testName = '含水饱和度%';
update [dbo].[tmp_raw_data]  set testName = '井号'  where testName = '井名';
update [dbo].[tmp_raw_data]  set testName = '井深（m）'  where testName = '井段（m）';
update [dbo].[tmp_raw_data]  set testName = '井深（m）'  where testName = '深度m';
update [dbo].[tmp_raw_data]  set testName = '克氏渗透率（mD）'  where testName = '克氏';
update [dbo].[tmp_raw_data]  set testName = '孔隙度（%）'  where testName = '孔隙（%）';
update [dbo].[tmp_raw_data]  set testName = '孔隙度（%）'  where testName = '孔隙度%';
update [dbo].[tmp_raw_data]  set testName = '氯盐含量(mg/kg)'  where testName = '氯离子含量（mg/kg）';
update [dbo].[tmp_raw_data]  set testName = '氯盐含量(mg/kg)'  where testName = '氯岩含量(mg/kg)';
update [dbo].[tmp_raw_data]  set testName = '岩性'  where testName = '岩性描述';
update [dbo].[tmp_raw_data]  set testName = '岩石密度（g/cm3）'  where testName = '岩石密度g/cm3';
update [dbo].[tmp_raw_data]  set testName = '脉冲法渗透率（mD）'  where testName = '脉冲渗透（mD）';
update [dbo].[tmp_raw_data]  set testName = '脉冲法渗透率（mD）'  where testName = '脉冲渗透率（mD）';
update [dbo].[tmp_raw_data]  set testName = '样品编号'  where testName = '送样编号';
update [dbo].[tmp_raw_data]  set testName = '碳酸盐含量（%）'  where testName = '碳酸盐含量%';
update [dbo].[tmp_raw_data]  set testName = '渗透率（mD）垂直'  where testName = '渗透率10-3μm2';
-- nd需转换后改名
-- update [dbo].[tmp_raw_data]  set testName = '渗透率（mD）垂直'  where testName = '岩心垂直渗透率nd';
-- update [dbo].[tmp_raw_data]  set testName = '脉冲法渗透率（mD）'  where testName = '脉冲法超低渗透率nd';
update [dbo].[tmp_raw_data] set testValue = '' where testValue = '/';
update [dbo].[tmp_raw_data] set testValue = '' where testValue = '-';


select reportName, null as "检测编号（置空）", sampleBatch, null as "样品编号（置空）",
       wellNo, sid,
       CAST(CAST(mdTop AS FLOAT) AS DECIMAL(18, 3)) AS mdTop,
       CAST(CAST(mdbase AS FLOAT) AS DECIMAL(18, 3)) AS mdbase,
       layer, rockName,
       CAST(CAST(porosity AS FLOAT) AS DECIMAL(18, 4)) AS porosity,
       CAST(CAST(horizonPermeability AS FLOAT) AS DECIMAL(18, 4)) AS horizonPermeability,
       CAST(CAST(verticalPermeability AS FLOAT) AS DECIMAL(18, 4)) AS verticalPermeability,
       CAST(CAST(rockDensity AS FLOAT) AS DECIMAL(18, 4)) AS rockDensity,
       company, testDateStart, testDateEnd, remark
from tmp_rock_analysis
order by tid desc, testDateStart;

insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'JC2021125 - 宁古7井（第2、3筒） - 物性报告.xls', '井号', '宁古7');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'C2021125 - 宁古7井 第1筒 - 物性报告.xls', '井号', '宁古7');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'JC2021120 - 庆辽乐86H601导井 - 物性报告.xls', '井号', '庆辽乐86H601导');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'JC2021079 - 乐52-4井 - 物性报告.xls', '井号', '乐52-4');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'JC2021067B - 宁古11井 - 物性报告 .xls', '井号', '宁古11');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y027 - 辽河油田 - 宁618井 -  常规物性报告.xls', '井号', '宁618');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y025 - 辽河油田 - 宜56-1井 - 常规物性报告.xls', '井号', '宜56-1');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y024 - 辽河油田 - 宜10-26-16井 - 常规物性报告.xls', '井号', '宜10-26-16');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y022 - 辽河油田 - 宁621井 - 常规物性报告.xls', '井号', '宁621');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y021 - 辽河油田 - 宁620井 - 常规物性报告.xls', '井号', '宁620');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y017-宜庆10井 - 常规物性报告.xls', '井号', '宜庆10');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y015-宜41井-储量、页岩研究测试- 常规物性报告.xls', '井号', '宜41');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y011 - 辽河油田 - 宁624井 -常规物性报告.xls', '井号', '宁624');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2023001Y003 - 宜庆19井 - 煤岩常规物性报告-20个.xls', '井号', '宜庆19');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), '1-6、49-乐61-H712导井 -  常规物性报告.xls', '井号', '乐61-H712导');
-- insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), '6-7- 常规物性报告-7个.xls', '井号', '宁古3-4');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2022019Y008 - 宜120井 - 常规物性报告.xls', '井号', '宜120');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2022019Y007 - 辽河油田 - 宜10-9-53井 -本溪组 - 常规物性报告-23个.xls', '井号', '宜10-9-53');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2022019Y007 - 宜庆13井 - 常规物性报告-10个.xls', '井号', '宜庆13');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2022019Y007 - 辽河油田 - 宜116井 - 山2 - 常规物性报告-56个.xls', '井号', '宜116');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), 'AJC2022019Y006A - 辽河油田 - 宜157-H1（第二批） - 常规物性报告.xls', '井号', '宜157-H1');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), '15、16、18-21、27-常规物性报告.xls', '井号', '正161-H717导');
insert into tmp_raw_data (id, reportName, testName, testValue) values (NEWID(), '15~20、26-常规物性报告.xls', '井号', '宁51-H702导');


select reportName, tid, null as "检测编号（置空）", sampleBatch, null as "样品编号（置空）",
       wellNo as "井号", sid as "原样号",
       CAST(CAST(mdTop AS FLOAT) AS DECIMAL(18, 3)) AS '顶界深度',
        CAST(CAST(mdbase AS FLOAT) AS DECIMAL(18, 3)) AS '底界深度',
        layer as '层位', rockName as '岩性',
        CASE
            WHEN moistureAirDry IS NULL or moistureAirDry = '' THEN NULL
            ELSE CAST(CAST(moistureAirDry AS FLOAT) AS DECIMAL(18, 4))
            END AS "水分-空气干燥基",
       CASE
           WHEN ashAirDry IS NULL or ashAirDry = '' THEN NULL
           ELSE CAST(CAST(ashAirDry AS FLOAT) AS DECIMAL(18, 4))
           END AS "灰分-空气干燥基",
       CASE
           WHEN ashDry IS NULL or ashDry = '' THEN NULL
           ELSE CAST(CAST(ashDry AS FLOAT) AS DECIMAL(18, 4))
           END AS "灰分-干燥基",
       CASE
           WHEN volatileAirDry IS NULL or volatileAirDry = '' THEN NULL
           ELSE CAST(CAST(volatileAirDry AS FLOAT) AS DECIMAL(18, 4))
           END AS "挥发分-空气干燥基",
       CASE
           WHEN volatileDry IS NULL or volatileDry = '' THEN NULL
           ELSE CAST(CAST(volatileDry AS FLOAT) AS DECIMAL(18, 4))
           END AS "挥发分-干燥基",
       CASE
           WHEN volatileDryAshFree IS NULL or volatileDryAshFree = '' THEN NULL
           ELSE CAST(CAST(volatileDryAshFree AS FLOAT) AS DECIMAL(18, 4))
           END AS "挥发分-干燥无灰基",
       fixedCarbonAirDry,
-- CASE
--     WHEN fixedCarbonAirDry IS NULL or fixedCarbonAirDry = '' THEN NULL
--     ELSE CAST(CAST(fixedCarbonAirDry AS FLOAT) AS DECIMAL(18, 4))
-- END AS "固定碳-空气干燥基",

       '西安阿伯塔资环分析测试技术有限公司' as "检测单位", testDateStart as '检测开始日期', testDateEnd as '检测结束日期', remark as '备注'
from tmp_coal_industry_analysis
order by tid asc, testDateStart;

-- 主量元素
SELECT
    m.reportName,
    m.tid,
    NULL AS "检测编号（置空）",
    CONCAT(b.testDateStart, '-', b.testDateEnd) as sampleBatch,
    NULL AS "样品编号（置空）",
    wellNo AS "井号",
    sid AS "原样号",
    CAST ( CAST ( mdTop AS FLOAT ) AS DECIMAL ( 18, 3 ) ) AS '顶界深度',
    CAST ( CAST ( mdbase AS FLOAT ) AS DECIMAL ( 18, 3 ) ) AS '底界深度',
    layer AS '层位',
    rockName AS '岩性',
    CASE  WHEN SIO2 IS NULL  OR SIO2 = '' THEN NULL ELSE CAST ( CAST ( SIO2 AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "SIO2",
    CASE  WHEN AL2O3 IS NULL OR AL2O3 = '' THEN NULL ELSE CAST ( CAST ( AL2O3 AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "AL2O3",
    CASE  WHEN TFe2O3 IS NULL OR TFe2O3 = '' THEN NULL ELSE CAST ( CAST ( TFe2O3 AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "FE2O3",
    CASE  WHEN MGO IS NULL OR MGO = '' THEN NULL ELSE CAST ( CAST ( MGO AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "MGO",
    CASE  WHEN CAO IS NULL OR CAO = '' THEN NULL ELSE CAST ( CAST ( CAO AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "CAO",
    CASE  WHEN NA2O IS NULL OR NA2O = '' THEN NULL ELSE CAST ( CAST ( NA2O AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "NA2O",
    CASE  WHEN MNO IS NULL OR MNO = '' THEN NULL ELSE CAST ( CAST ( MNO AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "MNO",
    CASE  WHEN TIO2 IS NULL OR TIO2 = '' THEN NULL ELSE CAST ( CAST ( TIO2 AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "TIO2",
    CASE  WHEN P2O5 IS NULL OR P2O5 = '' THEN NULL ELSE CAST ( CAST ( P2O5 AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "P2O5",
    CASE  WHEN LOI IS NULL OR LOI = '' THEN NULL ELSE CAST ( CAST ( LOI AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "LOI",
    CASE  WHEN TOTAL IS NULL OR TOTAL = '' THEN NULL ELSE CAST ( CAST ( TOTAL AS FLOAT ) AS DECIMAL ( 18, 4 ) ) END AS "TOTAL",
    b.testDateStart, b.testDateEnd
FROM
    [dbo].[tmp_ele_major] m
LEFT JOIN tmp_base b on m.reportName = b.reportName
ORDER BY
    m.reportName,
    m.tid