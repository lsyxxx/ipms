# 费用分类接口文档

## Summary

- 模块：`admin/src/main/java/com/abt/finance`
- 功能：财务人员维护费用分类基础数据，支持 CRUD、启用/禁用、排序调整
- 基础路径：`/fi/expenseCategory`
- 数据表：`fi_expense_category`
- 编号规则：字符串类型，用户手动录入，不可重复
- 名称规则：分类名称全局唯一，不可重复
- 排序规则：新增时按录入顺序自动递增 `sortNo`，支持手动批量调整
- 删除规则：业务人员不可物理删除；管理员通过角色 `JS_FI_EXPENSE_CATEGORY_DEL` 调用物理删除接口

## 数据模型

### ExpenseCategory

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 否 | 主键，UUID，新增时不传 |
| code | String | 是 | 分类编号，手动录入，最大 64 字符，唯一 |
| name | String | 是 | 分类名称，最大 128 字符，唯一 |
| sortNo | int | 否 | 排序号，新增未传时自动取 `max(sortNo)+1` |
| enabled | boolean | 否 | 是否启用，默认 `true` |
| remark | String | 否 | 备注，最大 256 字符 |
| createUserid | String | 否 | 创建人 ID（审计字段，只读） |
| createUsername | String | 否 | 创建人姓名（审计字段，只读） |
| createDate | String | 否 | 创建时间，格式 `yyyy-MM-dd HH:mm:ss` |
| updateUserid | String | 否 | 更新人 ID（审计字段，只读） |
| updateUsername | String | 否 | 更新人姓名（审计字段，只读） |
| updateDate | String | 否 | 更新时间，格式 `yyyy-MM-dd HH:mm:ss` |

### ExpenseCategoryRequestForm（查询参数）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，从 1 开始，默认 1 |
| limit | int | 否 | 每页条数，默认 20；`findList` 接口忽略分页 |
| query | String | 否 | 模糊搜索，匹配编号或名称 |
| code | String | 否 | 按编号模糊搜索 |
| name | String | 否 | 按名称模糊搜索 |
| enabledFilter | Boolean | 否 | 启用状态筛选；**不传则返回全部（含启用与禁用）** |

### ExpenseCategorySortItem（排序调整项）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 是 | 费用分类 ID |
| sortNo | int | 是 | 新的排序号，必须大于 0 |

## 统一返回结构

所有接口返回 `R<T>`：

```json
{
  "data": {},
  "code": 200,
  "msg": "操作成功",
  "count": 0,
  "timestamp": "2026-08-31 17:00:00"
}
```

分页接口 `data` 为 Spring `Page` 结构，常用字段：

- `content`：当前页数据列表
- `totalElements`：总记录数
- `totalPages`：总页数
- `number`：当前页码（从 0 开始）
- `size`：每页大小

---

## 接口列表

### 1. 按 ID 查询

```http
GET /fi/expenseCategory/findById?id={id}
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 是 | 费用分类 ID |

**响应示例**

```json
{
  "data": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "code": "01",
    "name": "办公费",
    "sortNo": 1,
    "enabled": true,
    "remark": "",
    "createDate": "2026-08-31 10:00:00"
  },
  "code": 200,
  "msg": "查询成功"
}
```

---

### 2. 分页查询

```http
GET /fi/expenseCategory/findPage?page=1&limit=20&query=办公
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| limit | int | 否 | 每页条数，默认 20 |
| query | String | 否 | 编号或名称模糊搜索 |
| code | String | 否 | 编号模糊搜索 |
| name | String | 否 | 名称模糊搜索 |
| enabledFilter | Boolean | 否 | 启用筛选；不传查全部 |

**排序**：按 `sortNo ASC, code ASC`

**响应示例**

```json
{
  "data": {
    "content": [
      {
        "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "code": "01",
        "name": "办公费",
        "sortNo": 1,
        "enabled": true
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "number": 0,
    "size": 20
  },
  "code": 200,
  "msg": "查询成功",
  "count": 1
}
```

---

### 3. 列表查询（不分页）

```http
GET /fi/expenseCategory/findList?query=差旅
```

**说明**

- 参数同分页查询，但不使用 `page/limit`
- 默认返回全部记录（含启用与禁用）
- 适用于下拉选择、导出等场景

**响应示例**

```json
{
  "data": [
    {
      "id": "...",
      "code": "02",
      "name": "差旅费",
      "sortNo": 2,
      "enabled": true
    }
  ],
  "code": 200,
  "msg": "查询成功",
  "count": 1
}
```

---

### 4. 新增 / 更新

```http
POST /fi/expenseCategory/save
Content-Type: application/json
```

**新增请求示例**

```json
{
  "code": "01",
  "name": "办公费",
  "enabled": true,
  "remark": "日常办公用品"
}
```

**更新请求示例**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "code": "01",
  "name": "办公费用",
  "sortNo": 1,
  "enabled": true,
  "remark": "更新备注"
}
```

**业务规则**

- `code`、`name` 必填
- `code` 最长 64、`name` 最长 128、`remark` 最长 256，超长返回业务异常
- `code`、`name` 均全局唯一，重复时返回业务异常
- 新增时未传 `sortNo` 或 `sortNo <= 0`，系统自动分配 `max(sortNo) + 1`，保留录入顺序
- 更新时若未传有效 `sortNo`，保留原排序号

**响应示例**

```json
{
  "data": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "code": "01",
    "name": "办公费",
    "sortNo": 1,
    "enabled": true
  },
  "code": 200,
  "msg": "保存成功"
}
```

---

### 5. 更新启用状态

```http
GET /fi/expenseCategory/update/enabled?id={id}&enabled=true
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 是 | 费用分类 ID |
| enabled | Boolean | 是 | `true` 启用 / `false` 禁用 |

**响应示例**

```json
{
  "code": 200,
  "msg": "状态更新成功"
}
```

---

### 6. 批量调整排序

```http
POST /fi/expenseCategory/update/sort
Content-Type: application/json
```

**请求示例**

```json
[
  { "id": "id-001", "sortNo": 1 },
  { "id": "id-002", "sortNo": 2 },
  { "id": "id-003", "sortNo": 3 }
]
```

**业务规则**

- 列表不能为空
- 每项 `sortNo` 必须大于 0
- 前端拖拽排序后，将完整顺序提交即可

**响应示例**

```json
{
  "code": 200,
  "msg": "排序更新成功"
}
```

---

### 7. 获取下一个排序号

```http
GET /fi/expenseCategory/sortNo
```

**说明**：新增前调用，返回 `max(sortNo) + 1`，供前端展示推荐排序号。

**响应示例**

```json
{
  "data": 4,
  "code": 200
}
```

---

### 8. 物理删除（管理员）

```http
GET /fi/expenseCategory/delete?id={id}
```

**权限要求**

- 需要角色：`JS_FI_EXPENSE_CATEGORY_DEL`
- 业务人员无此角色，无法调用
- 超级管理员（`SYSTEM_ID`）拥有全部角色，可执行删除

**说明**

- 物理删除，数据不可恢复
- 当前阶段仅提供删除接口，不做引用校验；后续若被业务单据引用，需在 service 层补充校验

**响应示例**

```json
{
  "code": 200,
  "msg": "删除成功"
}
```

**无权限响应**

```json
{
  "code": 403,
  "msg": "Access Denied"
}
```

---

## 错误码说明

| 场景 | 说明 |
|------|------|
| 分类编号已存在 | 保存时 `code` 与其他记录重复 |
| 分类名称已存在 | 保存时 `name` 与其他记录重复 |
| 分类编号不能超过64个字符 | 保存时 `code` 超长 |
| 分类名称不能超过128个字符 | 保存时 `name` 超长 |
| 备注不能超过256个字符 | 保存时 `remark` 超长 |
| 费用分类不存在 | 查询/更新/删除时 ID 无效 |
| 分类编号不能为空 | 保存校验失败 |
| 分类名称不能为空 | 保存校验失败 |
| 排序列表不能为空 | 批量排序时提交空数组 |
| 费用分类已禁用 | 业务单据保存时所选分类 `enabled=false` |
| Access Denied | 无物理删除权限 |

---

## 建表脚本

脚本路径：`sql/fi_expense_category.sql`

执行前请确认目标库为 SQL Server 2014 及以上兼容环境。

---

## 初始数据

现有分类数据见附件 `费用分类.xls`（编码 + 费用分类，共 33 条）。

导入脚本：`sql/fi_expense_category_import.sql`

1. 先执行 `sql/fi_expense_category.sql` 建表
2. 再执行导入脚本；按 `code_` 幂等，已存在编号则跳过
3. `sortNo` 按 Excel 行序从 1 到 33
4. 默认 `enabled = 1`

---

## 业务单据关联（可复用抽象）

### 设计说明

- 业务表**仅持久化快照**：`expenseCategoryCode`、`expenseCategoryName`（对应列 `exp_cat_code`、`exp_cat_name`）
- **不持久化主数据 id**；`expenseCategoryId` 为 `@Transient` 入参，保存/提交时用于查主数据并回填快照
- 可复用组件：
  - `ExpenseCategoryRef`（`@Embeddable`）
  - `WithExpenseCategory`（接口）
  - `ExpenseCategorySupport.fillAndValidate(entity, categoryId)`

### 已接入实体

| 实体 | 表 | 持久化列 |
|------|-----|----------|
| Reimburse（费用报销） | `wf_rbs` | `exp_cat_code`, `exp_cat_name` |
| PayVoucher（款项支付） | `wf_pay_voucher` | `exp_cat_code`, `exp_cat_name` |

### 请求/响应字段

| 字段 | 类型 | 持久化 | 说明 |
|------|------|--------|------|
| expenseCategoryId | String | 否 | 提交时传主数据 id；后端查主数据并写入快照 |
| expenseCategoryCode | String | 是 | 快照编号，保存后只读展示 |
| expenseCategoryName | String | 是 | 快照名称，保存后只读展示 |

### 保存校验规则

- **选填**：未传 `expenseCategoryId` 时不修改已有快照
- 传了 `expenseCategoryId`：主数据必须存在且 `enabled=true`；后端以主数据为准覆盖 `code/name` 快照

### 业务单据请求示例（费用报销 apply/save）

```json
{
  "cost": 1000.00,
  "reason": "办公用品采购",
  "expenseCategoryId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

保存成功后响应含快照（无 id）：

```json
{
  "id": "RBS202608310001",
  "expenseCategoryCode": "01",
  "expenseCategoryName": "办公费"
}
```

### 业务表变更脚本

脚本路径：`sql/wf_expense_category_ref.sql`

### 新流程接入步骤

1. 实体 `implements WithExpenseCategory`
2. 增加 `@Embedded ExpenseCategoryRef expenseCategoryRef` 与 `@Transient String expenseCategoryId`
3. 执行 ALTER TABLE 增加 `exp_cat_code`、`exp_cat_name` 两列
4. 在 `saveEntity` 前调用 `expenseCategorySupport.fillAndValidate(entity, entity.getExpenseCategoryId())`
5. 前端下拉数据来自 `/fi/expenseCategory/findList?enabledFilter=true`

---

## 前端对接建议

1. **维护列表页**：调用 `findPage`，支持 `query` 关键字搜索；提供启用/禁用筛选时使用 `enabledFilter`
2. **下拉选择**：调用 `findList`，可按需传 `enabledFilter=true` 仅展示启用项
3. **新增表单**：先调 `sortNo` 获取推荐排序号，编号由用户手动输入
4. **拖拽排序**：调整后调用 `update/sort` 提交完整顺序
5. **删除按钮**：仅对拥有 `JS_FI_EXPENSE_CATEGORY_DEL` 角色的用户展示；业务人员通过 `update/enabled=false` 禁用即可
