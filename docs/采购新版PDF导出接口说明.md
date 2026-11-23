# 采购新版 PDF 导出

新版采用 Apache PDFBox 2.0.37，旧下载接口及旧任务目录保留。`/purchase/create/pdf` 附件生成接口改为使用新版生成方法，保持原请求和响应格式。

## 生成方式及模板

- 继续使用 `abt.pur.excel.template`、`abt.pur.accept.excel.template` 指定的现有 Excel 模板。
- FastExcel 在内存中填充模板，POI 读取结果的列宽、字号、对齐、合并单元格、边框与签名图片，PDFBox 绘制 PDF。
- 每张采购单只有一个 `PDDocument`。先绘制采购申请单；已完成且已验收时，在同一文档的新页绘制验收记录，最后只保存一次。不生成中间 PDF，不执行 PDF 合并，不调用 Aspose。
- 保留模板方向：现有采购申请单 A4 纵向，验收记录 A4 横向；明细分页时重复标题、文件编号及表头。合计、签名及说明区域整体换页，超长单条明细可续页。
- 支持仓库现有六列、七列采购模板，以表头确定列数。只针对现有两种单据模板，不是通用 Excel 打印引擎；新增图表、公式计算或纵向合并等版式需另行适配。
- 明细排序采用 `sortNo`，空值靠后，不修改原实体集合。金额使用原字段，不重新计算。
- 每批至多 100 张单据，统一收集实际需要的签名用户并去重后一次查询，采购与验收共享查询结果；只查询用户编号和文件名，不加载 Base64 大字段。超大调用每段至多 1000 个用户编号，兼容 SQL Server 参数限制。没有签名用户时不执行查询，渲染期间不逐人查库。
- 保留原签名条件及缺失占位。验收模板“申请人”栏沿用采购人签名、“验收人”栏沿用创建人签名。
- 验收模板中的 `receiveTime` 在现有实体中没有对应字段，沿用现有填充行为留空，不新增或推测数据库字段。

## 单文件接口

| 方法 | 地址 | 参数 | 返回 |
| --- | --- | --- | --- |
| GET | `/purchase/pdf/export` | `id`：采购申请编号，必填 | 采购申请单 PDF |
| GET | `/purchase/pdf/export-accept` | `id`：采购申请编号，必填 | 验收记录 PDF |
| GET | `/purchase/create/pdf` | `id`：采购申请编号，必填 | `R<String>`，data 为采购申请 PDF 的绝对路径 |

两个下载接口返回 `application/pdf`，附件名为 `采购申请单.pdf` 或 `采购验收单.pdf`。验收记录要求流程已完成且已验收。接口沿用应用登录校验，不向前端返回实体；参数、业务异常交由全局异常处理。

`/purchase/create/pdf` 供采购详情“去支付”和“去报销”生成采购申请附件，仍返回原来的文件绝对路径，前端无需调整。文件保存于 `abt.rbs.pur.dir`，使用唯一文件名避免同时生成时覆盖；只包含采购申请单，即使已验收也不追加验收记录。此入口不再使用 Aspose。

## 批量导出接口

统一前缀：`/purchase/pdf/document-export`。

| 方法 | 地址 | 用途 |
| --- | --- | --- |
| POST | `/save` | 创建任务，JSON 参数为 `PurchaseDocumentExportRequest` |
| GET | `/findList` | 当前用户最近 50 个未过期任务 |
| GET | `/findById?id=...` | 查看任务状态 |
| POST | `/cancel?id=...` | 取消任务 |
| GET | `/download?id=...` | 下载完成的 ZIP |

请求示例：

```json
{
  "scope": "ALL",
  "query": "试剂",
  "state": "已通过",
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "depts": "部门编号1,部门编号2"
}
```

JSON 返回沿用 `R<PurchaseDocumentExportVO>`，字段、数字状态枚举、`MY/TODO/DONE/ALL` 查询范围与旧版一致。下载接口返回 `application/zip`。前端可沿用旧任务交互，改用新前缀。

- 编号快照落盘，每批最多 100 个编号，逐单生成并写入 ZIP；单据加载完整明细，不展开主表分页。
- 每单一个 PDF，文件名 `单据编号-总金额-申请人-部门.pdf`。已完成且已验收时含两种单据；否则仅申请单，并在清单说明原因。
- ZIP 含 `导出结果清单.csv`（UTF-8 BOM）和 `导出说明.txt`。
- 同一用户在新版中同时只能有一个未结束任务；查看、取消、下载仅允许任务创建者。
- 保留单据失败隔离、任务取消、重启中断标记、24 小时保留与过期清理。
- 新旧任务使用不同服务实例和目录，共用现有有界 `purchaseDocumentExportExecutor` 线程池。
- 仍为单实例任务实现，多实例部署约束与旧版相同。

## 配置

无需修改旧配置即可使用。下列是新增的可选属性：

| 属性 | 默认值 | 说明 |
| --- | --- | --- |
| `abt.pur.pdf.font` | `classpath:fonts/purchase/NotoSerifSC-Regular.ttf` | 内置思源宋体风格 Noto Serif SC 静态 TTF，按需嵌入字符；可配置 `file:/...` 替换为允许嵌入的完整中文静态 TTF |
| `abt.pur.pdf.export-dir` | `${abt.temp.dir}/purchase-pdf-document-exports` | 新版任务专用目录，不应配置为旧任务目录 |

中文字体随应用打包，附 SIL OFL 1.1 许可，不依赖服务器安装宋体。保留模板字号，字形使用内置开源字体，因此不承诺与旧 Aspose 输出逐像素相同。字体或模板缺失明确报错。

## 验证

```powershell
.\mvnw.cmd -pl admin -am -DskipTests=false "-Dtest=PurchasePdfServiceImplTest,PurchasePdfDocumentExportServiceImplTest,PurchasePdfSignatureQueryTest" -Dsurefire.failIfNoSpecifiedTests=false test
```

覆盖签名批量查询次数、跨单据去重、空用户跳过、SQL Server 参数分片、真实实体下的签名投影查询解析，以及真实模板输出、页面方向及顺序、签名图片、字体嵌入、空明细、长明细分页、排序、验收状态、无中间文件、批量查询条件、任务所有者隔离、取消、失败、重启和过期清理。测试样例生成在 `admin/target/purchase-pdf-preview/`。
