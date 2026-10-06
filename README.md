# SimpleWeibo-backend

简易微博后端服务，提供用户注册登录、发帖、帖子查询、点赞等核心功能，并内置内容审核责任链与接口限流能力。

## 技术栈

| 类别 | 技术 | 版本 |
| --- | --- | --- |
| 语言 / 运行时 | Java | 21 |
| 框架 | Spring Boot（webmvc / validation / AOP） | 4.1.0 |
| ORM | MyBatis-Plus（mybatis-plus-spring-boot4-starter） | 3.5.17 |
| 数据库 | MySQL（HikariCP 连接池） | - |
| 缓存 | Redis（Lettuce + commons-pool2）、Caffeine 本地缓存 | - |
| 消息队列 | Kafka（spring-kafka） | - |
| 工具库 | Lombok、Hutool | 5.8.47 |

## 功能模块

- **用户**：注册、登录（token 会话存 Redis）、退出登录、查询当前用户信息、按 ID 查询用户
- **帖子**：发帖（带内容审核）、帖子详情、帖子分页列表
- **点赞**：点赞 / 取消点赞 / 点赞状态 / 点赞数查询
- **内容审核**：发帖时经过责任链审核（远程校验 → DFA 敏感词检测 → 风险裁决），累计风险分达到阈值则拒绝发布
- **接口限流**：`@RateLimit` 注解 + AOP，基于 Redis，按 `userId + 业务 key` 维度限流，支持固定窗口、滑动窗口、令牌桶三种算法，触发限流返回 429

## 核心设计

### 登录态鉴权

登录成功后生成 token，以 `login:token:{token}` 的形式存入 Redis 并映射到 userId。前端通过 `Authorization: Bearer {token}` 请求头携带 token，`AuthInterceptor` 拦截 `/api/**` 并放行注册、登录接口，校验通过后将用户信息写入 `UserContext`（ThreadLocal），请求结束自动清理。

### 发帖审核责任链

发帖时内容依次经过 `PostAuditHandler` 责任链，各节点向 `AuditContext` 累加风险分：

1. `RemoteCheckHandler`（Order=1）：远程内容校验（当前为模拟实现）
2. `SensitiveWordAuditHandler`：基于 DFA 算法的敏感词检测，敏感词库来自数据库并本地缓存
3. `JudgeAuditHandler`（Order=9999）：终审裁决，风险分 ≥ 100 抛出 `AUDIT_REJECTED` 拒绝发帖

### 点赞链路（Redis + Kafka）

点赞 / 取消点赞通过 **Redis Lua 脚本**原子执行「Set 写入 + 计数增减」，天然防止重复点赞；随后发布点赞事件到 **Kafka**，由消费者异步落库（`post_like` 表 + `post` 表冗余计数原子更新），削峰填谷。Kafka 发送失败时自动回滚 Redis，保证数据一致。

### 接口限流

通过注解声明式使用，维度为登录用户：

```java
// 每个用户 60 秒内最多发 5 条微博, 使用滑动窗口算法
@RateLimit(key = "post:create", limit = 5, window = 60,
           timeUnit = TimeUnit.SECONDS, algorithm = RateLimitAlgorithm.SLIDING_WINDOW)
public Result<Void> create(CreatePostRequest request) { ... }
```

支持 `FIXED_WINDOW`（固定窗口）、`SLIDING_WINDOW`（滑动窗口）、`TOKEN_BUCKET`（令牌桶）三种算法，计数状态存 Redis，触发限流抛出 429 业务异常。

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.x
- Redis
- Kafka

### 1. 初始化数据库

创建数据库 `simple_weibo` 后，依次执行 `sql/` 目录下的建表脚本：

```sql
CREATE DATABASE simple_weibo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE simple_weibo;
SOURCE sql/user.sql;
SOURCE sql/post.sql;
SOURCE sql/post_like.sql;
SOURCE sql/sensitive_word.sql;  -- 含敏感词种子数据
```

### 2. 配置中间件连接

本地开发默认连接（见 `application-dev.yaml`）：

| 中间件 | 地址 |
| --- | --- |
| MySQL | `localhost:3305`，用户 `root`，密码 `123456` |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |

如本地端口不同，直接修改 `src/main/resources/application-dev.yaml`。

### 3. 启动

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn clean package
java -jar target/SimpleWeibo-backend-0.0.1-SNAPSHOT.jar
```

服务默认监听 `http://localhost:8123`。

### 4. 生产环境部署

通过环境变量切换 profile 并注入中间件连接信息：

```bash
SPRING_PROFILES_ACTIVE=prod \
MYSQL_HOST=<mysql-host> MYSQL_PORT=3306 MYSQL_DATABASE=simple_weibo \
MYSQL_USERNAME=<user> MYSQL_PASSWORD=<password> \
REDIS_HOST=<redis-host> REDIS_PORT=6379 REDIS_PASSWORD=<password> \
KAFKA_SERVERS=<kafka-servers> \
java -jar SimpleWeibo-backend-0.0.1-SNAPSHOT.jar
```

生产环境 CORS 默认关闭（同域 Nginx 反代部署）；前后端分离部署时通过 `app.cors.allowed-origins` 配置前端来源。

## API 概览

除注册 / 登录外，其余接口均需携带请求头 `Authorization: Bearer {token}`。

统一响应格式：

```json
{ "code": 200, "message": "成功", "data": { } }
```

### 用户

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| POST | `/api/users/register` | 注册 | 否 |
| POST | `/api/users/login` | 登录 | 否 |
| POST | `/api/users/logout` | 退出登录 | 是 |
| GET | `/api/users/me` | 查询当前登录用户 | 是 |
| GET | `/api/users/{userId}` | 查询指定用户 | 是 |

### 帖子

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| POST | `/api/posts` | 发帖（经过审核链） | 是 |
| GET | `/api/posts` | 帖子分页列表 | 是 |
| GET | `/api/posts/{postId}` | 帖子详情 | 是 |

### 点赞

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| POST | `/api/posts/likes/{postId}` | 点赞 | 是 |
| DELETE | `/api/posts/likes/{postId}` | 取消点赞 | 是 |
| GET | `/api/posts/likes/{postId}/status` | 查询当前用户点赞状态 | 是 |
| GET | `/api/posts/likes/{postId}/count` | 查询点赞数 | 是 |

## 项目结构

按业务域（package-by-feature）组织，域内再按操作拆分：

```
src/main/java/org/example/simpleweibobackend
├── common                      # 通用基础
│   ├── config                  # MyBatis-Plus、CORS、拦截器等配置
│   ├── exception               # 业务异常 + 全局异常处理器
│   ├── Result / PageVO         # 统一响应体、分页对象
│   └── BaseEntity / ErrorCode  # 实体公共字段、错误码枚举
├── user                        # 用户域
│   ├── register / login / logout   # 注册、登录、退出
│   ├── query                   # 用户信息查询
│   └── auth                    # token 鉴权拦截器、UserContext
├── post                        # 帖子域
│   ├── create                  # 发帖
│   │   └── audit               # 审核责任链（远程校验、DFA 敏感词、裁决）
│   ├── query                   # 帖子查询
│   └── like                    # 点赞（Redis Lua + Kafka 异步落库）
└── ratelimit                   # 接口限流（注解 + AOP + 三种算法）
```

## 数据库表

| 表 | 说明 |
| --- | --- |
| `user` | 用户表，用户名唯一 |
| `post` | 帖子表，含冗余点赞计数字段 `like_count` |
| `post_like` | 点赞记录表，`(user_id, post_id)` 唯一约束防重复点赞 |
| `sensitive_word` | 敏感词表，供 DFA 检测使用，含种子数据 |
