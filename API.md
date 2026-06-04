# 校园二手交易平台接口文档

版本：v0.1  
基础路径：`/api`  
数据格式：`application/json; charset=utf-8`

## 1. 通用约定

### 1.1 认证方式

除注册、登录、公开商品列表、商品详情外，其余接口默认需要登录。

请求头：

```http
Authorization: Bearer <accessToken>
```

### 1.2 通用响应结构

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

失败响应：

```json
{
  "code": 40001,
  "message": "参数错误",
  "data": null
}
```

分页响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "page": 1,
    "pageSize": 10,
    "total": 0
  }
}
```

### 1.3 通用错误码

| code | 含义 |
| --- | --- |
| 0 | 成功 |
| 40001 | 参数错误 |
| 40100 | 未登录或登录已过期 |
| 40300 | 无权限 |
| 40400 | 资源不存在 |
| 40900 | 状态冲突，例如商品已售出 |
| 50000 | 服务器错误 |

### 1.4 枚举

商品状态 `itemStatus`：

| 值 | 含义 |
| --- | --- |
| `ON_SALE` | 在售 |
| `RESERVED` | 已预约 |
| `SOLD` | 已售出 |
| `REMOVED` | 已下架 |

商品成色 `condition`：

| 值 | 含义 |
| --- | --- |
| `NEW` | 全新 |
| `LIKE_NEW` | 几乎全新 |
| `GOOD` | 轻微使用痕迹 |
| `FAIR` | 明显使用痕迹 |

订单状态 `orderStatus`：

| 值 | 含义 |
| --- | --- |
| `PENDING` | 待卖家确认 |
| `ACCEPTED` | 卖家已确认 |
| `CANCELED` | 已取消 |
| `COMPLETED` | 已完成 |

## 2. 用户与认证

### 2.1 用户注册

`POST /api/auth/register`

请求体：

```json
{
  "studentNo": "20240001",
  "nickname": "张三",
  "phone": "13800000000",
  "password": "123456"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "studentNo": "20240001",
    "nickname": "张三"
  }
}
```

### 2.2 用户登录

`POST /api/auth/login`

请求体：

```json
{
  "studentNo": "20240001",
  "password": "123456"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "accessToken": "jwt-token",
    "user": {
      "userId": 1,
      "studentNo": "20240001",
      "nickname": "张三",
      "avatarUrl": ""
    }
  }
}
```

### 2.3 获取当前用户信息

`GET /api/users/me`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "studentNo": "20240001",
    "nickname": "张三",
    "phone": "13800000000",
    "avatarUrl": "",
    "campus": "主校区",
    "createdAt": "2026-06-04T16:00:00"
  }
}
```

### 2.4 修改当前用户信息

`PUT /api/users/me`

请求体：

```json
{
  "nickname": "张三",
  "phone": "13800000000",
  "avatarUrl": "https://example.com/avatar.png",
  "campus": "主校区"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

## 3. 文件上传

### 3.1 上传图片

`POST /api/files/images`

请求类型：`multipart/form-data`

字段：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `file` | file | 是 | 图片文件 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "url": "https://example.com/uploads/item-1.png"
  }
}
```

## 4. 商品分类

### 4.1 获取分类列表

`GET /api/categories`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "categoryId": 1,
      "name": "教材书籍"
    },
    {
      "categoryId": 2,
      "name": "数码设备"
    }
  ]
}
```

## 5. 商品

### 5.1 发布商品

`POST /api/items`

请求体：

```json
{
  "title": "高等数学教材",
  "description": "教材九成新，少量笔记",
  "categoryId": 1,
  "price": 25.00,
  "originalPrice": 49.00,
  "condition": "GOOD",
  "campus": "主校区",
  "tradePlace": "图书馆门口",
  "imageUrls": [
    "https://example.com/uploads/item-1.png"
  ]
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "itemId": 1001
  }
}
```

### 5.2 获取商品列表

`GET /api/items`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 关键词，匹配标题和描述 |
| `categoryId` | number | 否 | 分类 ID |
| `minPrice` | number | 否 | 最低价 |
| `maxPrice` | number | 否 | 最高价 |
| `campus` | string | 否 | 校区 |
| `itemStatus` | string | 否 | 商品状态，默认只查 `ON_SALE` |
| `sort` | string | 否 | `latest` 最新，`price_asc` 价格升序，`price_desc` 价格降序 |
| `page` | number | 否 | 页码，默认 1 |
| `pageSize` | number | 否 | 每页条数，默认 10 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "itemId": 1001,
        "title": "高等数学教材",
        "price": 25.00,
        "coverUrl": "https://example.com/uploads/item-1.png",
        "condition": "GOOD",
        "itemStatus": "ON_SALE",
        "campus": "主校区",
        "seller": {
          "userId": 1,
          "nickname": "张三",
          "avatarUrl": ""
        },
        "createdAt": "2026-06-04T16:00:00"
      }
    ],
    "page": 1,
    "pageSize": 10,
    "total": 1
  }
}
```

### 5.3 获取商品详情

`GET /api/items/{itemId}`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "itemId": 1001,
    "title": "高等数学教材",
    "description": "教材九成新，少量笔记",
    "category": {
      "categoryId": 1,
      "name": "教材书籍"
    },
    "price": 25.00,
    "originalPrice": 49.00,
    "condition": "GOOD",
    "itemStatus": "ON_SALE",
    "campus": "主校区",
    "tradePlace": "图书馆门口",
    "imageUrls": [
      "https://example.com/uploads/item-1.png"
    ],
    "seller": {
      "userId": 1,
      "nickname": "张三",
      "avatarUrl": "",
      "campus": "主校区"
    },
    "favoriteCount": 3,
    "viewCount": 28,
    "createdAt": "2026-06-04T16:00:00",
    "updatedAt": "2026-06-04T16:10:00"
  }
}
```

### 5.4 修改商品

`PUT /api/items/{itemId}`

说明：仅商品发布者可修改，已售出商品不可修改。

请求体：

```json
{
  "title": "高等数学教材",
  "description": "教材九成新，少量笔记",
  "categoryId": 1,
  "price": 20.00,
  "originalPrice": 49.00,
  "condition": "GOOD",
  "campus": "主校区",
  "tradePlace": "图书馆门口",
  "imageUrls": [
    "https://example.com/uploads/item-1.png"
  ]
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 5.5 下架商品

`PATCH /api/items/{itemId}/remove`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 5.6 获取我的发布

`GET /api/users/me/items`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `itemStatus` | string | 否 | 商品状态 |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：同商品列表。

## 6. 收藏

### 6.1 收藏商品

`POST /api/items/{itemId}/favorite`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 6.2 取消收藏

`DELETE /api/items/{itemId}/favorite`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 6.3 获取我的收藏

`GET /api/users/me/favorites`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：同商品列表。

## 7. 订单与交易

### 7.1 创建订单

`POST /api/orders`

说明：买家对商品发起购买请求。创建成功后商品状态可变为 `RESERVED`。

请求体：

```json
{
  "itemId": 1001,
  "message": "我想今晚 7 点在图书馆门口交易"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 5001,
    "orderStatus": "PENDING"
  }
}
```

### 7.2 获取我的订单

`GET /api/orders`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `role` | string | 否 | `buyer` 我买到的，`seller` 我卖出的 |
| `orderStatus` | string | 否 | 订单状态 |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "orderId": 5001,
        "orderStatus": "PENDING",
        "item": {
          "itemId": 1001,
          "title": "高等数学教材",
          "price": 25.00,
          "coverUrl": "https://example.com/uploads/item-1.png"
        },
        "buyer": {
          "userId": 2,
          "nickname": "李四"
        },
        "seller": {
          "userId": 1,
          "nickname": "张三"
        },
        "createdAt": "2026-06-04T16:30:00"
      }
    ],
    "page": 1,
    "pageSize": 10,
    "total": 1
  }
}
```

### 7.3 获取订单详情

`GET /api/orders/{orderId}`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "orderId": 5001,
    "orderStatus": "PENDING",
    "message": "我想今晚 7 点在图书馆门口交易",
    "item": {
      "itemId": 1001,
      "title": "高等数学教材",
      "price": 25.00,
      "coverUrl": "https://example.com/uploads/item-1.png",
      "tradePlace": "图书馆门口"
    },
    "buyer": {
      "userId": 2,
      "nickname": "李四",
      "phone": "13900000000"
    },
    "seller": {
      "userId": 1,
      "nickname": "张三",
      "phone": "13800000000"
    },
    "createdAt": "2026-06-04T16:30:00",
    "updatedAt": "2026-06-04T16:30:00"
  }
}
```

### 7.4 卖家确认订单

`PATCH /api/orders/{orderId}/accept`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 7.5 取消订单

`PATCH /api/orders/{orderId}/cancel`

请求体：

```json
{
  "reason": "时间不合适"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 7.6 完成订单

`PATCH /api/orders/{orderId}/complete`

说明：买家或卖家确认线下交易已完成后调用。

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

## 8. 留言与沟通

### 8.1 商品留言列表

`GET /api/items/{itemId}/comments`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "commentId": 9001,
      "content": "可以便宜一点吗？",
      "user": {
        "userId": 2,
        "nickname": "李四",
        "avatarUrl": ""
      },
      "createdAt": "2026-06-04T16:40:00"
    }
  ]
}
```

### 8.2 发表商品留言

`POST /api/items/{itemId}/comments`

请求体：

```json
{
  "content": "可以便宜一点吗？"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "commentId": 9001
  }
}
```

### 8.3 获取会话列表

`GET /api/chats`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "chatId": 3001,
      "targetUser": {
        "userId": 2,
        "nickname": "李四",
        "avatarUrl": ""
      },
      "item": {
        "itemId": 1001,
        "title": "高等数学教材"
      },
      "lastMessage": "今晚 7 点可以吗？",
      "unreadCount": 1,
      "updatedAt": "2026-06-04T17:00:00"
    }
  ]
}
```

### 8.4 获取会话消息

`GET /api/chats/{chatId}/messages`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "messageId": 7001,
        "senderId": 1,
        "content": "今晚 7 点可以吗？",
        "createdAt": "2026-06-04T17:00:00"
      }
    ],
    "page": 1,
    "pageSize": 20,
    "total": 1
  }
}
```

### 8.5 发送消息

`POST /api/chats/{chatId}/messages`

请求体：

```json
{
  "content": "今晚 7 点可以吗？"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "messageId": 7001
  }
}
```

## 9. 评价

### 9.1 创建评价

`POST /api/orders/{orderId}/reviews`

说明：订单完成后可评价交易对象。

请求体：

```json
{
  "rating": 5,
  "content": "交易顺利，教材保存很好"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "reviewId": 8001
  }
}
```

### 9.2 获取用户评价

`GET /api/users/{userId}/reviews`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "reviewId": 8001,
        "rating": 5,
        "content": "交易顺利，教材保存很好",
        "reviewer": {
          "userId": 2,
          "nickname": "李四"
        },
        "createdAt": "2026-06-04T18:00:00"
      }
    ],
    "page": 1,
    "pageSize": 10,
    "total": 1
  }
}
```

## 9.5 求购与置换

### 9.5.1 获取求购列表

`GET /api/wanted-posts`

返回当前数据库 `wanted_posts` 中的求购帖子，分页结构同通用分页。

响应字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| postId | number | 求购 ID |
| userId | number | 发布人 |
| title | string | 求购物品 |
| description | string | 需求描述 |
| categoryId | number | 分类 ID |
| categoryName | string | 分类名称 |
| campus | string | 需求校区 |
| budgetMin | number | 最低预算 |
| budgetMax | number | 最高预算 |
| status | string | `OPEN` / `CLOSED` |

### 9.5.2 发布求购

`POST /api/wanted-posts`

请求体：

```json
{
  "title": "求购二手自行车",
  "description": "校内通勤用",
  "categoryId": 5,
  "campus": "校本部",
  "budgetMin": 200,
  "budgetMax": 350
}
```

### 9.5.3 关闭求购

`PATCH /api/wanted-posts/{postId}/close`

### 9.5.4 获取置换申请列表

`GET /api/swap-requests`

返回当前数据库 `swap_requests` 中的置换申请，分页结构同通用分页。

响应字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| swapRequestId | number | 置换申请 ID |
| requestNo | string | 申请编号 |
| requesterId | number | 发起人 |
| targetItemId | number | 目标商品 ID |
| targetItemTitle | string | 目标商品名称 |
| offeredItemId | number | 置换商品 ID |
| offeredItemTitle | string | 置换商品名称 |
| ownerId | number | 目标商品卖家 |
| status | string | `PENDING` / `ACCEPTED` / `REJECTED` / `CANCELLED` |
| message | string | 置换说明 |

### 9.5.5 创建置换申请

`POST /api/swap-requests`

请求体：

```json
{
  "targetItemId": 5,
  "offeredItemId": 1,
  "message": "想用高数教材加差价换羽毛球拍套装。"
}
```

### 9.5.6 处理置换申请

```http
PATCH /api/swap-requests/{requestId}/accept
PATCH /api/swap-requests/{requestId}/reject
PATCH /api/swap-requests/{requestId}/cancel
```

## 10. 管理端

### 10.1 获取用户列表

`GET /api/admin/users`

权限：管理员

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 学号、昵称、手机号 |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "userId": 1,
        "studentNo": "20240001",
        "nickname": "张三",
        "phone": "13800000000",
        "enabled": true,
        "createdAt": "2026-06-04T16:00:00"
      }
    ],
    "page": 1,
    "pageSize": 10,
    "total": 1
  }
}
```

### 10.2 禁用用户

`PATCH /api/admin/users/{userId}/disable`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 10.3 恢复用户

`PATCH /api/admin/users/{userId}/enable`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 10.4 管理端下架商品

`PATCH /api/admin/items/{itemId}/remove`

请求体：

```json
{
  "reason": "违规内容"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

## 11. 首期建议实现范围

首期建议先实现以下接口，保证平台主流程可跑通：

| 模块 | 接口 |
| --- | --- |
| 认证 | 注册、登录、当前用户信息 |
| 商品 | 分类列表、发布商品、商品列表、商品详情、修改商品、下架商品、我的发布 |
| 收藏 | 收藏、取消收藏、我的收藏 |
| 订单 | 创建订单、我的订单、订单详情、确认订单、取消订单、完成订单 |

消息、评价、管理端可以作为第二阶段实现。

## 12. 当前实现状态

当前后端已经提供 Spring Boot MVC REST API，接口路径、请求方式和统一响应结构可用于前后端联调。数据库使用 MySQL `second_hand_trade`，服务层通过 JDBC 读取初始化数据。

后端目录：

```text
backend/src/main/java/com/example/Second_hand/trading/platform
├─ controller
├─ service
├─ dto
└─ config
```

前端统一 API 客户端：

```text
fronted/src/services/api.js
```

前端 API 模块：

| 模块 | 说明 |
| --- | --- |
| `authApi` | 注册、登录、管理员登录 |
| `userApi` | 当前用户、我的发布、我的收藏、评价 |
| `fileApi` | 图片上传 |
| `categoryApi` | 分类列表 |
| `itemApi` | 商品、收藏、留言 |
| `orderApi` | 订单、取消、完成、评价 |
| `wantedApi` | 求购发布、求购列表、关闭求购 |
| `swapApi` | 置换申请、接受、拒绝、取消 |
| `chatApi` | 会话、消息 |
| `adminApi` | 后台管理接口 |

## 13. 管理员后台补充接口

### 13.1 管理员登录

`POST /api/auth/admin/login`

请求体：

```json
{
  "account": "admin",
  "password": "admin123456"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "accessToken": "mock-admin-token",
    "admin": {
      "username": "admin",
      "role": "ADMIN"
    }
  }
}
```

### 13.2 数据大盘

`GET /api/admin/dashboard`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "totalUsers": 12486,
    "todayNewUsers": 128,
    "onSaleItems": 3672,
    "todayAmount": 26840,
    "activeUsers": 2108
  }
}
```

### 13.3 后台商品列表

`GET /api/admin/items`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `categoryId` | number | 否 | 分类 ID |
| `campus` | string | 否 | 校区 |
| `status` | string | 否 | 商品状态 |
| `startDate` | string | 否 | 发布时间开始 |
| `endDate` | string | 否 | 发布时间结束 |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：分页商品列表。

### 13.4 删除商品

`DELETE /api/admin/items/{itemId}`

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 13.5 后台分类管理

获取分类：

`GET /api/admin/categories`

新增分类：

`POST /api/admin/categories`

请求体：

```json
{
  "name": "教材教辅",
  "tags": ["考研资料", "公共课教材"]
}
```

修改分类：

`PUT /api/admin/categories/{categoryId}`

删除分类：

`DELETE /api/admin/categories/{categoryId}`

### 13.6 后台订单列表

`GET /api/admin/orders`

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `orderStatus` | string | 否 | 订单状态 |
| `tradeMode` | string | 否 | 交易模式 |
| `startDate` | string | 否 | 开始日期 |
| `endDate` | string | 否 | 结束日期 |
| `page` | number | 否 | 页码 |
| `pageSize` | number | 否 | 每页条数 |

响应：分页订单列表。

### 13.7 纠纷列表

`GET /api/admin/disputes`

响应：分页纠纷列表。

### 13.8 纠纷仲裁

`PATCH /api/admin/disputes/{disputeId}/resolve`

请求体：

```json
{
  "result": "REFUND_APPROVED",
  "remark": "商品与描述不符，同意退款"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 13.9 举报列表

`GET /api/admin/reports`

响应：分页举报列表。

### 13.10 通过举报

`PATCH /api/admin/reports/{reportId}/approve`

请求体：

```json
{
  "action": "REMOVE_ITEM_AND_DEDUCT_CREDIT",
  "remark": "虚假商品，已下架并扣分"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 13.11 驳回举报

`PATCH /api/admin/reports/{reportId}/reject`

请求体：

```json
{
  "remark": "证据不足"
}
```

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

### 13.12 系统配置

获取配置：

`GET /api/admin/settings`

更新配置：

`PUT /api/admin/settings`

请求体：

```json
{
  "sensitiveWords": ["私下转账", "押金"],
  "payment": {
    "wechatAppId": "wx-campus-demo",
    "alipayAppId": "alipay-campus-demo",
    "campusCardMerchant": "CAMPUS-2026"
  },
  "tradeRules": {
    "maxImages": 9,
    "disputeDays": 3,
    "creditDeduction": 10
  }
}
```

### 13.13 公告管理

公告列表：

`GET /api/admin/notices`

新增公告：

`POST /api/admin/notices`

修改公告：

`PUT /api/admin/notices/{noticeId}`

删除公告：

`DELETE /api/admin/notices/{noticeId}`

新增/修改请求体：

```json
{
  "title": "毕业季闲置交易安全提醒",
  "content": "请优先选择同校区面交，贵重商品现场验机。",
  "scopeType": "ALL",
  "campus": "",
  "popupEnabled": true,
  "status": "PUBLISHED"
}
```

## 14. 接口实现边界

当前后端接口已连接 MySQL：

- 查询类接口从 `second_hand_trade` 读取初始化数据。
- 普通用户账号未初始化，`users` 表为空。
- 管理员账号保留 `admin/admin123456`。
- 登录 token 仍为演示 token，尚未接 JWT。
- 新增、修改、删除类接口当前以返回成功为主，后续需要补充真实写库逻辑、事务和权限校验。
- 文件上传接口当前返回数据库文件表中的示例图片 URL，尚未接本地/对象存储。

后续完善建议：

- 新增 Entity、Repository、Service 实现。
- 对订单、商品、举报、纠纷等写操作增加事务。
- 接入 JWT 登录态和后台权限拦截。
- 前端页面逐步由 mock 数据切换到 `fronted/src/services/api.js`。
