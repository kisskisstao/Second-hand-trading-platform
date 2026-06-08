# 校园二手交易平台

一个校园二手交易平台项目，包含 Vue 前端、Spring Boot 后端和 MySQL 初始化脚本。

## 当前状态

- 普通用户：默认 0 个，注册后才写入数据库。
- 管理员：默认 1 个，账号 `admin`，密码 `admin123456`。
- 商品数据：默认 0 条；初始化会清空演示商品，后续由注册用户发布，或由管理员后台代指定普通用户新增。
- 商品列表：从首页点击进入 `/items`，筛选放在列表页，点击“确认筛选”后刷新结果。
- 商品分页：列表页使用 Element Plus 分页组件，每页 10 条；后端 `/api/items` 支持 `page` 和 `pageSize`。
- 接口真实化：商品列表筛选、发布商品、保存草稿、图片上传、我的发布、收藏/取消收藏、留言、订单、聊天、求购、置换已接数据库。
- 商品管理：卖家可真实下架、重新上架、软删除自己的商品；管理员后台可新增指定卖家的商品，也可下架和删除商品。
- 商品草稿：保存草稿会写入 `items.status = 'DRAFT'`，只在个人中心“我的发布 > 草稿”展示，不进入公开详情和商品列表。
- 图片上传：发布页图片上传到后端 `uploads/images`，数据库记录 `/api/files/images/{storageKey}` 访问地址。
- 求购置换：新增 `purchases`、`exchanges` 表，支持发布、列表、匹配推荐；前端已补充发布置换、查看匹配、个人中心管理置换记录。
- 系统通知：管理员下架、上架或删除用户商品时，会给卖家写入系统通知；管理员发布平台公告时，会给目标用户生成系统信息；订单创建、接单、支付、取消、完成会同步通知交易另一方。
- 个人中心同步：我的订单、我的评价/评论、我的求购、以物换物均已接入真实接口；订单同时展示买家订单和卖家订单。
- 支付：已接入支付宝和微信支付的配置入口与下单流程；默认关闭真实支付，避免开发环境误收款。
- 认证：登录已使用 JWT，受保护接口通过统一认证拦截器校验；新注册密码使用带盐 PBKDF2-HMAC-SHA256 哈希存储，并兼容旧 `sha256$` 哈希登录。
- 异常：后端统一返回 `ApiResponse` 错误结构。
- 后台数据大盘：统计卡片、交易额走势、分类占比、校区分布和待处理项均从数据库实时聚合。
- 清空数据：普通用户、商品、商品图片、收藏、留言、订单、聊天、求购、置换、举报、纠纷、通知、公告等演示业务数据默认清空；后续随真实操作写入。
- 服务状态：本次没有启动前后端常驻服务。

## 技术栈

- 前端：Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios、ECharts
- 后端：Java 17、Spring Boot、Spring MVC、JdbcTemplate、MyBatis-Plus
- 数据库：MySQL 8

## 目录结构

```text
backend/                 Spring Boot 后端
backend/sql/             建表和初始化数据
backend/sql/03_add_purchases_exchanges.sql  求购和置换新表增量脚本
backend/scripts/         数据库初始化脚本
fronted/                 Vue 前端
fronted/src/data/        前端展示数据
API.md                   接口说明
DATABASE.md              数据库设计说明
WORK.md                  工作记录与后续建议
```

## 本地运行步骤

以下步骤按 Windows PowerShell 编写。项目目录名里有空格，建议每个命令都在对应目录执行，不要把长路径手动拼进命令里。

### 1. 准备环境

需要安装并能在终端访问：

| 工具 | 建议版本 | 用途 |
| --- | --- | --- |
| JDK | 17 | 运行 Spring Boot 后端 |
| MySQL | 8.x | 存储用户、商品、订单、聊天等数据 |
| Node.js | `^20.19.0` 或 `>=22.12.0` | 运行 Vue/Vite 前端 |
| npm | 随 Node 安装 | 安装前端依赖 |
| Maven | 可选 | 如果 Maven Wrapper 不可用，可用本机 Maven |

默认数据库连接配置在 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/second_hand_trade
    username: root
    password: 123456
```

商品图片默认保存目录也在同一个文件中配置：

```yaml
app:
  upload:
    dir: uploads
```

如果你的 MySQL 用户名或密码不是 `root/123456`，需要同步修改两个地方：

- `backend/src/main/resources/application.yml`
- 执行 `backend/scripts/init-database.ps1` 时传入 `-MysqlUser` 和 `-MysqlPassword`

### 2. 初始化数据库

先启动本机 MySQL 服务，并确认 `mysql` 命令已加入 `PATH`。然后在项目根目录执行：

```powershell
cd backend
.\scripts\init-database.ps1
cd ..
```

如果你的数据库密码不是默认的 `123456`，可以这样执行：

```powershell
cd backend
.\scripts\init-database.ps1 -MysqlUser root -MysqlPassword 你的密码
cd ..
```

脚本会依次执行：

```text
backend/sql/01_create_tables.sql
backend/sql/02_seed_data.sql
```

说明：

- `01_create_tables.sql` 会创建 `second_hand_trade` 数据库和所有表。
- `02_seed_data.sql` 会清空普通用户、商品、订单、聊天等业务数据，只保留管理员账号、分类、敏感词和系统配置。
- 初始化后商品默认是 0 条，后续由普通用户发布，或管理员后台为指定普通用户新增。

初始化后可以检查数据量：

```powershell
mysql -uroot -p123456 --default-character-set=utf8mb4 -D second_hand_trade -e "SELECT (SELECT COUNT(*) FROM users) AS users_count, (SELECT COUNT(*) FROM admin_users) AS admins_count, (SELECT COUNT(*) FROM categories) AS categories_count, (SELECT COUNT(*) FROM items) AS items_count, (SELECT COUNT(*) FROM orders) AS orders_count, (SELECT COUNT(*) FROM chats) AS chats_count;"
```

预期结果：

```text
users_count=0
admins_count=1
categories_count=6
items_count=0
orders_count=0
chats_count=0
```

如果只想手动执行 SQL，也可以用：

```powershell
mysql -uroot -p123456 --default-character-set=utf8mb4 --binary-mode < backend/sql/01_create_tables.sql
mysql -uroot -p123456 --default-character-set=utf8mb4 --binary-mode < backend/sql/02_seed_data.sql
```

### 3. 启动后端

打开一个新的 PowerShell 窗口，在项目根目录执行：

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

如果 Maven Wrapper 在你的环境不可用，可以安装 Maven 后执行：

```powershell
cd backend
mvn spring-boot:run
```

后端启动成功后地址为：

```text
http://127.0.0.1:8080
```

可以访问健康检查：

```powershell
curl.exe http://127.0.0.1:8080/api/health
```

### 4. 启动前端

再打开一个新的 PowerShell 窗口，在项目根目录执行：

```powershell
cd fronted
npm install
npm run dev
```

前端启动成功后访问：

```text
http://127.0.0.1:5173
```

前端 Vite 已配置代理：

```text
/api -> http://127.0.0.1:8080
```

所以本地开发时前端会自动把接口请求转发到后端。

### 5. 登录和录入数据

普通用户：

- 数据库默认没有普通用户。
- 先打开 `http://127.0.0.1:5173/register` 注册。
- 再使用注册的学号或邮箱和密码登录。
- 未登录时不保留也不显示“小张同学”等固定实例用户。
- 登录后个人中心展示后端返回的真实用户信息。
- 登录后可发布商品、保存草稿、上传商品图片、收藏、留言、咨询、创建订单。

管理员：

```text
账号：admin
密码：admin123456
入口：http://127.0.0.1:5173/admin
```

管理员后台可以：

- 查看数据库实时统计的数据大盘。
- 查看用户、商品、订单等真实表数据。
- 为指定普通用户新增商品。
- 下架、重新上架或删除商品。
- 新增、编辑、发布和删除平台公告，公告数据来自 `announcements` 表。
- 商品处理和公告发布会同步写入用户 `notifications` 系统通知。
- 管理员登录后，前台顶部“个人中心”旁会显示“数据后台”入口；普通用户不显示该入口。

建议测试流程：

1. 注册普通用户 A，并登录发布一个商品。
2. 注册普通用户 B，打开 A 的商品详情，点击“立即咨询”。
3. A 和 B 会进入按 `items.seller_id` 创建的真实聊天会话。
4. 使用管理员账号进入后台，查看数据大盘和商品管理是否同步变化。

### 6. 常用验证命令

后端测试：

```powershell
cd backend
.\mvnw.cmd test
```

如果 Maven Wrapper 不可用：

```powershell
cd backend
mvn test
```

前端构建：

```powershell
cd fronted
npm run build
```

数据库快速检查：

```powershell
mysql -uroot -p123456 --default-character-set=utf8mb4 -D second_hand_trade -e "SELECT COUNT(*) AS users FROM users; SELECT COUNT(*) AS items FROM items; SELECT COUNT(*) AS orders FROM orders; SELECT COUNT(*) AS chats FROM chats;"
```

### 7. 常见问题

- `mysql command was not found`：MySQL 未加入 `PATH`，把 MySQL `bin` 目录加入环境变量，或使用完整路径执行 `mysql.exe`。
- `Access denied for user 'root'`：数据库密码不是 `123456`，修改 `application.yml`，初始化时用 `-MysqlPassword` 传入真实密码。
- 后端启动失败并提示无法连接数据库：确认 MySQL 已启动，且 `second_hand_trade` 已初始化。
- 前端页面请求失败：确认后端已在 `8080` 端口启动，前端开发服务器已在 `5173` 端口启动。
- 前端构建出现 third-party pure annotation 或 chunk size warning：这是 Vite/Rolldown 对依赖和大包的警告，不影响本地运行。

## JWT 与权限

登录成功后后端返回 `accessToken`，前端保存到 `localStorage`，后续请求通过请求头携带：

```http
Authorization: Bearer <jwt>
```

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
- `GET /api/exchanges`
- `GET /api/exchanges/{exchangeId}/matches`

普通用户接口需要 `USER` JWT，后台接口需要 `ADMIN` JWT。未登录返回 401，权限不匹配返回 403。

WebSocket 连接同样需要在 STOMP `CONNECT` 帧中携带：

```http
Authorization: Bearer <jwt>
```

当前 WebSocket 只允许普通用户 token 连接，客户端仅可订阅 `/user/queue/notifications`、`/user/queue/messages` 和 `/topic/broadcast`；客户端直接向 `/app/...` 发送消息已禁用，聊天、通知等写操作统一走受保护的 REST 接口和后端服务推送。

## 主要页面

- 首页：从真实商品接口展示推荐商品和搜索入口；数据库为空时不再展示演示商品。
- 商品列表：路径 `/items`，筛选在列表页，点击“确认筛选”后应用条件；每页展示 10 条商品。
- 商品详情：只展示上架商品；同校区相关推荐只读取真实数据库商品并排除当前商品，有 1 个展示 1 个、有 2 个展示 2 个、最多展示 3 个，空数据时不展示固定模板；收藏、留言、立即咨询、预约商品调用真实接口；商品评论会同步到个人中心“我的评价/评论”。
- 登录/注册：注册写入数据库，登录读取真实用户或管理员信息。
- 个人中心：只展示当前登录用户信息；我的发布按上架、下架、已出、草稿分栏从数据库查询，本人发布的商品可删除；系统通知读取 `notifications` 表；我的订单、我的评价/评论、我的求购和以物换物均同步真实数据并支持对应操作。
- 订单页：查询真实订单，买家和卖家的订单都会返回；支持接单、取消、完成和创建支付单。
- 聊天页：查询真实会话和消息，发送消息写入数据库；买家咨询商品时按该商品 `seller_id` 精确进入卖家会话。
- 求购/置换广场：求购列表、发布求购、置换列表从真实接口读取；支持选择自己的在售商品发布置换、查看置换匹配推荐；匹配推荐由后端按分类、校区、预算和关键词打分。
- 管理后台：商品、分类、公告、数据大盘统计同步当前数据库；管理员登录后前台顶部显示“数据后台”入口，可为指定普通用户新增商品，商品下架/删除真实写库并通知卖家，公告新增/删除真实写库，公告发布生成系统通知，订单会随真实交易同步展示。

## 后端服务拆分

后端已经从旧的单一 `TradeDataService` 拆分为：

- `AuthService`：注册、普通用户登录、管理员登录。
- `JwtService`：JWT 签发、解析和校验。
- `UserService`：当前用户、我的发布、我的收藏、系统通知、用户评价、个人商品评论汇总。
- `ItemService`：分类、商品筛选分页、用户发布、后台代指定卖家新增商品、收藏、留言、商品图片关联。
- `FileStorageService`：图片上传落盘、`files` 表记录和公开图片读取。
- `TradeWorkflowService`：订单、聊天等交易流程；订单和聊天已真实写库。
- `BazaarService`：求购、置换和匹配推荐。
- `AdminService`：后台看板、举报、纠纷、设置、公告。
- `HealthService`：健康检查。

## 支付说明

支付配置位于 `backend/src/main/resources/application.yml`：

```yaml
app:
  payment:
    alipay:
      enabled: false
    wechat:
      enabled: false
```

默认 `enabled: false`，所以不会真的向支付宝或微信发起下单，也不会产生真实收款二维码。配置真实商户参数后：

- 支付宝会生成 `alipay.trade.page.pay` 跳转 URL。
- 微信会调用 API v3 Native 下单并返回 `code_url` 二维码链接。
- 数据库只记录订单、支付单、支付流水号和状态，真正的钱由支付宝或微信支付结算到配置的商户账户。
- 如果使用平台商户号，钱先进入平台商户结算账户，再由平台分账或线下结算给卖家。
- 如果要让钱直接进卖家账户，需要每个卖家绑定自己的商户或收款身份，并补直连或分账流程。

## 最近验证结果

- 后端 `.\mvnw.cmd clean test` 通过。
- 前端 `npm run build` 通过。
- 前端构建仍有第三方依赖 pure annotation 和 chunk size warning，不影响运行。

## 新增功能（2026-06-08）

### 1. 用户评价体系

订单完成后，买家可对卖家进行评价，支持 1-5 星评分和文字评价。

- **后端实现**：`ReviewEntity`、`ReviewMapper`、`ReviewService`、`ReviewController`
- **前端组件**：`OrderReview.vue`（订单评价）、`ReviewList.vue`（评价展示）
- **API 接口**：
  - `POST /api/reviews` - 创建评价
  - `POST /api/orders/{orderId}/reviews` - 按订单创建评价
  - `GET /api/users/me/comments` - 获取当前用户相关的商品评论，包括自己发表的评论和别人评论自己商品的留言
  - `GET /api/reviews/user/{userId}` - 获取用户评价列表
  - `GET /api/reviews/user/{userId}/stats` - 获取用户评分统计（平均分、评价数）
  - `GET /api/users/{userId}/reviews` - 获取用户评价列表分页包装
- **业务规则**：
  - 只有已完成订单的买家才能评价该订单卖家
  - 评价目标用户由后端根据订单 `seller_id` 推导，前端不能指定任意目标用户
  - 每个订单每个买家只能评价一次
  - 评分必须为 1-5 星，评价内容最长 500 字
  - 评价后自动触发卖家信用积分变更

### 2. 信用积分系统

用户初始信用分为 100 分，根据交易评价动态调整。

- **实现位置**：`UserService` 中的 `updateCreditScore()` 和 `getUserCreditScore()`
- **积分规则**：
  - 4-5 星好评：+5 分
  - 3 星中评：0 分
  - 1-2 星差评：-10 分
- **积分范围**：0 - 200 分，使用 `GREATEST(0, LEAST(credit_score + ?, 200))` 限制
- **应用场景**：评价完成后自动更新，可用于后续交易权限控制

### 3. 实时消息推送

基于 WebSocket + STOMP 协议实现实时消息推送，支持订单通知、聊天消息、系统通知。

- **后端实现**：
  - `WebSocketConfig` - WebSocket 配置，启用 STOMP 协议、JWT 连接校验和订阅权限限制
  - `MessageService` - 消息推送服务，支持订单/聊天/系统通知
  - `WebSocketController` - 保留为控制器占位，当前不开放客户端入站写消息
  - `NotificationEntity`、`NotificationMapper` - 通知数据持久化
- **前端实现**：
  - `NotificationPanel.vue` - 系统通知面板组件
  - `websocket.js` - WebSocket 服务封装，连接时携带 JWT，并订阅 `/user/queue/notifications`、`/user/queue/messages` 和 `/topic/broadcast`
- **推送类型**：
  - **订单通知**：订单状态变更时推送给买卖双方
  - **聊天消息**：新消息实时推送给会话参与人
  - **系统通知**：管理员操作、公告发布等推送给目标用户
  - **广播消息**：全平台公告推送给所有在线用户
- **安全规则**：
  - STOMP `CONNECT` 必须携带 `Authorization: Bearer <jwt>`
  - 只允许订阅当前用户队列和广播主题
  - 客户端 WebSocket `SEND` 已禁用，避免绕过 REST 鉴权直接写入通知或聊天
- **技术栈**：Spring Boot WebSocket + SockJS + STOMP

### 4. 订单与个人中心同步

修复账号 B 预约或购买账号 A 商品后，订单没有同步到个人中心的问题。

- **后端实现**：`TradeWorkflowService` 在创建订单、卖家接单、买家发起支付、取消订单、完成订单时写入订单通知并推送给交易另一方。
- **前端实现**：`ProfileCenterView.vue` 的“我的订单”接入 `/api/orders`，同时展示当前用户作为买家和卖家的订单。
- **可操作内容**：
  - 卖家可在个人中心接单。
  - 买家可在个人中心发起支付。
  - 买卖双方可取消或完成订单。
  - 买家可在订单完成后提交评价。
- **同步效果**：
  - 账号 B 创建订单后，账号 B 的个人中心会显示该订单。
  - 账号 A 的个人中心也会显示该订单，并收到系统通知。

### 5. 商品评论与订单评价同步

补全个人中心的评价/评论展示，解决评论后个人中心没有更新的问题。

- **新增接口**：`GET /api/users/me/comments`
- **后端实现**：`UserService.comments()` 汇总当前用户相关的商品评论。
- **前端实现**：`ProfileCenterView.vue` 的“我的评价/评论”展示两类数据：
  - 订单评价：别人对当前用户作为卖家的交易评价。
  - 商品评论：当前用户发表的商品评论，以及别人评论当前用户商品的留言。
- **跳转能力**：商品评论可直接跳转回对应商品详情页。

### 6. 以物换物补全

补全以物换物的前端发布和个人中心管理流程。

- **广场页**：
  - `/swap` 支持点击“发布置换”。
  - 可选择自己的在售商品作为置换商品。
  - 可填写想换物品、校区和说明。
  - 可查看后端返回的匹配推荐。
- **个人中心**：
  - “以物换物”展示当前用户发布的置换记录。
  - 支持标记匹配和取消置换。
- **后端复用**：继续使用已有 `BazaarService`、`/api/exchanges`、`/api/exchanges/{exchangeId}/matches`、`/api/exchanges/{exchangeId}/matched`、`/api/exchanges/{exchangeId}/cancel`。

### 新增依赖

**后端**（已添加到 `pom.xml`）：
- `spring-boot-starter-websocket` - WebSocket 支持

**前端**（已添加到 `package.json`，首次运行仍需执行 `npm install`）：
- `sockjs-client@1.6.1` - WebSocket 客户端
- `stompjs@2.3.3` - STOMP 协议客户端
