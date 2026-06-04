# 校园二手交易平台

基于 Vue3 + Element Plus + Spring Boot 的校园二手交易平台原型项目，包含用户前台、管理员后台、统一 API 客户端、Spring Boot MVC 三层接口骨架和数据库设计文档。

当前状态：前端页面和交互已完成静态原型；后端已切换为 Spring Boot MVC + JDBC，数据库使用 MySQL `second_hand_trade`；建表脚本和初始化数据脚本已提供并已验证可执行；前端演示数据、数据库种子数据、后台管理演示数据已按同一批商品/订单/求购/置换/举报/纠纷/公告数据同步。

## 技术栈

前端：

- Vue 3
- Vue Router
- Pinia
- Element Plus
- ECharts
- Axios
- Vite

后端：

- Java 17
- Spring Boot 4
- Spring Web MVC
- Maven Wrapper

数据库设计：

- MySQL 8.x

## 项目结构

```text
Second-hand trading platform
├─ API.md                 # 接口文档
├─ DATABASE.md            # 数据库表设计
├─ README.md              # 项目说明
├─ backend                # Spring Boot 后端
│  └─ src/main/java/com/example/Second_hand/trading/platform
│     ├─ controller       # Controller 层，负责 REST API
│     ├─ service          # Service 层，当前通过 JDBC 读取数据库
│     ├─ dto              # DTO/统一响应结构
│     ├─ config           # CORS 等配置
│     └─ SecondHandTradingPlatformApplication.java
│  ├─ sql                 # 建表和初始化数据脚本
│  └─ scripts             # 数据库初始化执行脚本
└─ fronted                # Vue3 前端
   └─ src
      ├─ assets           # 全局样式和静态资源
      ├─ components
      │  ├─ admin         # 管理端通用组件
      │  └─ product       # 商品展示组件
      ├─ data             # 前端 mock 数据
      ├─ layouts          # 页面布局，例如管理员后台布局
      ├─ router           # 路由配置
      ├─ services         # API 请求封装
      ├─ stores           # Pinia 状态管理
      └─ views
         ├─ admin         # 管理员后台页面
         └─ front         # 用户前台页面
```

## 启动方式

### 1. 初始化数据库

数据库连接配置：

```text
数据库：second_hand_trade
账号：root
密码：root
```

配置文件：

```text
backend/src/main/resources/application.yml
```

执行建表和初始化数据：

```bash
cd backend
.\scripts\init-database.ps1
```

该脚本会依次执行：

```text
backend/sql/01_create_tables.sql
backend/sql/02_seed_data.sql
```

说明：

- `users` 普通用户表不插入任何用户账号。
- `admin_users` 只保留管理员账号 `admin/admin123456`。
- 其余业务表插入约 10 条初始化数据。
- 脚本可重复执行。

### 2. 启动后端

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

后端默认地址：

```text
http://127.0.0.1:8080
```

健康检查：

```text
http://127.0.0.1:8080/api/health
```

### 3. 启动前端

```bash
cd fronted
npm install
npm run dev
```

前端默认地址：

```text
http://127.0.0.1:5173/
```

Vite 已配置代理：

```text
/api -> http://127.0.0.1:8080
```

## 登录入口

用户前台：

```text
http://127.0.0.1:5173/
```

管理员后台：

```text
http://127.0.0.1:5173/admin
```

管理员演示账号：

```text
账号：admin
密码：admin123456
```

## 已实现页面

用户前台：

- 首页
- 登录/注册页
- 商品发布页
- 商品详情页
- 个人中心
- IM 聊天页
- 求购广场
- 以物换物专区
- 毕业季专题页
- 订单管理页
- 搜索结果页
- 帮助页

管理员后台：

- 首页数据大盘
- 用户管理
- 商品管理
- 分类管理
- 订单与纠纷管理
- 举报审核管理
- 系统配置
- 公告管理

## 后端 MVC 说明

当前后端采用 Spring Boot MVC 三层结构：

- `controller`：接收 HTTP 请求，返回统一 `ApiResponse`
- `service`：业务逻辑层，当前 `TradeDataService` 通过 JDBC 读取 MySQL 数据
- `dto`：统一响应、分页响应等数据结构
- `config`：跨域等基础配置

当前后端已连接 MySQL，并通过 `TradeDataService` 使用 `JdbcTemplate` 读取数据库数据。后续如果继续完善业务，可以在现有结构上新增：

```text
entity
repository
service.impl
```

并逐步将 `JdbcTemplate` 查询替换为更完整的 Repository/ORM 实现。

## 前端结构说明

前端按职责目录组织：

- 页面统一放在 `views`
- 管理端页面放 `views/admin`
- 前台页面放 `views/front`
- 接口请求统一放 `services/api.js`
- 状态管理统一放 `stores`
- 商品组件放 `components/product`
- 后台组件放 `components/admin`
- mock 数据放 `data`

## 接口文档

接口文档见：

```text
API.md
```

前端统一 API 客户端：

```text
fronted/src/services/api.js
```

主要 API 模块：

- `authApi`
- `userApi`
- `fileApi`
- `categoryApi`
- `itemApi`
- `orderApi`
- `wantedApi`
- `swapApi`
- `chatApi`
- `adminApi`

## 数据库设计

数据库表设计和脚本见：

```text
DATABASE.md
backend/sql/01_create_tables.sql
backend/sql/02_seed_data.sql
backend/scripts/init-database.ps1
```

主要表：

- 用户与认证：`users`、`admin_users`、`user_privacy`
- 商品：`categories`、`category_tags`、`items`、`item_images`、`favorites`、`item_comments`
- 交易：`orders`、`order_status_logs`、`reviews`
- 沟通：`chats`、`chat_messages`
- 风控治理：`reports`、`disputes`、`sensitive_words`、`audit_logs`
- 运营配置：`announcements`、`notifications`、`system_settings`
- 专区功能：`wanted_posts`、`swap_requests`

## 数据同步说明

为了便于页面原型、后端接口和数据库联调，当前三处数据保持一致：

- 数据库初始化脚本：`backend/sql/02_seed_data.sql`
- 用户端演示数据：`fronted/src/data/mock.js`
- 管理端演示数据：`fronted/src/data/adminMock.js`

同步范围：

- 普通用户账号：`users = 0`，不插入真实用户账号
- 管理员账号：`admin_users = 1`，保留 `admin/admin123456`
- 分类：6 个一级分类、18 个子标签
- 商品、订单、求购、置换申请、举报、纠纷、公告、敏感词：各 10 条左右
- 后台商品、订单、分类、置换演示数据从用户端 mock 派生，避免两份前端数据不一致

## 验证命令

后端：

```bash
cd backend
.\mvnw.cmd test
```

前端：

```bash
cd fronted
npm run build
```

当前验证结果：

- 后端测试通过
- 前端构建通过
- 数据库初始化脚本已执行成功
- `users` 表为 0 条，符合“不写用户账号”要求
- `admin_users` 表为 1 条，账号为 `admin`
- `swap_requests` 表为 10 条，与前端 `swapRequests` 保持一致
- Vite 构建存在依赖包 pure annotation 和 chunk size warning，不影响运行

## 下一步建议

1. 后端新增登录鉴权、JWT、权限拦截。
2. 将 `TradeDataService` 中的 SQL 查询拆分到 Repository。
3. 前端页面逐步从 mock 数据切换到 `services/api.js`。
4. 添加真实注册、登录、发布商品、订单流转事务。
5. 补充后台操作审计和更细粒度权限。
