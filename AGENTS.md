# IPMS Agent Guide

本文件为仓库级代理协作规范，内容参考 [E:\github\ipms\.cursor\rules\java-backend.mdc](E:/github/ipms/.cursor/rules/java-backend.mdc) 整理而成。对本仓库进行分析、编码、调试、SQL 编写、脚本生成时，默认遵循以下规则。

## 项目背景

- 本项目是 Spring Boot + JPA 企业级后端项目
- 必须遵循 MVC 分层思想
- 修改代码时优先最小改动，保持现有风格一致
- 不要随意重构，不要修改无关文件

## 分层规范

项目分层：

- `controller`
- `service`
- `serviceImpl`
- `repository`
- `entity`
- `model`

依赖关系必须严格遵守：

- `controller -> service -> repository`

禁止跨层调用。

### controller 层

职责：

- 接收请求
- 参数校验
- 调用 service
- 返回结果

规则：

- controller 层包名为 `controller`
- controller 中方法传入参数数据校验，优先使用 `@Valid`/`@Validated`，且要在校验字段上写出中文 `message` 提示
- 无法使用 `@Valid`/`@Validated` 时，在 controller 中显式校验
- controller 中只能调用 service
- 禁止 controller 直接调用 repository
- 禁止 controller 编写业务逻辑
- 禁止 controller 操作数据库
- controller 保持轻量化
- 禁止返回前端 `Map` 类型数据

### service 层

职责：

- 定义业务接口
- 定义业务方法

规则：

- service 只定义接口
- 不写实现逻辑
- `serviceImpl` 实现 `service`

### serviceImpl 层

职责：

- 实现业务逻辑
- 调用 repository
- 处理事务

规则：

- 数据库操作必须通过 repository
- 复杂业务逻辑写在 `serviceImpl`
- 禁止在 repository 中写业务逻辑
- 禁止返回 `Map` 类型数据

### repository 层

职责：

- 数据访问
- JPA 查询

规则：

- repository 只负责数据库访问
- 不写业务逻辑
- 优先使用 JPA 方法命名
- 复杂 SQL 使用 `@Query`
- 非必要不要使用原生 SQL

## entity 规范

规则：

- entity 必须与数据库表对应
- 一个 entity 对应一张表
- entity 只表示数据库结构
- entity 不用于前端直接返回
- entity 不用于复杂业务聚合
- 允许返回 entity 对象
- entity 类上必须添加 `@JsonInclude(JsonInclude.Include.NON_EMPTY)`

命名规范：

- 类名使用 PascalCase
- 表名使用 `snake_case`
- 字段名使用 `camelCase`

禁止：

- entity 中编写业务逻辑
- entity 中编写复杂计算
- entity 直接返回给前端

## model 规范

model 表示应用层对象，包括：

- DTO
- VO
- Query
- Request
- Response

规则：

- DTO 用于数据传输
- VO 用于页面展示
- Query 用于查询条件
- Request 用于接口请求参数
- Response 用于接口返回结构

禁止：

- model 与数据库强绑定
- model 中包含数据库操作

## controller 命名与 URL 规范

controller 类：

- 使用 `XxxController` 命名

URL 使用：

- 小写
- 中划线
- REST 风格

获取数据统一使用 `find` 开头，例如：

- `/sample/findById`
- `/sample/findList`
- `/sample/findPage`
- `/sample/save`
- `/sample/update`
- `/sample/delete`

禁止：

- `getData`
- `queryData`
- `selectData`
- `listData`

## 方法命名规范

controller 与 service 方法命名保持一致：

- `findById`
- `findList`
- `findPage`
- `save`
- `update`
- `delete`

禁止：

- `get`
- `query`
- `select`
- `search`

repository 方法：

- 优先使用 JPA 命名规则
- 复杂查询使用 `@Query`
- SQL 保持简单清晰

禁止：

- 超长方法名
- repository 中写复杂业务逻辑

## 返回结构规范

接口返回统一结构：

- `Result<T>`

示例：

- `Result<SampleVO>`
- `Result<List<SampleVO>>`
- `Result<PageResult<SampleVO>>`

禁止直接返回：

- entity
- `List<entity>`

## 分页规范

- 分页接口统一使用 `findPage`
- 分页返回对象使用 `org.springframework.data.domain.Page`

## 参数规范

- controller 参数超过 3 个时必须封装对象
- 查询条件封装对象继承 `RequestForm`

禁止：

- controller 方法参数过长
- 大量 `@RequestParam`

## 异常处理规范

- 统一使用全局异常处理
- 使用 `BusinessException`
- 使用 `GlobalExceptionHandler`

禁止：

- controller 中 `try-catch`
- 返回字符串错误信息

## 日志规范

- 必须使用 slf4j
- 关键业务必须记录日志
- 错误必须记录 `error` 日志
- 不打印敏感信息

禁止：

- `System.out.println`

## 注释规范

- 类必须添加注释
- 复杂方法必须添加注释

禁止：

- 无意义注释
- 注释与代码不一致

## 事务规范

- 事务写在 `serviceImpl`

禁止：

- controller 使用事务
- repository 使用事务

## 代码风格规范

- 优先可读性
- 方法保持简洁
- 单个方法不要过长
- 单个类职责单一
- 优先最小改动原则

禁止：

- 过度封装
- 炫技代码
- 无意义抽象

## AI 行为约束

- 禁止虚构数据库字段
- 禁止猜测接口结构
- 必须基于已有代码分析
- 修改代码时优先最小改动
- 不要随意重构
- 不要修改无关文件
- 优先复用已有代码
- 保持现有代码风格一致

## JPA 使用规范

优先使用：

- `JpaRepository`
- `Specification`
- `Pageable`

更新数据时：

- 优先查询后修改
- 避免直接 `UPDATE SQL`

禁止：

- 滥用 `nativeQuery`
- 手写大量 SQL

## 时间字段规范

统一字段：

- `createTime`
- `createUserId`
- `updateTime`
- `updateUserId`

时间格式：

- `yyyy-MM-dd HH:mm:ss`

## 状态字段规范

状态字段统一：

- `status`

规则：

- 使用数字枚举
- 禁止 magic number

示例：

- `0` - 禁用
- `1` - 启用

## 枚举规范

- 状态值优先使用 `enum`

禁止：

- `if (status == 1)`

使用：

- `StatusEnum.ENABLED`

## 安全规范

禁止：

- SQL 拼接
- 明文密码
- 返回敏感字段

必须：

- 参数校验
- 防止空指针
- 校验权限

## SQL 规范

规则：

- SQL 关键字大写
- `WHERE` 条件清晰
- 避免 `SELECT *`
- 本项目数据库统一按 SQL Server 语法开发
- 本地测试数据库版本为 Microsoft SQL Server 2022 Express（16.0.1000.6）
- 生产环境数据库版本为 Microsoft SQL Server 2014 Standard（12.0.2000.8）
- 所有 SQL、JPA 原生查询、建表/改表脚本必须以 SQL Server 2014 兼容性为准，不能只在 2022 上可运行
- 编写 SQL 时优先使用 SQL Server 2014 已支持的语法和函数
- 新增数据库脚本前，必须考虑生产环境是否支持对应语法、函数、数据类型和 DDL 写法

兼容性禁止事项：

- 禁止使用 SQL Server 2016+ 才提供的特性，例如 `STRING_SPLIT`、`STRING_AGG`、`OPENJSON`、`FOR JSON`、`DROP IF EXISTS`、`CREATE OR ALTER`
- 禁止默认假设生产环境支持 2022 的新函数、新语法或更高兼容级别
- 禁止在未确认兼容性的情况下引入新版驱动特性对应的数据库能力

兼容性建议：

- 字符串聚合优先使用 `STUFF + FOR XML PATH`
- 需要兼容删除对象时，优先使用 `IF OBJECT_ID(...) IS NOT NULL`
- 编写分页、排序、临时表、DDL、函数调用时，先按 SQL Server 2014 校验
- 若本地 2022 与生产 2014 行为可能不一致，必须在代码注释、脚本注释或交付说明中明确说明风险

禁止：

- 超长 SQL

## 文件修改原则

- 优先最小修改
- 不修改无关代码
- 不删除已有逻辑
- 保持向下兼容
- 新功能优先参考已有实现
