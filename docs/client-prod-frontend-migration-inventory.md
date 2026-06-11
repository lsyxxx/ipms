# client_prod 前端功能迁移清单

## 1. 说明

- 主线前端：`E:\github\client_prod`
- 目标前端：`E:\github\lims\lab-lims-frontend`
- 本清单只看前端功能迁移，不讨论旧登录页；但保留系统管理、权限、菜单可见性、按钮权限等能力评估
- 迁移程度口径：
  - `已迁移`：`lab-lims-frontend` 已有明确页面路由，功能主入口已具备
  - `部分迁移`：已有页面或 API 骨架，但旧前端功能深度未完全覆盖
  - `未迁移`：目前未看到对应新页面入口，需单独补模块
- 新前端权限以 `src/constants/permissions.ts` 中的 `PERM` 为准，旧前端零散权限逻辑需统一收敛到新权限模型

## 2. 总体结论

- `lab-lims-frontend` 已完成第一批核心域迁移：系统管理、检测基础配置、业务登记、样品、任务、报告、统计、小程序运营、主审批流、人事基础
- `client_prod` 仍有大量旧域未迁入：委托执行链、结果录入、报告配置、低代码表单/流程设计、库存完整页面、工资、车辆、OA/记录、安全检查、报价/合同、WMS、签名、系统日志等
- 当前新前端更偏“按新架构重建”，不是对 `client_prod` 逐页复制；因此迁移时应优先按“功能是否已有等价入口”判断，而不是按目录名 1:1 对比

## 3. 功能清单

| 功能分类 | client_prod 主要功能 | client_prod URL | lab-lims-frontend 迁移程度 | lab-lims-frontend URL | 权限/说明 |
|---|---|---|---|---|---|
| 系统管理 | 用户管理 | `/usermanager/index` | 未迁移 | `/admin/users` | 新前端已有用户管理页，但旧 `usermanager/index` 的工作台型页面不是同一能力 |
| 系统管理 | 部门管理 | `/orgmanager/index` | 已迁移 | `/admin/departments` | 使用 `department:*` 权限 |
| 系统管理 | 角色管理 | `/rolemanager/index` | 已迁移 | `/admin/roles` | 使用 `role:*` 权限 |
| 系统管理 | 角色授权/资源分配 | `/rolemanager/assignRes` | 已迁移 | `/admin/role-permissions`、`/admin/api-resources` | 新前端拆成角色授权与接口资源映射 |
| 系统管理 | 权限点维护 | 旧前端无明显独立主页 | 已迁移 | `/admin/permissions` | 使用 `permission:*` 权限 |
| 系统管理 | 数据权限规则 | `/dataprivilegerules/index` | 未迁移 | - | 旧 `DataPrivilegeRules` 需单独判断是否保留或并入新授权体系 |
| 系统管理 | 模块菜单管理 | `/modulemanager/index` | 未迁移 | - | 旧 `modules/resources/accessObjs` 平台能力，现阶段新前端未见对应页 |
| 系统管理 | 资源管理 | `/resources/index` | 未迁移 | - | 与旧 OpenAuth 资源树相关，需按 `abt-lims` 权限模型重构 |
| 系统管理 | 开放岗位/入口配置 | `/openjobs/index` | 未迁移 | - | 旧 `openjobs` 未见新页 |
| 系统管理 | 系统日志 | `/syslogs/index` | 未迁移 | - | 新前端暂无日志查询页 |
| 系统管理 | 系统消息后台列表 | `/sysmessages/index` | 部分迁移 | `/messages`、`/miniapp-ops/messages`、`/admin/notify-ledger` | 新前端已迁移消息中心，但旧消息后台页未完全等价 |
| 系统管理 | 个人中心/工作台 | `/profile`、`/usermanager/infoPlatform`、`/renwulog/renwulog` | 部分迁移 | `/home`、`/messages`、`/personal/menu` | 新前端有首页、消息、个人流程快捷入口，但旧“个人工作台/任务日志”未完整保留 |
| 检测基础配置 | 检测分类 | `/categories/index` | 已迁移 | `/basic-config/test-units` | 统一到 `TestUnit` |
| 检测基础配置 | 检测项目列表 | `/check/checkModule/list` | 已迁移 | `/basic-config/test-items` | 统一到 `TestItem`，权限 `testing:view/edit` |
| 检测基础配置 | 检测项目详情 | `/check/checkModule/detail/:id` | 已迁移 | `/basic-config/test-items/:id` | 新前端已有详情页 |
| 检测基础配置 | 新增/编辑检测项目 | `/check/checkModule/add`、`/check/checkModule/edit/:id` | 已迁移 | `/basic-config/test-items/new`、`/basic-config/test-items/:id/edit` | 新前端已切到新表单结构 |
| 检测基础配置 | 检测子参数新增/编辑 | `/check/checkItem/add`、`/check/checkItem/edit/:id` | 部分迁移 | `/basic-config/test-items/:id/edit` | 新前端并入检测项目编辑，不再保留独立旧页形态 |
| 检测基础配置 | 检测负责人配置 | `/check/checkModuleAssign` | 未迁移 | - | 新前端暂无负责人配置页 |
| 检测基础配置 | 设备管理 | `/instru/list`、`/instru/add`、`/instru/edit/:id`、`/instru/detail/:id` | 已迁移 | `/basic-config/instruments`、`/basic-config/instruments/new`、`/basic-config/instruments/:id/edit` | 详情页在新前端更偏表单/列表模式 |
| 检测基础配置 | 检测标准 | `/standard/list`、`/standard/add`、`/standard/edit/:id`、`/standard/detail/:id` | 已迁移 | `/basic-config/test-standards`、`/basic-config/test-standards/new`、`/basic-config/test-standards/:id/edit` | 新前端已具备标准管理 |
| 检测基础配置 | 资质字典 / 枚举配置 | 旧分散在 `EnumLib`、`checkitem` | 已迁移 | `/basic-config/qualifications` | 新前端已独立成资质配置页 |
| 小程序运营 | 小程序动态表单列表 | `/wx/scheme/list` | 已迁移 | `/miniapp-ops/dynamic-schemes` | 新前端已建立统一运营入口 |
| 小程序运营 | 动态表单详情/编辑 | `/wx/scheme/detail/:id`、`/wx/scheme/add`、`/wx/scheme/edit/:id` | 已迁移 | `/miniapp-ops/dynamic-config/:id`、`/miniapp-ops/dynamic-config/:id/edit` | 新前端按配置详情/编辑拆分 |
| 小程序运营 | 小程序用户概览/列表/详情 | 旧前端无统一运营页 | 已迁移 | `/miniapp-ops/users`、`/miniapp-ops/users/list`、`/miniapp-ops/users/:id` | 使用 `open-user:*`、`client:*`、`invoice-title:*` 等权限 |
| 小程序运营 | 小程序消息管理 | 旧页分散 | 已迁移 | `/miniapp-ops/messages` | 使用 `message:view-all`、`message:send` |
| 检验检测主链 | 业务登记列表/概览 | `/weituodan/*`、`dashboard` 中部分统计 | 已迁移 | `/business`、`/business-overview` | 新前端已按 `Business/TestOrder` 语义重建 |
| 检验检测主链 | 业务详情 | `/weituodan/entrustDetail/:id` | 已迁移 | `/business/:id/detail` | 已转成新详情页 |
| 检验检测主链 | 样品管理 | `/weituodan/sampleregist*`、`sampletask/*` | 部分迁移 | `/business/:id/samples`、`/sample-overview`、`/samples` | 新前端已有样品页，但旧“样品登记全链路”仍未完全覆盖 |
| 检验检测主链 | 检测任务 | `/weituodan/testTaskView`、`sampletask/task*`、`jiancerenwu` | 部分迁移 | `/tasks` | 新前端已有任务主列表，但旧拆分页很多，仍需继续迁 |
| 检验检测主链 | 报告管理 | `/weituodan/viewdetailreport*`、`reportsystemsetting`、`indexreport` | 部分迁移 | `/reports` | 新前端已有报告主入口，但旧报告设置/明细/模板相关能力未全迁 |
| 检验检测主链 | 结果录入/审核 | `/weituodan/sampleresult*`、`waijianresult*`、`shouquanqianzishenhe` 等 | 未迁移 | - | 旧 `resultapi`、`zhiliangjianyan` 主导的结果链仍待补 |
| 检验检测主链 | 外检/外送样品处理 | `/weituodan/waijian*`、`workflow/sbct*` | 部分迁移 | `/workflow/sbct/*`、`/workflow/sbctstl/*` | 审批流已迁；实验室执行细页未完全迁 |
| 统计分析 | 主页统计/面板 | `/dashboard`、`dashboard/admin/index` | 已迁移 | `/stats/dashboard`、`/stats/senders`、`/stats/third-parties`、`/stats` | 已拆成多张统计页 |
| 统计分析 | 单井统计/样品统计 | `/dashboard`、旧报表页 | 已迁移 | `/stats`、`/stats/test-items`、`/samples`、`/sample-overview` | 新统计更明确 |
| 客户订单 | 客户订单概览/列表/新增/编辑/详情 | 旧前端无成型模块 | 已迁移 | `/client-orders`、`/client-orders/list`、`/client-orders/new`、`/client-orders/:id/edit`、`/client-orders/:id/detail` | 新前端为新增能力 |
| 审批中心 | 待办事项 | `/workflow/todoAll` | 已迁移 | `/workflow/todo-all` | 权限 `workflow:task:read` |
| 审批中心 | 报销 | `/workflow/myRbsList`、`/workflow/rbsAll`、`/workflow/rbs/apply`、`/workflow/rbs/detail/:entityId` | 已迁移 | `/workflow/rbs/my`、`/workflow/rbs/all`、`/workflow/rbs/apply`、`/workflow/rbs/detail/:entityId` | 已完成 URL 新命名 |
| 审批中心 | 差旅 | `/workflow/myRbsTripList`、`/workflow/rbsTripAll`、`/workflow/trip/apply`、`/workflow/trip/detail/:entityId` | 已迁移 | `/workflow/trip/my`、`/workflow/trip/all`、`/workflow/trip/apply`、`/workflow/trip/detail/:entityId` | 已迁移 |
| 审批中心 | 开票 | `/workflow/myInvoiceApplyList`、`/workflow/invoiceAll`、`/workflow/inv/apply`、`/workflow/inv/detail/:entityId` | 已迁移 | `/workflow/inv/my`、`/workflow/inv/all`、`/workflow/inv/apply`、`/workflow/inv/detail/:entityId` | 已迁移 |
| 审批中心 | 支付 | `/workflow/myPayVoucherList`、`/workflow/payVoucherAll`、`/workflow/pay/apply`、`/workflow/pay/detail/:entityId` | 已迁移 | `/workflow/pay/my`、`/workflow/pay/all`、`/workflow/pay/apply`、`/workflow/pay/detail/:entityId` | 已迁移 |
| 审批中心 | 借款 | `/workflow/myLoanList`、`/workflow/loanAll`、`/workflow/loan/apply`、`/workflow/loan/detail/:entityId` | 已迁移 | `/workflow/loan/my`、`/workflow/loan/all`、`/workflow/loan/apply`、`/workflow/loan/detail/:entityId` | 已迁移 |
| 审批中心 | 发票冲账 | `/workflow/myInvoiceOffsetList`、`/workflow/invoiceOffsetAll`、`/workflow/invoffset/apply`、`/workflow/invoffset/detail/:entityId` | 已迁移 | `/workflow/invoffset/my`、`/workflow/invoffset/all`、`/workflow/invoffset/apply`、`/workflow/invoffset/detail/:entityId` | 已迁移 |
| 审批中心 | 采购 | `/purchase/my`、`/purchase/all`、`/purchase/apply/:entityId?`、`/purchase/dtl/:entityId/:src?`、`/purchase/accept/:entityId` | 已迁移 | `/workflow/purchase/my`、`/workflow/purchase/all`、`/workflow/purchase/apply`、`/workflow/purchase/detail/:entityId`、`/workflow/purchase/accept/:entityId` | 已并入统一 workflow 命名 |
| 审批中心 | 外送申请 | `/workflow/sbct/my`、`/workflow/sbct/all`、`/workflow/sbct/apply`、`/workflow/sbct/detail/:entityId` | 已迁移 | `/workflow/sbct/my`、`/workflow/sbct/all`、`/workflow/sbct/apply`、`/workflow/sbct/detail/:entityId` | 已迁移 |
| 审批中心 | 外送结算 | `/workflow/sbctstl/my`、`/workflow/sbctstl/apply`、`/workflow/sbctstl/detail/:entityId` | 已迁移 | `/workflow/sbctstl/my`、`/workflow/sbctstl/all`、`/workflow/sbctstl/apply`、`/workflow/sbctstl/detail/:entityId` | 新前端还补了 `all` 列表 |
| 审批中心 | 流程设置 | `/workflow/wfsys`、`/workflow/setting` | 已迁移 | `/workflow/settings` | 使用 `workflow:definition:read` |
| 审批中心 | 快捷申请/财务记录入口 | 旧零散在个人工作台、财务页 | 已迁移 | `/personal/menu`、`/personal/workflow-apply`、`/finance/menu`、`/finance/workflow-records` | 新前端用菜单聚合页承载 |
| 轻量流程 | 报销轻量流程 | 旧前端无 | 已迁移 | `/light-wf/reimburse/todo`、`/done`、`/myapply`、`/all`、`/apply`、`/:id`、`/design` | 新增模块，与旧 workflow 引擎独立 |
| 人事管理 | 员工列表/详情/编辑 | `/t_employeeinfos/index`、`/detail`、`/setting` | 已迁移 | `/hr/employees`、`/hr/employees/:id`、`/hr/employees/new`、`/hr/employees/:id/edit` | 权限 `employee:*` |
| 人事管理 | 员工证件 | `/t_employeeinfos/certlist`、`/sglcertlist` | 已迁移 | `/hr/employee-certs` | 已有独立页 |
| 人事管理 | 打卡记录上传 | `/dakalog/setting` | 未迁移 | - | 旧 `ImportDakaLog` 未见新页 |
| 人事管理 | 排班、考勤、年统计 | `/t_employeeinfos/paiban`、`kaoqin*`、`daka*` | 未迁移 | - | 新前端暂未承接 |
| 财务管理 | 财务菜单/流程记录 | `/finance/*` 中部分流程相关入口 | 部分迁移 | `/finance/menu`、`/finance/workflow-records` | 新前端偏流程视角，旧固定资产/账目仍未全迁 |
| 财务管理 | 固定资产、资金分类、二级分类、收付款登记 | `/finance/fixedAsset/*`、`/finance/setting`、`/finance/rp/*`、`/finance/accountItems` | 未迁移 | - | 新前端暂无对应业务页面 |
| 结算管理 | 结算列表/新增/详情/编辑/汇总 | `/settlement/list`、`/settlement/apply`、`/settlement/detail/:id`、`/settlement/edit/:id`、`/settlement/entrust` | 未迁移 | - | 已有 `src/api/settlement.ts`，但未见路由页面 |
| 库存管理 | 出入库登记、库存、盘点、月周报、设置 | `/stock/register/:stockType`、`/stock/dtl/*`、`/stock/inv/list`、`/stock/chk/*`、`/stock/setting`、`/stock/smry/*` | 未迁移 | - | 已有 `src/api/stockApi.ts`，但未见完整视图路由 |
| 客户管理 | 客户详情/表单 | `/t_customer_infos/index`、`/customer/detail/:id` | 部分迁移 | 仅见组件 `t_customer_infos/CustomerForm.vue` | 新前端存在客户表单组件，但缺完整客户列表/详情路由 |
| 供应商管理 | 供应商列表/表单 | `/t_supply_infos/index` | 部分迁移 | 仅见组件 `t_supply_infos/SupplierForm.vue` | 同上，路由未完整建立 |
| 车辆管理 | 车辆列表/车辆动态 | `/t_car_infos/index`、`/t_car_infos/carDynamic` | 未迁移 | - | 新前端暂无车辆模块 |
| 安全检查 | 检查项目配置、表单配置、记录申请、列表、详情 | `/safety/item/config/:checkType`、`/safety/form/config/:checkType`、`/safety/record/*` | 未迁移 | - | 新前端暂无安全模块 |
| 工资/薪资 | 工资导入、汇总、审批、个人工资条 | `/sl/import/*`、`/sl/chk/*`、`/sl/my/index`、`/wagesmanage/*` | 未迁移 | - | 新前端暂无工资模块 |
| OA/记录 | 行政/人事/科研/设备/请假/出差等记录类页面 | `/record/*`、`/OA/*` | 未迁移 | - | 旧 OA 记录域整体未迁 |
| 协议/合同/报价 | 预协议、销售合同、市场看板、报价项目、报价方式、采购合同 | `/agreement/*`、`/market/board`、`/baojiaxiangmu/*`、`/weituodan/caigouhetong` | 未迁移 | - | 新前端暂无对应模块 |
| 低代码表单与流程设计 | 表单设计 | `/forms/add`、`/forms/edit/:id` | 未迁移 | - | 旧低代码平台能力未迁 |
| 低代码表单与流程设计 | 流程设计 | `/flowschemes/add`、`/flowschemes/edit/:id` | 未迁移 | - | 旧流程模板设计器未迁 |
| 低代码表单与流程设计 | 流程实例中心 | `/flowinstances/add`、`/flowinstances/detail/:id`、`/flowinstances/verify/:id`、`/flowinstances/update/:id`、`/flowInstanceWait/flowInstanceWait` | 未迁移 | - | 与新审批中心不是同一实现 |
| 低代码表单与流程设计 | BuilderTable/自定义表 | `/buildertables/index` | 未迁移 | - | 未见新页 |
| 文件与附件 | 行政通知附件、通用上传、附件详情 | `/uploadfiles/attachment`、`/uploadfiles/attachmentDetail/:id` | 未迁移 | - | 新前端有文件 API，但旧附件业务页未迁 |
| 签名管理 | 用户签名列表 | `/ussig/list` | 未迁移 | - | 新前端暂无签名模块 |
| WMS / 入库单 | 入库单列表与明细 | `/wmsinboundordertbls/index` | 未迁移 | - | 新前端暂无页面 |

## 4. 权限迁移重点

### 4.1 已在新前端明确建模的权限域

- 系统管理：`user:*`、`role:*`、`department:*`、`permission:*`、`api_resource:*`
- 检验检测：`test-order:*`、`sample:*`、`task:*`、`file:*`、`stats:view`
- 检测配置：`testing:view`、`testing:edit`
- 小程序运营：`open-user:*`、`client:*`、`favorite-item:*`、`invoice-title:*`
- 审批中心：`workflow:*`
- 轻量流程：`wf-reimburse:*`、`wf-design:*`
- 人事：`employee:*`、`employee-cert:*`
- 消息/通知：`message:*`、`notify:*`

### 4.2 旧前端权限需要迁移适配的重点

- 旧 `client_prod` 中存在大量页面级 `notauth`、菜单动态装配、模块资源树、角色资源分配、数据权限规则
- 迁移到新前端时应统一收敛到：
  - 路由 `meta.requiresPerm`
  - 路由 `meta.requiresAnyPerm`
  - `PERM` 常量
  - 后端 `permission/api_resource/role_permission`
- 对旧的以下域要额外补权限设计：
  - 库存
  - 结算/财务固定资产
  - 工资/薪资
  - 安全检查
  - OA/记录
  - 低代码表单/流程设计
  - 签名/上传附件/WMS

## 5. 建议的迁移优先级

### P1：已迁移但需补深度

- 检测主链：业务、样品、任务、报告
- 检测基础配置：检测项目/参数/标准/动态表单
- 审批中心：核对旧流程页是否都被新 workflow 覆盖
- 系统管理：核对旧用户工作台和消息能力是否需要补

### P2：已有 API 或业务必要性高，但页面未完整迁入

- 结算
- 库存
- 客户/供应商完整管理页
- 人事中的打卡/考勤/排班

### P3：目前整块未迁的旧域

- 结果录入/审核/外检执行链
- OA/记录
- 工资/薪资
- 安全检查
- 低代码表单/流程平台
- 车辆、报价、合同、WMS、签名、系统日志

## 6. 备注

- `lab-lims-frontend` 已经不是按 `client_prod` 原样目录迁移，而是按新信息架构重组；因此“目录不存在”不等于“功能不存在”
- 部分功能在新前端已合并：
  - 旧 `checkItem` 独立页并入 `TestItem` 编辑页
  - 旧零散 workflow 入口合并到 `/workflow/*` 与菜单聚合页
  - 旧消息、通知、权限配置拆分到多张新页面
- 当前文档中的 URL 以“前端页面路由”为主，不包含后端接口 URL
