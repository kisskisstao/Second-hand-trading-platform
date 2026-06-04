// Keep this file aligned with backend/sql/02_seed_data.sql.

import { campuses, categories, categoryTags, orders, products, swapRequests } from './mock.js'

export const adminStats = [
  { label: '总用户数', value: '0', trend: '普通用户未初始化' },
  { label: '今日新增用户', value: '0', trend: '无普通用户账号' },
  { label: '在售商品总量', value: '8', trend: '来自 items 表' },
  { label: '今日交易额', value: '￥3,839.00', trend: '来自 orders 表' },
  { label: '活跃用户数', value: '10', trend: '来自 notifications 表' },
]

export const adminUsers = []

export const adminProducts = products.map((product) => ({
  id: product.id,
  title: product.title,
  category: product.category,
  seller: product.seller,
  campus: product.campus,
  price: product.price,
  status: product.statusText,
  publishedAt: product.date,
}))

export const adminCategories = categories.map((category, index) => ({
  id: index + 1,
  name: category,
  tags: categoryTags.filter((tag) => tag.category === category).map((tag) => tag.name),
  productCount: products.filter((product) => product.category === category).length,
}))

export const adminOrders = orders.map((order) => ({
  id: order.id,
  product: order.product.title,
  buyer: `用户${2000 + Number(order.id.slice(-3))}`,
  seller: order.product.seller,
  status: order.status,
  mode: order.mode,
  amount: order.amount,
  createdAt: `2026-06-04 ${String(9 + Number(order.id.slice(-2))).padStart(2, '0')}:00`,
}))

export const adminSwapRequests = swapRequests.map((request) => ({
  id: request.id,
  requester: request.requester,
  target: request.target.title,
  offered: request.offered.title,
  owner: request.target.seller,
  status: request.status,
  message: request.message,
}))

export const adminDisputes = [
  {
    id: 'DP202606040001',
    orderId: 'OD202606040003',
    buyer: '用户2003',
    seller: '用户1003',
    reason: '商品制冷效果与描述不一致。',
    status: '待仲裁',
    amount: 260,
  },
  {
    id: 'DP202606040002',
    orderId: 'OD202606040004',
    buyer: '用户2004',
    seller: '用户1004',
    reason: '交易地点临时变更产生争议。',
    status: '处理中',
    amount: 99,
  },
  {
    id: 'DP202606040003',
    orderId: 'OD202606040005',
    buyer: '用户2005',
    seller: '用户1005',
    reason: '取消订单后退款沟通不一致。',
    status: '已驳回',
    amount: 68,
  },
  {
    id: 'DP202606040004',
    orderId: 'OD202606040006',
    buyer: '用户2006',
    seller: '用户1006',
    reason: '商品配件缺失。',
    status: '同意退款',
    amount: 128,
  },
  {
    id: 'DP202606040005',
    orderId: 'OD202606040007',
    buyer: '用户2007',
    seller: '用户1007',
    reason: '资料版本不符。',
    status: '待仲裁',
    amount: 35,
  },
  {
    id: 'DP202606040006',
    orderId: 'OD202606040008',
    buyer: '用户2008',
    seller: '用户1008',
    reason: '桌子有划痕。',
    status: '处理中',
    amount: 22,
  },
  {
    id: 'DP202606040007',
    orderId: 'OD202606040009',
    buyer: '用户2009',
    seller: '用户1009',
    reason: '吉他琴弦损坏。',
    status: '待仲裁',
    amount: 180,
  },
  {
    id: 'DP202606040008',
    orderId: 'OD202606040010',
    buyer: '用户2010',
    seller: '用户1010',
    reason: '自行车刹车问题。',
    status: '待仲裁',
    amount: 320,
  },
  {
    id: 'DP202606040009',
    orderId: 'OD202606040001',
    buyer: '用户2001',
    seller: '用户1001',
    reason: '面交时间冲突。',
    status: '已驳回',
    amount: 28,
  },
  {
    id: 'DP202606040010',
    orderId: 'OD202606040002',
    buyer: '用户2002',
    seller: '用户1002',
    reason: '屏幕轻微划痕争议。',
    status: '处理中',
    amount: 2699,
  },
]

export const adminReports = [
  {
    id: 'RP202606040001',
    reporter: '用户2001',
    target: 'iPad Air 5 64G 深空灰',
    type: '虚假商品',
    content: '图片疑似网图，拒绝验机。',
    status: '待审核',
  },
  {
    id: 'RP202606040002',
    reporter: '用户2002',
    target: '用户1003',
    type: '欺诈',
    content: '要求脱离平台先转账。',
    status: '待审核',
  },
  {
    id: 'RP202606040003',
    reporter: '用户2003',
    target: '聊天消息9',
    type: '敏感交易',
    content: '聊天中出现脱离平台交易提示。',
    status: '已通过',
  },
  {
    id: 'RP202606040004',
    reporter: '用户2004',
    target: '宿舍小冰箱52L',
    type: '描述不符',
    content: '商品状态与描述不一致。',
    status: '待审核',
  },
  {
    id: 'RP202606040005',
    reporter: '用户2005',
    target: '羽毛球拍双拍套装',
    type: '违禁内容',
    content: '疑似发布不合规物品。',
    status: '已驳回',
  },
  {
    id: 'RP202606040006',
    reporter: '用户2006',
    target: '用户1006',
    type: '骚扰',
    content: '频繁发送无关消息。',
    status: '待审核',
  },
  {
    id: 'RP202606040007',
    reporter: '用户2007',
    target: '考研英语真题套装',
    type: '虚假价格',
    content: '价格与详情描述不一致。',
    status: '待审核',
  },
  {
    id: 'RP202606040008',
    reporter: '用户2008',
    target: '聊天消息8',
    type: '联系方式外泄',
    content: '聊天中引导私聊。',
    status: '已通过',
  },
  {
    id: 'RP202606040009',
    reporter: '用户2009',
    target: '入门吉他一把',
    type: '图片不清晰',
    content: '无法确认商品状态。',
    status: '已驳回',
  },
  {
    id: 'RP202606040010',
    reporter: '用户2010',
    target: '用户1010',
    type: '迟到爽约',
    content: '多次约定不出现。',
    status: '待审核',
  },
]

export const sensitiveWords = [
  '私下转账',
  '押金',
  '脱离平台',
  '先付款',
  '加微信交易',
  '绕过平台',
  '定金不退',
  '银行卡转账',
  '虚拟币',
  '不走平台',
]

export const announcements = [
  { id: 1, title: '毕业季闲置交易安全提醒', scope: '全平台', status: '已发布', createdAt: '2026-06-01 09:00' },
  { id: 2, title: '东校区周末跳蚤市场开放通知', scope: '东校区', status: '已发布', createdAt: '2026-06-02 09:00' },
  { id: 3, title: '平台敏感词规则更新', scope: '全平台', status: '已发布', createdAt: '2026-06-03 09:00' },
  { id: 4, title: '校本部图书资料专区上线', scope: '校本部', status: '已发布', createdAt: '2026-06-03 10:00' },
  { id: 5, title: '西校区宿舍用品专场', scope: '西校区', status: '草稿', createdAt: '2026-06-03 11:00' },
  { id: 6, title: '南校区运动户外专场', scope: '南校区', status: '已发布', createdAt: '2026-06-03 12:00' },
  { id: 7, title: '大学城校区以物换物活动', scope: '大学城校区', status: '已发布', createdAt: '2026-06-04 09:00' },
  { id: 8, title: '举报审核处理说明', scope: '全平台', status: '草稿', createdAt: '2026-06-04 09:30' },
  { id: 9, title: '订单纠纷处理时效说明', scope: '全平台', status: '已发布', createdAt: '2026-06-04 10:00' },
  { id: 10, title: '校园卡支付测试通知', scope: '全平台', status: '草稿', createdAt: '2026-06-04 10:30' },
]

export const campusDistribution = campuses.map((campus) => ({
  campus,
  count: products.filter((product) => product.campus === campus).length,
}))
