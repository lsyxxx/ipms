# IPMS 文件中心

`file-center` 是共享 Maven JAR 模块，依赖 `common`，由 `admin` 加载运行。
MinIO SDK 版本与参考项目 `abt-lims` 保持一致（8.5.12）。
当前提供单文件流式上传，不写服务器本地文件，不保存数据库元数据。
现有 `/sys/file/*` 接口继续使用原有实现。

## 配置

在启动 `admin` 的进程环境中设置：

```text
FILE_CENTER_MINIO_ENABLED=true
FILE_CENTER_MINIO_ENDPOINT=http://127.0.0.1:9000
FILE_CENTER_MINIO_ACCESS_KEY=<访问密钥>
FILE_CENTER_MINIO_SECRET_KEY=<私有密钥>
FILE_CENTER_MINIO_BUCKET=ipms
FILE_CENTER_MINIO_MAX_SIZE_BYTES=209715200
```

默认关闭；开启后校验必填配置。桶需要事先创建并授权上传，应用不创建桶或修改匿名访问策略。
建议使用私有桶。单文件默认上限为 200 MiB，实际上传还受到 Spring multipart 及网关请求大小限制。
密钥通过环境变量配置，不提交到仓库。

## 上传接口

`POST /file-center/upload`，`Content-Type: multipart/form-data`，需要 IPMS 登录态（`X-Token`）。
Swagger 接口标识为 `admin.fileCenter.upload`。
经过前端 Java 代理时使用 `/japi/file-center/upload`，沿用 `requestJava` 的基址配置。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| file | 文件 | 必填，非空文件 |
| directory | 字符串 | 可选，默认 `uploads`；1–64 位字母、数字、中划线、下划线，不能包含路径分隔符 |

```shell
curl -X POST http://localhost:8080/file-center/upload \
  -H "X-Token: <登录令牌>" \
  -F "file=@report.pdf" \
  -F "directory=reports"
```

返回沿用 IPMS 当前的 `R<FileUploadResponse>`（项目现有统一返回类为 `R`）：

```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "bucket": "ipms",
    "objectKey": "reports/20261027/550e8400-e29b-41d4-a716-446655440000.pdf",
    "originalName": "report.pdf",
    "contentType": "application/pdf",
    "size": 1024,
    "etag": "<MinIO返回的ETag>"
  }
}
```

对象键使用 `directory/yyyyMMdd/UUID.ext`，避免同名覆盖；响应不包含密钥或永久公开 URL。
`contentType` 来自上传请求，缺失时使用 `application/octet-stream`，不代表已验证真实文件格式。
当前不提供元数据落库、文件业务绑定、预签名访问、下载、删除和业务级上传规则，
也未把旧附件结构迁移到 MinIO；后续功能应在本模块继续扩展。

## 验证

```shell
mvn -pl file-center -am test -DskipTests=false
mvn -pl admin -am compile -DskipTests
```

单元测试使用模拟客户端，无需启动 SQL Server 或 MinIO；部署后需要使用真实配置执行上传联调。
