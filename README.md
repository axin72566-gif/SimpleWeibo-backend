# SimpleWeibo Backend

基于 Java 21 和 Spring Boot 的简易微博后端，提供用户注册、帖子发布、详情查询和热榜接口，并包含可扩展的内容审核责任链与注解式限流组件。

## 功能

- **用户注册**：校验用户名、密码长度，生成随机昵称，返回不含密码的用户信息。
- **发布帖子**：按黑名单、敏感词、最终裁决的顺序执行审核，通过后写入数据库。
- **帖子详情**：查询详情时在数据库端原子增加浏览量，保持帖子更新时间不变。
- **热榜**：召回最近 7 天内最新的至多 1000 条帖子，计算热度后返回前 10 条。
- **接口限流**：支持固定窗口、滑动窗口、令牌桶、漏桶四种算法，以及接口、用户、IP 三种维度。注册接口按 IP 限制 60 秒内最多 5 次。
- **公共基础设施**：统一响应、全局异常处理、字段自动填充、分页插件和跨域配置。

当前未实现登录、令牌认证、关注、评论、点赞以及通用帖子列表接口。

## 技术栈

| 技术 | 版本 / 用途 |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.1.0，Web MVC、Validation、AOP |
| MyBatis-Plus | 3.5.17，数据访问与分页 |
| MySQL | 用户与帖子持久化 |
| Spring Data Redis / Lettuce | 已引入依赖及连接配置，当前业务和限流未使用 Redis 存储 |
| Hutool | 5.8.47，工具库 |
| Lombok | 简化实体与构造器代码 |
| Maven | 构建与依赖管理 |

## 快速开始

### 1. 准备环境

安装 JDK 21、Maven 和 MySQL，并确保 `java`、`mvn`、`mysql` 命令可用。仓库未包含 Maven Wrapper，需要使用本机 Maven。

默认开发配置如下：

| 配置项 | 默认值 |
| --- | --- |
| Spring Profile | `dev` |
| HTTP 端口 | `8123` |
| MySQL 地址 | `localhost:3305`（注意不是常见的 3306） |
| 数据库名 | `simple_weibo` |
| MySQL 用户名 | `root` |
| Redis 地址 | `localhost:6379`，数据库 `0` |

基础配置位于 `src/main/resources/application.yaml`，开发配置位于 `src/main/resources/application-dev.yaml`。数据库密码请按本机环境设置。Redis 已配置，但当前接口没有读写 Redis 的逻辑。

### 2. 初始化数据库

从项目根目录打开终端，连接 MySQL（按实际环境修改端口和用户）：

```sh
mysql -h localhost -P 3305 -u root -p
```

在 MySQL 客户端中执行：

```sql
CREATE DATABASE IF NOT EXISTS simple_weibo
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE simple_weibo;
SOURCE sql/user.sql;
SOURCE sql/post.sql;
```

SQL 文件仅负责建表，不包含建库或测试数据；建表语句不支持重复执行。项目未配置自动执行这些脚本，需要手动导入。

### 3. 配置连接并启动

可以修改开发配置，也可以通过环境变量覆盖。以下为 PowerShell 示例：

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3305/simple_weibo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
$env:SPRING_DATASOURCE_USERNAME = 'root'
$env:SPRING_DATASOURCE_PASSWORD = '替换为本机数据库密码'
mvn spring-boot:run
```

启动后，接口基础地址为 `http://localhost:8123`。本仓库仅提供后端，没有首页或 Swagger UI。

### 4. 构建与测试

```sh
mvn test
mvn clean package
java -jar target/SimpleWeibo-backend-0.0.1-SNAPSHOT.jar
```

目前测试仅包含 `@SpringBootTest` 的应用上下文加载测试，不覆盖业务流程。运行测试前应准备好开发配置所需的环境；`mvn clean package` 默认也会运行测试。

## API

请求体使用 `application/json`，接口返回统一结构：

```json
{
  "code": 200,
  "message": "成功",
  "data": {}
}
```

`data` 随接口变化；失败响应中的 `data` 为 `null`。

| 方法 | 路径 | 参数 | 成功时的 data |
| --- | --- | --- | --- |
| POST | `/api/users/register` | JSON：`username`、`password` | 用户信息 |
| POST | `/api/posts` | 请求头：`X-User-Id`；JSON：`title`、`content` | 新帖子信息 |
| GET | `/api/posts/{id}` | 路径参数：帖子 ID | 帖子信息，浏览量加 1 |
| GET | `/api/posts/hot` | 无 | 帖子数组，最多 10 条 |

字段约束与返回值：

- `username`：非空白，2–20 个字符；`password`：非空白，6–50 个字符。
- `title`：非空白，最多 100 个字符；`content`：非空白，最多 500 个字符。
- 用户信息包含 `id`、`username`、`nickname`。
- 帖子信息包含 `id`、`userId`、`title`、`content`、`viewCount`、`createTime`。

### PowerShell 调用示例

注册用户：

```powershell
$baseUrl = 'http://localhost:8123'
$registerBody = @{ username = 'demo_user'; password = 'demo_password' } | ConvertTo-Json
$registration = Invoke-RestMethod -Method Post -Uri "$baseUrl/api/users/register" -ContentType 'application/json; charset=utf-8' -Body $registerBody
$registration.data
```

发布帖子前，请使用注册返回的用户 ID。默认审核黑名单包含用户 ID **1 和 2**；全新数据库前两个注册用户会命中黑名单，联调发帖请使用后续注册用户，或按业务需求调整 `BlacklistAuditHandler`。

```powershell
$userId = $registration.data.id
$postBody = @{ title = 'Hello SimpleWeibo'; content = 'My first post.' } | ConvertTo-Json
$created = Invoke-RestMethod -Method Post -Uri "$baseUrl/api/posts" -Headers @{ 'X-User-Id' = "$userId" } -ContentType 'application/json; charset=utf-8' -Body $postBody
$created.data

# 查看详情，每次调用都会增加浏览量
Invoke-RestMethod -Uri "$baseUrl/api/posts/$($created.data.id)"

# 获取热榜
Invoke-RestMethod -Uri "$baseUrl/api/posts/hot"
```

## 核心实现

### 内容审核

`PostAuditChain` 按 `@Order` 顺序执行 `PostAuditHandler`：

1. `BlacklistAuditHandler`：用户 ID 为 1 或 2 时增加 100 风险分。
2. `SensitiveWordAuditHandler`：标题与正文拼接后，每命中一个词增加 100 分。当前词库为“赌博、诈骗、刷单、代开发票、外挂”。
3. `JudgeAuditHandler`：累计风险分达到 100 时拒绝发布。

新增审核规则可实现 `PostAuditHandler`，注册为 Spring 组件并指定执行顺序。

### 热榜

热榜按“召回 → 热度计算 → 排序”分层，对应 `HotPostRecaller`、`HotPostCalculator`、`HotPostRanker` 三个接口。默认热度公式为：

```text
热度 = 浏览量 / (发布至今的秒数 / 86400 + 1)
```

每次请求实时查询、计算并排序，当前没有热榜缓存或定时刷新任务。

### 限流

`@RateLimit` 经 AOP 拦截后，由 `RateLimiterRegistry` 选择算法。状态保存在 JVM 内存中，应用重启会清空，多实例之间不共享额度。

用户维度读取 `X-User-Id`，缺失时回退到 IP；IP 使用请求的 `getRemoteAddr()`。部署在反向代理之后时，需要结合代理配置确认实际识别到的客户端地址。

## 目录结构

```text
SimpleWeibo-backend/
├── pom.xml
├── sql/
│   ├── user.sql                         # 用户表
│   └── post.sql                         # 帖子表
└── src/
    ├── main/
    │   ├── java/org/example/simpleweibobackend/
    │   │   ├── SimpleWeiboBackendApplication.java
    │   │   ├── common/                  # 响应、异常、配置、限流
    │   │   ├── user/register/           # 用户注册
    │   │   └── post/
    │   │       ├── create/              # 发帖
    │   │       ├── query/               # 详情与浏览量
    │   │       ├── audit/               # 审核责任链
    │   │       └── hot/                 # 热榜召回、计算、排序
    │   └── resources/
    │       ├── application.yaml
    │       └── application-dev.yaml
    └── test/java/org/example/simpleweibobackend/
        └── SimpleWeiboBackendApplicationTests.java
```

## 当前实现边界

以下行为需要在接口联调时留意：

- 发帖直接读取 `X-User-Id`，当前没有认证流程，也没有校验该用户是否存在。
- 注册使用固定盐的 SHA-1 摘要保存密码；当前没有用户名重复检查，建表脚本也未建立用户名唯一索引。
- 全局异常处理尚未单独处理参数校验、缺失请求头等异常，这些请求可能返回 500。
- 查询不存在的帖子时，浏览量更新先失败，当前会返回 500，而不是预期的 404。
- 审核拒绝使用业务码 `1001`，异常处理又将其直接用作 HTTP 状态码，尚需将业务码与标准 HTTP 状态码分开处理。
