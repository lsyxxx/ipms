# 结算统计接口文档

## Summary

- 模块：`admin/src/main/java/com/abt/market`
- 目标：提供 4 个结算统计查询接口
- 本次口径：只区分 `查到有效结算单` 和 `查不到有效结算单`
- 有效结算状态：`SAVE`、`INVOICING`、`INVOICE`、`PAYED`
- 不计入：`TEMP`、`INVALID`、`isDel = true`
- 返回状态：
  - `SETTLED`
  - `UNSETTLED`

## 返回结构

通用返回 DTO：`SettlementStatDTO`

- `entrustId`：项目编号
- `projectName`：项目名称
- `clientId`：客户id
- `clientName`：客户名称
- `contractNo`：合同编号
- `contractName`：合同名称
- `settled`：是否查到有效结算单
- `settlementStatus`：`SETTLED` / `UNSETTLED`
- `settlementCount`：有效结算单数量
- `settledAmount`：有效结算金额合计

## 接口文档

### 1. 查询某个项目编号是否结算

- 方法：`GET`
- 路径：`/stlm/stats/entrust/status`
- 参数：
  - `entrustId`：项目编号，必传

示例：

```http
GET /stlm/stats/entrust/status?entrustId=JC2024005
```

响应示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "entrustId": "JC2024005",
    "settled": true,
    "settlementStatus": "SETTLED",
    "settlementCount": 2,
    "settledAmount": 12800.00
  }
}
```

说明：

- 该接口用于快速判断单个项目是否已结算
- `entrustId` 必传，不分页
- 只要查到至少 1 条有效结算单，就返回 `SETTLED`

### 2. 按项目编号查看结算情况

- 方法：`GET`
- 路径：`/stlm/stats/entrust`
- 参数：
  - `entrustId`：项目编号，选传，支持模糊查询
  - `settlementStatus`：结算状态，选传，`SETTLED` / `UNSETTLED`
  - `page`：页码，默认 `1`
  - `limit`：每页条数，默认 `20`

示例：

```http
GET /stlm/stats/entrust?entrustId=JC2024005
```

说明：

- `entrustId` 为空时，分页查询全部项目
- `settlementStatus` 不传时，不按状态过滤
- 返回结果中每一行代表一个项目

响应字段：

- `entrustId`：项目编号
- `projectName`：项目名称
- `clientName`：客户名称
- `settled`：是否查到有效结算单
- `settlementStatus`：`SETTLED` / `UNSETTLED`
- `settlementCount`：有效结算单数量
- `settledAmount`：有效结算金额合计

分页返回示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "entrustId": "JC2024005",
        "projectName": "某检测项目",
        "clientName": "中国石油",
        "settled": true,
        "settlementStatus": "SETTLED",
        "settlementCount": 2,
        "settledAmount": 12800.00
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  }
}
```

### 3. 按客户查看结算情况

- 方法：`GET`
- 路径：`/stlm/stats/client`
- 参数：
  - `clientName`：客户名称，选传，支持模糊查询
  - `settlementStatus`：结算状态，选传，`SETTLED` / `UNSETTLED`
  - `page`：页码，默认 `1`
  - `limit`：每页条数，默认 `20`

示例：

```http
GET /stlm/stats/client?clientName=中国石油
```

说明：

- `clientName` 为空时，分页查询全部客户
- `settlementStatus` 不传时，不按状态过滤
- 返回结果中每一行代表一个客户

响应字段：

- `clientId`
- `clientName`
- `settled`
- `settlementStatus`
- `settlementCount`
- `settledAmount`

说明：

- 当前客户分页数据源使用 `stlm_main.client_id + client_name`
- 仅查询已存在结算单的客户
- 客户名称按 `stlm_main.client_name` 做模糊查询
- 按 `client_name` 排序

### 4. 按合同编号查看结算情况

- 方法：`GET`
- 路径：`/stlm/stats/contract`
- 参数：
  - `contractQuery`：合同查询关键字，选传，同时模糊匹配合同编号和合同名称
  - `settlementStatus`：结算状态，选传，`SETTLED` / `UNSETTLED`
  - `page`：页码，默认 `1`
  - `limit`：每页条数，默认 `20`

示例：

```http
GET /stlm/stats/contract?contractQuery=ABT2025HT
```

说明：

- `contractQuery` 为空时，分页查询全部合同
- `settlementStatus` 不传时，不按状态过滤
- 返回结果中每一行代表一个合同
- 后端使用同一个字段同时匹配 `SaleAgreement.code` 和 `SaleAgreement.name`

响应字段：

- `contractNo`
- `contractName`
- `clientName`
- `settled`
- `settlementStatus`
- `settlementCount`
- `settledAmount`

说明：

- 本接口仅统计已经通过 `stlm_rel` 绑定到销售合同 `agr_sale.code` 的结算单
- 不按委托单中的合同号进行补查

## 数据来源

- 项目分页数据源：`Entrust`
- 客户分页数据源：`SettlementMain.clientId / clientName`
- 合同分页数据源：`SaleAgreement.code` / `SaleAgreement.name`
- 合同结算匹配关系：`stlm_rel -> agr_sale`

## 详细查询接口

### 5. 查询指定项目编号的关联结算单列表

- 方法：`GET`
- 路径：`/stlm/find/byEntrust`
- 参数：
  - `entrustId`：项目编号，必传
  - `page`：页码，默认 `1`
  - `limit`：每页条数，默认 `20`

示例：

```http
GET /stlm/find/byEntrust?entrustId=JC2024005&page=1&limit=20
```

返回字段：

- `settlementId`：结算单号
- `totalAmount`：结算总金额
- `saveType`：结算单状态
- `settlementUser`：结算人
- `settlementDate`：结算时间
- `remark`：备注
- `clientName`：客户名称

响应示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "content": [
      {
        "settlementId": "STLM20250001",
        "totalAmount": 12800.00,
        "clientName": "中国石油",
        "saveType": "SAVE",
        "settlementUser": "张三",
        "settlementDate": "2025-01-08T10:30:00",
        "remark": "阶段结算"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  }
}
```

### 6. 查询指定客户的关联结算单列表

- 方法：`GET`
- 路径：`/stlm/detail/page/client`
- 参数：
  - `clientId`：客户ID，必传
  - `page`：页码，默认 `1`
  - `limit`：每页条数，默认 `20`

示例：

```http
GET /stlm/detail/page/client?clientId=CUSTOMER001&page=1&limit=20
```

返回字段：

- `id`：结算单号
- `totalAmount`：结算总金额
- `clientName`：客户名称
- `entrustIds`：项目编号列表，仅返回 `entrustId`

## 备注

- 统计接口当前不展开结算单明细
- 当前不加入日期范围筛选
- 分页参数沿用项目通用 `RequestForm`：`page` 从 `1` 开始，`limit=0` 时服务端会强制使用默认分页大小
