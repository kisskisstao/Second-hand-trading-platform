# 校园二手交易平台

一个校园二手交易平台项目，包含 Vue 3 前端、Spring Boot 后端和 MySQL 初始化脚本。当前版本保留商品交易、求购、订单、聊天、评价、通知和后台管理功能；以物换物模块已从前后端功能入口中删除。

## 当前状态

- 默认管理员账号：`admin / admin123456`。
- 普通用户、商品、订单、聊天、收藏、评论、求购、举报、纠纷、通知、公告等业务数据默认清空，后续由真实操作写入数据库。
- 商品列表、商品发布、草稿保存、图片上传、收藏、留言、订单、聊天、求购、评价和个人中心均已接后端接口。
- 管理端已接真实接口：用户启用/禁用、商品新增/上下架/删除、分类增删改、订单/纠纷查看、纠纷处理、举报审核、系统设置、敏感词、公告管理、数据看板。
- 支付宝和微信支付配置入口已保留，默认关闭真实支付，开发环境不会实际收款。
- WebSocket 用于通知和消息订阅，开发环境允许 `localhost` / `127.0.0.1` 任意本地端口。
- 以物换物已删除：前端无 `/swap` 路由、无 `swapApi`，后端无 `/api/exchanges/**` 和 `/api/swap-requests/**` 接口。数据库历史表可保留但当前代码不使用。

## 技术栈

- 前端：Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios、ECharts
- 后端：Java 17、Spring Boot、Spring MVC、JdbcTemplate、MyBatis-Plus、WebSocket/STOMP
- 数据库：MySQL 8

## 目录结构

```text
backend/                 Spring Boot 后端
backend/sql/             建表和初始化数据
backend/scripts/         数据库初始化脚本
fronted/                 Vue 前端
fronted/src/services/    前端 API 和 WebSocket 服务
fronted/src/views/       前台和后台页面
API.md                   旧接口说明，部分内容可能滞后
DATABASE.md              旧数据库说明，部分内容可能滞后
WORK.md                  旧工作记录，部分内容可能滞后
```

## 本地运行

以下命令按 Windows PowerShell 编写。

### 1. 环境要求

| 工具 | 建议版本 | 用途 |
| --- | --- | --- |
| JDK | 17 | 运行 Spring Boot 后端 |
| MySQL | 8.x | 存储业务数据 |
| Node.js | `^20.19.0` 或 `>=22.12.0` | 运行 Vue/Vite 前端 |
| npm | 随 Node 安装 | 安装前端依赖 |

默认数据库配置在 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/second_hand_trade
    username: root
    password: 123456
```

图片默认保存到后端工作目录下的 `uploads/images`。

### 2. 初始化数据库

先启动 MySQL，并确认 `mysql` 命令已加入 `PATH`。

```powershell
cd backend
.\scripts\init-database.ps1
cd ..
```

如果 MySQL 密码不是默认值：

```powershell
cd backend
.\scripts\init-database.ps1 -MysqlUser root -MysqlPassword 你的密码
cd ..
```

初始化脚本会执行：

```text
backend/sql/01_create_tables.sql
backend/sql/02_seed_data.sql
```

说明：

- `01_create_tables.sql` 创建 `second_hand_trade` 数据库和所有表。
- `02_seed_data.sql` 清空业务数据，只保留管理员账号、分类、敏感词和系统设置。
- `03_add_purchases_exchanges.sql` 是历史增量脚本；当前求购表仍可用，置换相关表不再被代码使用。

### 3. 启动后端

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

默认地址：

```text
http://127.0.0.1:8080
```

健康检查：

```text
GET http://127.0.0.1:8080/api/health
```

### 4. 启动前端

```powershell
cd fronted
npm install
npm run dev -- --host 127.0.0.1
```

默认 Vite 端口通常是 `5173`。如果端口被占用，Vite 会自动切到 `5174`、`5175` 等端口。后端 CORS 和 WebSocket 已允许本机任意端口。

## 主要页面

- `/`：首页，展示真实商品推荐和搜索入口。
- `/items`、`/search`：商品列表和筛选。
- `/items/:itemId`：商品详情、收藏、留言、联系卖家、创建订单。
- `/items/publish`：发布商品、上传图片、保存草稿。
- `/wanted`：求购广场，支持发布求购和查看求购列表。
- `/season`：毕业季/急售专题，复用商品列表数据。
- `/orders`：用户订单，支持接单、取消、完成、支付入口。
- `/chats`：真实会话和消息列表。
- `/profile`：个人中心，包含我的发布、我的订单、收藏、通知、评价/评论、我的求购和隐私设置。
- `/login?tab=admin`：管理员登录。
- `/admin`：后台数据看板。
- `/admin/users`：用户管理。
- `/admin/items`：商品管理。
- `/admin/categories`：分类管理。
- `/admin/orders`：订单和纠纷管理。
- `/admin/reports`：举报审核。
- `/admin/settings`：系统设置和敏感词。
- `/admin/notices`：公告管理。

## API 概览

前端统一通过 `fronted/src/services/api.js` 请求 `/api`，Vite 开发代理转发到 `http://127.0.0.1:8080`。

公开接口：

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/admin/login`
- `GET /api/health`
- `GET /api/categories`
- `GET /api/items`
- `GET /api/items/{itemId}`
- `GET /api/items/{itemId}/comments`
- `GET /api/files/images/{storageKey}`
- `GET /api/purchases`
- `GET /api/purchases/{purchaseId}/matches`
- `GET /api/wanted-posts`

普通用户接口需要 `USER` JWT：

- `GET/PUT /api/users/me`
- `GET /api/users/me/items`
- `GET /api/users/me/favorites`
- `GET /api/users/me/notifications`
- `GET /api/users/me/comments`
- `POST/PUT/PATCH/DELETE /api/items/**` 中的写操作
- `POST/PATCH /api/purchases/**`
- `POST/GET /api/orders/**`
- `POST/GET /api/chats/**`
- `POST/GET /api/reviews/**`

管理员接口需要 `ADMIN` JWT：

- `/api/admin/dashboard`
- `/api/admin/users/**`
- `/api/admin/items/**`
- `/api/admin/categories/**`
- `/api/admin/orders`
- `/api/admin/disputes/**`
- `/api/admin/reports/**`
- `/api/admin/settings`
- `/api/admin/notices/**`

已删除接口：

- `/api/exchanges/**`
- `/api/swap-requests/**`

## 后端服务划分

- `AuthService`：注册、普通用户登录、管理员登录。
- `JwtService`：JWT 签发、解析和校验。
- `UserService`：当前用户、我的发布、收藏、通知、评论、评价。
- `ItemService`：商品分类、商品列表、发布商品、图片关联、收藏、留言、上下架和删除。
- `FileStorageService`：图片上传、落盘、文件记录和公开读取。
- `TradeWorkflowService`：订单、聊天、交易状态流转、通知。
- `ReviewService`：订单评价和评分统计。
- `BazaarService`：求购发布、求购列表、关闭求购、求购匹配推荐。
- `AdminService`：后台看板、用户、分类、举报、纠纷、设置、公告。
- `HealthService`：健康检查。

## 验证命令

后端：

```powershell
cd backend
.\mvnw.cmd clean test
```

前端：

```powershell
cd fronted
npm run build
```

最近一次验证结果：

- 后端 `.\mvnw.cmd clean test` 通过。
- 前端 `npm run build` 通过。
- 前端构建仍有第三方依赖 pure annotation 和 chunk size warning，不影响运行。

## 注意事项

- `.idea/`、运行日志、`uploads/`、`dist/`、依赖目录和本地缓存不应提交。
- 不要提交 `.env`、数据库导出、真实支付密钥或本地账号密码。
- 如果浏览器出现 401/403，先确认是否使用了正确登录身份；后台接口必须使用管理员登录。必要时清理：

```js
localStorage.clear()
location.href = '/login?tab=admin'
```
