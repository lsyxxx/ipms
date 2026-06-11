# client_prod URL 清单

## 结论

- 核心前端：`E:\github\client_prod`
- C# 后端控制器目录：`E:\svn-proj\OpenAuth.WebApi\Controllers`
- Java 后端控制器目录：`E:\github\ipms\admin\src\main\java`、`E:\github\ipms\wxapp\src\main\java`
- 扫描范围：前端 `src` 中实际引用到的 `@/api/*.js` 模块，以及页面内基于 `VUE_APP_BASE_API` / `VUE_APP_BASE_JAVA_API` 的直连请求
- API 模块数：`75`
- URL 明细条数：`985`
- 去重后 URL 数：`969`
- 已定位到 Java 来源：`386` 条
- 已定位到 C# 来源：`564` 条
- 未定位条数：`35` 条
- 默认 Mock 禁用的 URL：`3` 条（主要在 `dynamicScheme.js`）

## 判定规则

- `src/utils/request.js` 发出的请求，一律归属 `C#` 后端。
- `src/utils/requestJava.js` 发出的请求，一律归属 `Java` 后端。
- controller 源码匹配只用于定位“对应文件与行号”，不用于反推后端归属。
- 如果某个 URL 在另一端碰巧存在同名接口，也不改归属；这种情况按未定位处理。

## 前端环境前缀

- C# 请求封装：`src/utils/request.js`，基址变量为 `VUE_APP_BASE_API`。
- Java 请求封装：`src/utils/requestJava.js`，基址变量为 `VUE_APP_BASE_JAVA_API`。
- `.env.dev` / `.env.prod` / `.env.prod.gateway` 中，典型映射为：C# 走 `/api`，Java 走 `/japi`。
- `.env.web` 中还保留了直连部署地址：C# `http://7386n59g91.goho.co:41259/api`，Java `http://7386n59g91.goho.co:55486`。

## 交付文件

- 详细清单：`E:\github\ipms\docs\client_prod-url-inventory.csv`
- 当前说明：`E:\github\ipms\docs\client_prod-url-inventory.md`

CSV 列说明：

- `url_pattern`：前端请求路径，已统一去掉域名，仅保留路径；拼接参数位置会写成 `{param}`。
- `backend_hint`：前端实际请求封装，对应唯一后端归属。
- `resolved_source`：在该归属后端仓库中匹配到的 controller 来源。
- `status`：`matched` 为已定位，`unmatched` 为未在该归属后端 controller 中直接匹配到，`defined-but-mock-disabled` 表示前端定义了 URL 但当前默认走 Mock。
- `frontend_*`：前端来源文件、函数与行号。
- `backend_*`：匹配到的后端路由、方法、文件与行号。

## 未直接定位的 URL（去重后）

- `/categorys/loadForRole` | hint=`C#` | 前端=`E:\github\client_prod\src\api\categorys.js:13`
- `/chk/setting/item/delete` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:139`
- `/chk/setting/item/save` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:175`
- `/chk/setting/module/delete` | hint=`Java` | 前端=`E:\github\client_prod\src\api\checkModule.js:247`
- `/chk/setting/module/list` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:163`
- `/chk/setting/module/{param}` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:94`
- `/chk/setting/module/{param}/dynamic-form` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:128`
- `/chk/setting/module/{param}/items` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:117`
- `/dataPrivilegeRules/loadForRole` | hint=`C#` | 前端=`E:\github\client_prod\src\api\dataprivilegerules.js:13`
- `/fi/accItem/all` | hint=`Java` | 前端=`E:\github\client_prod\src\api\cash.js:100`
- `/fi/cash/` | hint=`Java` | 前端=`E:\github\client_prod\src\api\cash.js:85`
- `/fi/cash/{param}` | hint=`Java` | 前端=`E:\github\client_prod\src\api\cash.js:85`
- `/flowInstances/TodoCount` | hint=`C#` | 前端=`E:\github\client_prod\src\api\jiancerenwu.js:187`
- `/invoice/del` | hint=`Java` | 前端=`E:\github\client_prod\src\api\finance.js:223`
- `/ipms/chkmodule/check-item/{param}` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:187`
- `/ipms/chkmodule/instruments` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:151`
- `/ipms/chkmodule/search` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:198`
- `/ipms/chkmodule/standards` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkModule.js:210`
- `/modules/get` | hint=`C#` | 前端=`E:\github\client_prod\src\api\modules.js:5`
- `/openJobs/loadForRole` | hint=`C#` | 前端=`E:\github\client_prod\src\api\openjobs.js:13`
- `/stlm/stat/export/{param}` | hint=`Java` | 前端=`E:\github\client_prod\src\api\settlement.js:253`
- `/sysLogs/loadForRole` | hint=`C#` | 前端=`E:\github\client_prod\src\api\syslogs.js:13`
- `/t_car_infos/getfenlei` | hint=`C#` | 前端=`E:\github\client_prod\src\api\t_car_infos.js:14`
- `/t_Customer_Infos/GetEntrusts` | hint=`C#` | 前端=`E:\github\client_prod\src\api\t_Customer_Infos.js:54`
- `/T_jiancediaodus/EditTestTask` | hint=`C#` | 前端=`E:\github\client_prod\src\api\jiancerenwu.js:139`
- `/T_jiancediaodus/ExtendTestTask` | hint=`C#` | 前端=`E:\github\client_prod\src\api\jiancerenwu.js:122`
- `/T_Son_CheckItems/LoadfilterCalc` | hint=`C#` | 前端=`E:\github\client_prod\src\api\checkitem.js:281`
- `/T_systemSeetingywfls/load` | hint=`C#` | 前端=`E:\github\client_prod\src\api\t_systemseetingywfls.js:5`
- `/wf/sbctstl/export` | hint=`Java` | 前端=`E:\github\client_prod\src\api\subcontractSettlement.js:148`
- `/wmsInboundOrderDtbls/loadForRole` | hint=`C#` | 前端=`E:\github\client_prod\src\api\wmsinboundorderdtbls.js:13`
- `/wmsInboundOrderTbls/loadForRole` | hint=`C#` | 前端=`E:\github\client_prod\src\api\wmsinboundordertbls.js:13`
- `/wxapp/dynamicScheme/delete` | hint=`Java` | 前端=`E:\github\client_prod\src\api\dynamicScheme.js:194`
- `/wxapp/dynamicScheme/page` | hint=`Java` | 前端=`E:\github\client_prod\src\api\dynamicScheme.js:105`
- `/wxapp/dynamicScheme/{param}` | hint=`Java` | 前端=`E:\github\client_prod\src\api\dynamicScheme.js:158`

## 说明

- C# 项目大部分 controller 使用 `[Route("api/[controller]/[action]")]`，因此文档按 `/{Controller}/{Action}` 规则回溯。
- Java 项目按 `@RequestMapping + @GetMapping/@PostMapping/...` 拼出完整路径。
- 少量前端函数会把 URL 作为参数继续传递，例如审批流通用下载/审批函数；这类“二次传入 URL”的真实落点已在调用方函数中尽量统计，但无法像字面量 URL 一样 100% 自动回溯。
- 本次已按“`request.js => C#`、`requestJava.js => Java`”严格执行；跨端同名接口不会再被当成匹配成功。