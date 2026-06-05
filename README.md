# 校园二手交易平台

一个校园二手交易平台项目，包含 Vue 前端、Spring Boot 后端和 MySQL 初始化脚本。

## 当前状态

- 普通用户：默认 0 个，注册后才写入数据库。
- 管理员：默认 1 个，账号 `admin`，密码 `admin123456`。
- 商品数据：默认 0 条；初始化会清空演示商品，后续由注册用户发布，或由管理员后台代指定普通用户新增。
- 商品列表：从首页点击进入 `/items`，筛选放在列表页，点击“确认筛选”后刷新结果。
- 商品分页：列表页使用 Element Plus 分页组件，每页 10 条；后端 `/api/items` 支持 `page` 和 `pageSize`。
- 接口真实化：商品列表筛选、发布商品、我的发布、收藏/取消收藏、留言、订单、聊天、求购、置换已接数据库。
- 商品管理：卖家可真实下架、重新上架、软删除自己的商品；管理员后台可新增指定卖家的商品，也可下架和删除商品。
- 求购置换：新增 `purchases`、`exchanges` 表，支持发布、列表和匹配推荐。
- 支付：已接入支付宝和微信支付的配置入口与下单流程；默认关闭真实支付，避免开发环境误收款。
- 认证：登录已使用 JWT，受保护接口通过统一认证拦截器校验。
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

## 初始化数据库

```powershell
cd backend
.\scripts\init-database.ps1
```

脚本会执行：

```text
backend/sql/01_create_tables.sql
backend/sql/02_seed_data.sql
```

初始化后建议检查：

```sql
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM admin_users;
SELECT COUNT(*) FROM items;
SELECT COUNT(*) FROM orders;
SELECT COUNT(*) FROM chats;
SELECT COUNT(*) FROM payments;
SELECT COUNT(*) FROM purchases;
SELECT COUNT(*) FROM exchanges;
```

预期结果：普通用户 0，管理员 1，商品 0，订单 0，聊天 0，支付单 0，求购 0，置换 0。

如果已有数据库不想重建，只创建本轮新增表：

```powershell
mysql -uroot -proot --default-character-set=utf8mb4 --binary-mode < backend/sql/03_add_purchases_exchanges.sql
```

本轮已执行该增量脚本，当前 `purchases` 和 `exchanges` 表均已创建，数据量为 0。

## 启动项目

启动后端：

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

后端地址：

```text
http://127.0.0.1:8080
```

启动前端：

```powershell
cd fronted
npm install
npm run dev
```

前端地址：

```text
http://127.0.0.1:5173
```

## 登录说明

普通用户：

- 数据库默认没有普通用户。
- 先在前端注册，再使用注册的学号或邮箱和密码登录。
- 未登录时不保留也不显示“小张同学”等固定实例用户。
- 登录后个人中心展示后端返回的真实用户信息。

管理员：

```text
账号：admin
密码：admin123456
入口：http://127.0.0.1:5173/admin
```

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
- `GET /api/purchases`
- `GET /api/purchases/{purchaseId}/matches`
- `GET /api/exchanges`
- `GET /api/exchanges/{exchangeId}/matches`

普通用户接口需要 `USER` JWT，后台接口需要 `ADMIN` JWT。未登录返回 401，权限不匹配返回 403。

## 主要页面

- 首页：从真实商品接口展示推荐商品和搜索入口；数据库为空时不再展示演示商品。
- 商品列表：路径 `/items`，筛选在列表页，点击“确认筛选”后应用条件；每页展示 10 条商品。
- 商品详情：展示商品信息；收藏、留言、立即咨询、预约商品调用真实接口。
- 登录/注册：注册写入数据库，登录读取真实用户或管理员信息。
- 个人中心：只展示当前登录用户信息；我的发布、收藏、订单按当前用户从数据库查询。
- 订单页：查询真实订单，支持接单、取消、完成和创建支付单。
- 聊天页：查询真实会话和消息，发送消息写入数据库；买家咨询商品时按该商品 `seller_id` 精确进入卖家会话。
- 求购/置换广场：求购列表、发布求购、置换列表从真实接口读取；匹配推荐由后端按分类、校区、预算和关键词打分。
- 管理后台：商品、分类、数据大盘统计同步当前数据库；管理员可为指定普通用户新增商品，商品下架/删除真实写库，订单会随真实交易同步展示。

## 后端服务拆分

后端已经从旧的单一 `TradeDataService` 拆分为：

- `AuthService`：注册、普通用户登录、管理员登录。
- `JwtService`：JWT 签发、解析和校验。
- `UserService`：当前用户、我的发布、我的收藏、用户评价。
- `ItemService`：分类、商品筛选分页、用户发布、后台代指定卖家新增商品、收藏、留言、图片 URL。
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

## 验证命令

后端测试：

```powershell
cd backend
.\mvnw.cmd test
```

前端构建：

```powershell
cd fronted
npm run build
```

本次验证结果：

- 后端 `mvn test` 通过。
- 前端 `npm run build` 通过。
- 前端构建仍有第三方依赖 pure annotation 和 chunk size warning，不影响运行。
