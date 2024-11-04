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
