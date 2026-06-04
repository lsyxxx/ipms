# 结算统计接口文档

## Summary

- 模块：`admin/src/main/java/com/abt/market`
- 统计实现：独立 `SettlementStatService`
- 统计主口径：
  - 应结算明细：`T_SampleRegist_CheckModeuleItem`
  - 已结算明细：`stlm_test + stlm_main`
  - 金额口径：`stlm_smry.amt_`
- 有效结算状态：`SAVE`、`INVOICING`、`INVOICE`、`PAYED`
- 不计入：`TEMP`、`INVALID`、`isDel = true`

## 状态定义

### 项目结算状态 `SettlementStatStatus`

- `SETTLED`
  - 项目下源明细全部都能在有效 `TestItem` 中匹配到
- `PARTIALLY_SETTLED`
  - 项目下源明细只匹配到一部分
  - 或者源明细为 0，但存在多结算数据
- `UNSETTLED`
  - 项目下源明细一条都未匹配到，且不存在多结算数据

### 差异明细类型 `SettlementDiffType`

- `MATCHED`
  - 源数据存在，结算数据也存在
- `UNSETTLED`
  - 源数据存在，但未进入结算
- `EXTRA_SETTLED`
  - 结算数据存在，但源数据中不存在

## 匹配规则

按同一个 `Entrust` 下的样品检测项做明细对账：

- 源数据唯一键：
  - `T_SampleRegist_CheckModeuleItem.entrustId + SampleRegistId + CheckModeuleId`
- 已结算唯一键：
  - `stlm_test.entrust_id + sample_no + check_module_id`

满足以下条件时，认为是同一个样品的同一个检测项目：

- `entrustId = entrust_id`
- `SampleRegistId = sample_no`
- `CheckModeuleId = check_module_id`

## 日期范围参数

所有新的统计接口统一支持以下日期参数，统计范围作用于 `T_entrust.CreateDate`，结束日期包含当天。

### 自定义日期范围

- `startDate`
- `endDate`

示例：

```http
GET /stlm/stat/entrust/page?startDate=2025-01-01&endDate=2025-03-31
```

### 快捷范围 `datePreset`

- `THIS_MONTH`
- `THIS_QUARTER`
- `THIS_YEAR`
- `CUSTOM`

示例：

```http
GET /stlm/stat/entrust/page?datePreset=THIS_QUARTER
```

### 兼容参数 `year`

当未传 `startDate/endDate` 时，可传 `year=2025` 表示整年范围。

## 返回结构

### 1. 项目状态 DTO `EntrustSettlementStatDTO`

- `entrustId`：项目编号
- `projectName`：项目名称
- `clientName`：客户名称
- `totalCount`：源明细总数
- `settledCount`：已匹配结算数
- `unsettledCount`：未结算数
- `extraSettledCount`：多结算数
- `settledAmount`：已结算金额合计
- `settlementStatus`：`SETTLED / PARTIALLY_SETTLED / UNSETTLED`

### 2. 项目差异详情 DTO `EntrustSettlementDiffDTO`

继承项目状态 DTO，并增加：

- `items`：差异明细列表

### 3. 项目差异明细 DTO `EntrustSettlementDiffItemDTO`

- `diffType`
- `entrustId`
- `sampleRegistId`
- `sampleNo`
- `checkModeuleId`
- `checkModeuleName`
- `testItemId`
- `settlementMainId`
- `settlementSaveType`
- `settlementCreateDate`
- `clientName`

### 4. 汇总 DTO `SettlementYearSummaryDTO`

- `year`：兼容字段；当使用自定义日期范围或快捷范围时可能为空
- `settledEntrustCount`
- `partialSettledEntrustCount`
- `unsettledEntrustCount`
- `settledAmount`

## 新统计接口

### 1. 按项目分页查询结算状态

- 方法：`GET`
- 路径：`/stlm/stat/entrust/page`

参数：

- `entrustId`：项目编号，选传，支持模糊查询
- `projectName`：项目名称，选传，支持模糊查询
- `clientName`：客户名称，选传，支持模糊查询
- `settlementStatus`：选传，`SETTLED / PARTIALLY_SETTLED / UNSETTLED`
- `startDate`：选传，自定义开始日期
- `endDate`：选传，自定义结束日期，包含当天
- `datePreset`：选传，`THIS_MONTH / THIS_QUARTER / THIS_YEAR / CUSTOM`
- `year`：选传，兼容整年查询
- `page`：页码，默认 `1`
- `limit`：每页条数，默认 `20`

示例：

```http
GET /stlm/stat/entrust/page?datePreset=THIS_YEAR&settlementStatus=PARTIALLY_SETTLED&page=1&limit=20
```

响应示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "entrustId": "JC20250001",
        "projectName": "某检测项目",
        "clientName": "中国石油",
        "totalCount": 15,
        "settledCount": 10,
        "unsettledCount": 5,
        "extraSettledCount": 1,
        "settledAmount": 12800.00,
        "settlementStatus": "PARTIALLY_SETTLED"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  }
}
```

### 2. 查询单个项目的结算汇总状态

- 方法：`GET`
- 路径：`/stlm/stat/entrust/summary`

参数：

- `entrustId`：项目编号，必传

示例：

```http
GET /stlm/stat/entrust/summary?entrustId=JC20250001
```

响应字段：

- `entrustId`
- `projectName`
- `clientName`
- `totalCount`
- `settledCount`
- `unsettledCount`
- `extraSettledCount`
- `settledAmount`
- `settlementStatus`

### 3. 查询单个项目的结算差异明细

- 方法：`GET`
- 路径：`/stlm/stat/entrust/diff`

参数：

- `entrustId`：项目编号，必传

示例：

```http
GET /stlm/stat/entrust/diff?entrustId=JC20250001
```

响应示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "entrustId": "JC20250001",
    "projectName": "某检测项目",
    "clientName": "中国石油",
    "totalCount": 15,
    "settledCount": 10,
    "unsettledCount": 5,
    "extraSettledCount": 1,
    "settledAmount": 12800.00,
    "settlementStatus": "PARTIALLY_SETTLED",
    "items": [
      {
        "diffType": "MATCHED",
        "entrustId": "JC20250001",
        "sampleRegistId": "YP001",
        "sampleNo": "YP001",
        "checkModeuleId": "CM001",
        "checkModeuleName": "含水率",
        "testItemId": "TEST001",
        "settlementMainId": "STLM20250001",
        "settlementSaveType": "SAVE",
        "settlementCreateDate": "2025-01-08T10:30:00",
        "clientName": "中国石油"
      },
      {
        "diffType": "UNSETTLED",
        "entrustId": "JC20250001",
        "sampleRegistId": "YP002",
        "sampleNo": null,
        "checkModeuleId": "CM002",
        "checkModeuleName": "灰分",
        "testItemId": null,
        "settlementMainId": null,
        "settlementSaveType": null,
        "settlementCreateDate": null,
        "clientName": null
      },
      {
        "diffType": "EXTRA_SETTLED",
        "entrustId": "JC20250001",
        "sampleRegistId": null,
        "sampleNo": "YP999",
        "checkModeuleId": "CM009",
        "checkModeuleName": "异常项目",
        "testItemId": "TEST999",
        "settlementMainId": "STLM20250002",
        "settlementSaveType": "INVOICE",
        "settlementCreateDate": "2025-02-10T09:00:00",
        "clientName": "中国石油"
      }
    ]
  }
}
```

说明：

- 该接口用于查看：
  - 哪些检测项已结算
  - 结在哪个结算单里
  - 哪些检测项未结算
  - 哪些是多结算数据

### 4. 查询日期范围结算汇总

- 方法：`GET`
- 路径：`/stlm/stat/summary`

参数：

- `startDate`：选传
- `endDate`：选传
- `datePreset`：选传，`THIS_MONTH / THIS_QUARTER / THIS_YEAR / CUSTOM`
- `year`：选传，兼容整年查询

示例：

```http
GET /stlm/stat/summary?startDate=2025-01-01&endDate=2025-12-31
```

响应字段：

- `year`
- `settledEntrustCount`
- `partialSettledEntrustCount`
- `unsettledEntrustCount`
- `settledAmount`

### 5. 兼容旧路径的日期范围汇总接口

- 方法：`GET`
- 路径：`/stlm/stat/year/summary`

说明：

- 该路径为兼容保留
- 内部与 `/stlm/stat/summary` 使用同一套日期范围统计逻辑

## 导出接口

### 6. 导出指定年份的所有结算单据

- 方法：`GET`
- 路径：`/stlm/stat/export/settlements`

参数：

- `year`：必传，例如 `2025`

年份口径：

- 按 `SettlementMain.createDate` 所在年份导出

导出字段：

- 结算单号
- 结算单位
- 客户
- 结算金额
- 创建人
- 创建日期
- 附件名称
- 开票状态

开票状态说明：

- `已开票`
- `开票中`
- `未开票`

导出说明：

- 该导出会自动按多个 sheet 拆分
- 单个 sheet 最多写入 `10000` 行
- sheet 名称格式：`结算单据_1`、`结算单据_2`

### 7. 导出指定年份的项目及结算情况

- 方法：`GET`
- 路径：`/stlm/stat/export/entrusts`

参数：

- `year`：必传，例如 `2025`

年份口径：

- 根据 `Entrust.htBianHao` 是否包含年份字符串判断
- 示例：`AJC2025001Y001` 视为 `2025` 年项目

导出字段：

- 项目编号
- 合同编号
- 甲方
- 项目名称
- 结算情况
- 结算单号

结算情况取值：

- `全部结算`
- `部分结算`
- `未结算`

导出说明：

- 该导出会自动按多个 sheet 拆分
- 单个 sheet 最多写入 `10000` 行
- sheet 名称格式：`项目结算情况_1`、`项目结算情况_2`

### 8. 导出指定年份所有项目的样品和检测项目结算情况

- 方法：`GET`
- 路径：`/stlm/stat/export/entrust-items`

参数：

- `year`：必传，例如 `2025`

年份口径：

- 根据 `Entrust.htBianHao` 是否包含年份字符串判断

导出字段：

- 项目编号
- 合同编号
- 甲方
- 项目名称
- 样品编号
- 检测项目编码
- 检测项目名称
- 是否结算
- 结算单号

导出说明：

- 该导出会自动按多个 sheet 拆分
- 单个 sheet 最多写入 `10000` 行
- sheet 名称格式：`样品检测结算情况_1`、`样品检测结算情况_2`

## 旧接口说明

以下旧接口仍然保留，口径不变：

- `/stlm/stats/entrust/status`
- `/stlm/stats/entrust`
- `/stlm/stats/client`
- `/stlm/stats/contract`
- `/stlm/find/byEntrust`
- `/stlm/detail/page/client`

说明：

- 旧接口仍使用原有 `SettlementService` 统计逻辑
- 新增的三态统计、差异明细、日期范围能力，统一走新的 `/stlm/stat/*` 接口

## 备注

- 分页参数沿用通用 `RequestForm`
  - `page` 从 `1` 开始
  - `limit=0` 时服务端会强制使用默认分页大小
- 日期范围过滤作用于 `T_entrust.CreateDate`
- `endDate` 在后端会转换为“次日 00:00:00”，因此查询效果为包含结束日期当天
