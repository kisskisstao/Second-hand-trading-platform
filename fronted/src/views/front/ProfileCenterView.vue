<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProductListItem from '../../components/product/ProductListItem.vue'
import { itemApi, orderApi, reviewApi, userApi, wantedApi } from '../../services/api'
import { normalizeItemPage, normalizeOrder } from '../../services/normalizers'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const activeMenu = ref('selling')
const validItemStatusTabs = ['onSale', 'removed', 'sold', 'drafts']
const activeItemStatus = ref(validItemStatusTabs.includes(route.query.itemStatus) ? route.query.itemStatus : 'onSale')
const myProducts = ref([])
const favoriteProducts = ref([])
const myOrders = ref([])
const myReviews = ref([])
const myComments = ref([])
const myWantedPosts = ref([])
const notifications = ref([])
const loadingItems = ref(false)

const menuItems = [
  { key: 'selling', label: '我的在售商品' },
  { key: 'orders', label: '我的订单' },
  { key: 'favorites', label: '我的收藏' },
  { key: 'notifications', label: '系统通知' },
  { key: 'reviews', label: '我的评价/评论' },
  { key: 'wanted', label: '我的求购' },
  { key: 'privacy', label: '隐私设置' },
]

const privacy = ref({
  phone: false,
  wechat: true,
})

const title = computed(() => menuItems.find((item) => item.key === activeMenu.value)?.label)
const currentUser = computed(() => authStore.user || {})
const currentUserId = computed(() => currentUser.value.userId || currentUser.value.id)
const displayName = computed(() => currentUser.value.nickname || currentUser.value.realName || currentUser.value.account)
const avatarText = computed(() => displayName.value?.slice(0, 1) || '用')
const onSaleProducts = computed(() => myProducts.value.filter((product) => product.status === 'ON_SALE'))
const removedProducts = computed(() => myProducts.value.filter((product) => product.status === 'REMOVED'))
const soldProducts = computed(() => myProducts.value.filter((product) => product.status === 'SOLD' || product.status === 'RESERVED'))
const draftProducts = computed(() => myProducts.value.filter((product) => product.status === 'DRAFT'))
const currentStatusProducts = computed(() => {
  if (activeItemStatus.value === 'removed') return removedProducts.value
  if (activeItemStatus.value === 'sold') return soldProducts.value
  if (activeItemStatus.value === 'drafts') return draftProducts.value
  return onSaleProducts.value
})
const currentStatusEmptyText = computed(() => {
  if (activeItemStatus.value === 'removed') return '暂无已下架商品'
  if (activeItemStatus.value === 'sold') return '暂无已出商品'
  if (activeItemStatus.value === 'drafts') return '暂无草稿'
  return '暂无在售商品'
})
const profileLine = computed(() => {
  const parts = [
    currentUser.value.realName ? `实名：${currentUser.value.realName}` : '',
    currentUser.value.department || '',
    currentUser.value.enrollmentYear ? `${currentUser.value.enrollmentYear} 级` : '',
  ].filter(Boolean)

  return parts.length > 0 ? parts.join(' · ') : '暂无实名资料'
})

watch(
  () => authStore.isLoggedIn,
  (loggedIn) => {
    if (loggedIn) fetchUserItems()
  },
  { immediate: true },
)

watch(
  () => route.query,
  (query) => {
    if (query.tab && menuItems.some((item) => item.key === query.tab)) {
      activeMenu.value = query.tab
    }
    if (validItemStatusTabs.includes(query.itemStatus)) {
      activeItemStatus.value = query.itemStatus
    }
  },
)

function goRegister() {
  router.push('/register')
}

function savePrivacy() {
  ElMessage.success('隐私设置已保存')
}

async function fetchUserItems() {
  if (!authStore.isLoggedIn) return

  loadingItems.value = true
  try {
    const [itemsResponse, favoritesResponse, ordersResponse, reviewsResponse, commentsResponse, wantedResponses] = await Promise.all([
      userApi.getMyItems({ page: 1, pageSize: 100 }),
      userApi.getMyFavorites({ page: 1, pageSize: 100 }),
      orderApi.list({ page: 1, pageSize: 100 }),
      reviewApi.getUserReviews(currentUserId.value),
      userApi.getMyComments({ page: 1, pageSize: 100 }),
      Promise.all(['OPEN', 'CLOSED'].map((status) => wantedApi.list({ page: 1, pageSize: 100, status }))),
    ])
    myProducts.value = normalizeItemPage(itemsResponse).list
    favoriteProducts.value = normalizeItemPage(favoritesResponse).list
    myOrders.value = (ordersResponse.data?.list || []).map(normalizeOrder)
    myReviews.value = normalizeReviews(reviewsResponse)
    myComments.value = normalizeMyComments(commentsResponse)
    myWantedPosts.value = wantedResponses.flatMap(normalizePurchases).filter(isMine)
    await fetchNotifications()
  } catch (error) {
    myProducts.value = []
    favoriteProducts.value = []
    myOrders.value = []
    myReviews.value = []
    myComments.value = []
    myWantedPosts.value = []
    notifications.value = []
    console.error(error)
  } finally {
    loadingItems.value = false
  }
}

function normalizeMyComments(response = {}) {
  const rows = response.data?.list || response.data || response.list || []
  return Array.isArray(rows)
    ? rows.map((row) => ({
      id: row.commentId || row.id,
      itemId: row.itemId,
      itemTitle: row.itemTitle || '商品',
      userName: row.userName || `用户${row.userId || ''}`,
      relation: row.relation || 'SENT',
      content: row.content || '',
      createdAt: String(row.createdAt || '').replace('T', ' ').slice(0, 16),
    }))
    : []
}

function normalizeReviews(response = {}) {
  const rows = response.data || response
  return Array.isArray(rows)
    ? rows.map((row) => ({
      id: row.reviewId || row.id,
      orderId: row.orderId,
      reviewerId: row.reviewerId,
      targetUserId: row.targetUserId,
      rating: Number(row.rating) || 0,
      content: row.content || '',
      createdAt: String(row.createdAt || '').replace('T', ' ').slice(0, 16),
    }))
    : []
}

function normalizePurchases(response = {}) {
  const data = response.data || response
  return Array.isArray(data.list)
    ? data.list.map((row) => ({
      id: row.purchaseId,
      userId: row.userId,
      title: row.title,
      description: row.description || '',
      campus: row.campus || '',
      status: row.status || 'OPEN',
      budget: budgetText(row),
      createdAt: String(row.createdAt || '').replace('T', ' ').slice(0, 16),
    }))
    : []
}

function budgetText(row) {
  if (row.budgetMin && row.budgetMax) return `￥${row.budgetMin} - ￥${row.budgetMax}`
  if (row.budgetMax) return `￥${row.budgetMax} 以内`
  if (row.budgetMin) return `￥${row.budgetMin} 以上`
  return '面议'
}

function isMine(row) {
  return String(row.userId) === String(currentUserId.value)
}

async function fetchNotifications() {
  try {
    const response = await userApi.getMyNotifications({ page: 1, pageSize: 100 })
    notifications.value = (response.data?.list || []).map((row) => ({
      id: row.notificationId || row.id,
      title: row.title || '系统通知',
      content: row.content || '',
      type: row.type || 'SYSTEM',
      readAt: row.readAt || '',
      createdAt: String(row.createdAt || '').replace('T', ' ').slice(0, 16),
    }))
  } catch (error) {
    notifications.value = []
    console.error(error)
  }
}

function statusText(status) {
  const map = {
    ON_SALE: '上架中',
    REMOVED: '已下架',
    SOLD: '已出',
    RESERVED: '已预约',
    DRAFT: '草稿',
  }
  return map[status] || status || '未知'
}

function statusType(status) {
  if (status === 'ON_SALE') return 'success'
  if (status === 'REMOVED') return 'danger'
  if (status === 'SOLD' || status === 'RESERVED') return 'info'
  return 'warning'
}

function orderStatusText(status) {
  const map = {
    PENDING: '待接单',
    ACCEPTED: '待支付',
    PAYING: '支付中',
    PAID: '已支付',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
  }
  return map[status] || status || '未知'
}

function orderStatusType(status) {
  if (status === 'COMPLETED') return 'success'
  if (status === 'CANCELLED') return 'info'
  if (status === 'PENDING') return 'warning'
  return ''
}

function wantedStatusText(status) {
  const map = {
    OPEN: '求购中',
    CLOSED: '已关闭',
  }
  return map[status] || status || '未知'
}

function isBuyer(order) {
  return String(order.buyer?.userId) === String(currentUserId.value)
}

function isSeller(order) {
  return String(order.seller?.userId) === String(currentUserId.value)
}

async function acceptOrder(order) {
  try {
    await orderApi.accept(order.id)
    ElMessage.success('已接单')
    await fetchUserItems()
  } catch (error) {
    ElMessage.error(error.message || '接单失败')
  }
}

async function cancelOrder(order) {
  try {
    const { value } = await ElMessageBox.prompt('请填写取消原因', '取消订单', {
      confirmButtonText: '确认取消',
      cancelButtonText: '返回',
      inputPlaceholder: '例如：时间无法协调',
    })
    await orderApi.cancel(order.id, { reason: value || '用户取消' })
    ElMessage.success('订单已取消')
    await fetchUserItems()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '取消失败')
  }
}

async function completeOrder(order) {
  try {
    await orderApi.complete(order.id)
    ElMessage.success('订单已完成')
    await fetchUserItems()
  } catch (error) {
    ElMessage.error(error.message || '完成失败')
  }
}

async function payOrder(order, provider) {
  try {
    const response = await orderApi.pay(order.id, { provider })
    const payment = response.data
    if (payment.paymentUrl) {
      window.location.href = payment.paymentUrl
      return
    }
    if (payment.qrUrl) {
      ElMessageBox.alert(payment.qrUrl, `${provider} 支付二维码链接`, { confirmButtonText: '知道了' })
    } else {
      ElMessage.success('支付单已创建')
    }
    await fetchUserItems()
  } catch (error) {
    ElMessage.error(error.message || '支付创建失败')
  }
}

async function reviewOrder(order) {
  try {
    const { value: ratingValue } = await ElMessageBox.prompt('请填写 1-5 分', '评价订单', {
      confirmButtonText: '下一步',
      cancelButtonText: '取消',
      inputValue: '5',
      inputPattern: /^[1-5]$/,
      inputErrorMessage: '评分只能是 1 到 5',
    })
    const { value: content } = await ElMessageBox.prompt('请填写评价内容', '评价订单', {
      confirmButtonText: '提交评价',
      cancelButtonText: '取消',
      inputPlaceholder: '交易体验、商品情况等',
    })
    await orderApi.review(order.id, { rating: Number(ratingValue), content: content || '交易顺利' })
    ElMessage.success('评价已提交')
    await fetchUserItems()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '评价失败')
  }
}

async function closeWanted(post) {
  try {
    await wantedApi.close(post.id)
    ElMessage.success('求购已关闭')
    await fetchUserItems()
  } catch (error) {
    ElMessage.error(error.message || '关闭求购失败')
  }
}

async function publishProduct(product) {
  try {
    await itemApi.onShelf(product.id)
    ElMessage.success('商品已上架')
    await fetchUserItems()
    activeItemStatus.value = 'onSale'
  } catch (error) {
    ElMessage.error(error.message || '商品上架失败')
  }
}

async function offShelfProduct(product) {
  try {
    await itemApi.offShelf(product.id)
    ElMessage.success('商品已下架')
    await fetchUserItems()
    activeItemStatus.value = 'removed'
  } catch (error) {
    ElMessage.error(error.message || '商品下架失败')
  }
}

function deleteProduct(product) {
  ElMessageBox.confirm(`确认删除「${product.title}」？删除后不会出现在个人中心。`, '删除商品', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await itemApi.delete(product.id)
      ElMessage.success('商品已删除')
      await fetchUserItems()
    } catch (error) {
      ElMessage.error(error.message || '商品删除失败')
    }
  }).catch(() => {})
}

function viewProduct(product) {
  router.push(`/items/${product.id}`)
}
</script>

<template>
  <main class="page-wrap profile-page">
    <el-card v-if="!authStore.isLoggedIn" class="auth-required-card" shadow="never">
      <h1>还没登录</h1>
      <p>登录或注册后可以查看个人中心、订单、收藏和隐私设置。</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="router.push('/login')">前往登录</el-button>
        <el-button size="large" @click="goRegister">前往注册</el-button>
      </div>
    </el-card>

    <template v-else>
      <el-card class="profile-hero" shadow="never">
        <div class="profile-user">
          <el-avatar :size="82">{{ avatarText }}</el-avatar>
          <div>
            <h1>{{ displayName }}</h1>
            <p>{{ profileLine }}</p>
            <el-rate :model-value="5" disabled show-score :score-template="`信用分 ${currentUser.creditScore || 100}`" />
          </div>
        </div>
        <div class="profile-stats">
          <div><strong>{{ onSaleProducts.length }}</strong><span>在售商品</span></div>
          <div><strong>{{ soldProducts.length }}</strong><span>已售商品</span></div>
          <div><strong>{{ favoriteProducts.length }}</strong><span>收藏商品</span></div>
        </div>
      </el-card>

      <section class="profile-layout">
        <el-card shadow="never" class="profile-menu-card">
          <el-menu v-model="activeMenu" :default-active="activeMenu" @select="activeMenu = $event">
            <el-menu-item v-for="item in menuItems" :key="item.key" :index="item.key">
              {{ item.label }}
            </el-menu-item>
          </el-menu>
        </el-card>

        <el-card shadow="never" class="profile-content" v-loading="loadingItems">
          <template #header>
            <div class="card-header">
              <span>{{ title }}</span>
              <el-tag v-if="activeMenu === 'selling'" type="warning">上架 / 下架 / 已出 / 草稿</el-tag>
            </div>
          </template>

          <div v-if="activeMenu === 'selling'" class="status-board">
            <el-tabs v-model="activeItemStatus">
              <el-tab-pane :label="`上架中（${onSaleProducts.length}）`" name="onSale" />
              <el-tab-pane :label="`已下架（${removedProducts.length}）`" name="removed" />
              <el-tab-pane :label="`已出（${soldProducts.length}）`" name="sold" />
              <el-tab-pane :label="`草稿（${draftProducts.length}）`" name="drafts" />
            </el-tabs>

            <div v-if="currentStatusProducts.length > 0" class="profile-item-list">
              <div v-for="product in currentStatusProducts" :key="product.id" class="profile-item-row">
                <el-image class="profile-item-thumb" :src="product.image" fit="cover" />
                <div class="profile-item-main">
                  <div class="profile-item-title">
                    <strong>{{ product.title }}</strong>
                    <el-tag :type="statusType(product.status)">{{ statusText(product.status) }}</el-tag>
                  </div>
                  <p>{{ product.desc }}</p>
                  <div class="profile-item-meta">
                    <span>￥{{ product.price }}</span>
                    <span>{{ product.category }}</span>
                    <span>{{ product.campus }}</span>
                    <span>{{ product.date }}</span>
                  </div>
                </div>
                <div class="profile-item-actions">
                  <el-button
                    v-if="product.status === 'ON_SALE'"
                    type="primary"
                    plain
                    @click="viewProduct(product)"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    v-if="product.status === 'ON_SALE'"
                    type="warning"
                    plain
                    @click="offShelfProduct(product)"
                  >
                    下架
                  </el-button>
                  <el-button
                    v-if="['drafts', 'removed'].includes(activeItemStatus)"
                    type="primary"
                    @click="publishProduct(product)"
                  >
                    发布上架
                  </el-button>
                  <el-button
                    type="danger"
                    plain
                    @click="deleteProduct(product)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>

            <el-empty v-else :description="currentStatusEmptyText" />
          </div>

          <div v-else-if="activeMenu === 'orders'" class="order-mini-list">
            <el-card v-for="order in myOrders" :key="order.id" class="order-card" shadow="never">
              <div class="order-head">
                <span>订单号：{{ order.orderNo }}</span>
                <div class="order-head-tags">
                  <el-tag :type="isBuyer(order) ? 'success' : 'warning'" effect="plain">
                    {{ isBuyer(order) ? '我买到的' : '我卖出的' }}
                  </el-tag>
                  <el-tag :type="orderStatusType(order.status)">{{ orderStatusText(order.status) }}</el-tag>
                </div>
              </div>
              <div class="order-body profile-order-body">
                <el-image :src="order.product.image" fit="cover" />
                <div class="order-info">
                  <h3>{{ order.product.title }}</h3>
                  <p>{{ order.tradeMode }} · {{ order.createdAt }}</p>
                  <p>买家：{{ order.buyer?.nickname || '-' }} · 卖家：{{ order.seller?.nickname || '-' }}</p>
                  <strong>￥{{ order.amount }}</strong>
                </div>
                <div class="trade-code">
                  <span>交易口令</span>
                  <strong>{{ order.tradeCode || '-' }}</strong>
                  <div class="qr-box">PAY</div>
                </div>
                <div class="order-actions">
                  <el-button v-if="isSeller(order) && order.status === 'PENDING'" type="primary" @click="acceptOrder(order)">
                    接单
                  </el-button>
                  <el-dropdown
                    v-if="isBuyer(order) && ['PENDING', 'ACCEPTED', 'PAYING'].includes(order.status)"
                    @command="payOrder(order, $event)"
                  >
                    <el-button type="success">去支付</el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="ALIPAY">支付宝</el-dropdown-item>
                        <el-dropdown-item command="WECHAT">微信支付</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  <el-button
                    v-if="['ACCEPTED', 'PAID'].includes(order.status)"
                    type="primary"
                    plain
                    @click="completeOrder(order)"
                  >
                    完成交易
                  </el-button>
                  <el-button
                    v-if="isBuyer(order) && order.status === 'COMPLETED'"
                    type="warning"
                    plain
                    @click="reviewOrder(order)"
                  >
                    评价
                  </el-button>
                  <el-button v-if="!['COMPLETED', 'CANCELLED'].includes(order.status)" @click="cancelOrder(order)">
                    取消
                  </el-button>
                </div>
              </div>
            </el-card>
            <el-empty v-if="myOrders.length === 0" description="暂无订单" />
          </div>

          <div v-else-if="activeMenu === 'favorites'" class="product-list">
            <ProductListItem v-for="product in favoriteProducts" :key="product.id" :product="product" />
            <el-empty v-if="favoriteProducts.length === 0" description="暂无收藏商品" />
          </div>

          <div v-else-if="activeMenu === 'notifications'" class="notification-list">
            <div v-for="notice in notifications" :key="notice.id" class="notification-row">
              <div>
                <strong>{{ notice.title }}</strong>
                <p>{{ notice.content }}</p>
                <small>{{ notice.createdAt }}</small>
              </div>
              <el-tag type="warning" effect="plain">系统信息</el-tag>
            </div>
            <el-empty v-if="notifications.length === 0" description="暂无系统通知" />
          </div>

          <div v-else-if="activeMenu === 'reviews'" class="review-list">
            <div class="profile-subsection-title">订单评价</div>
            <div v-for="review in myReviews" :key="review.id" class="review-row">
              <div>
                <el-rate :model-value="review.rating" disabled />
                <p>{{ review.content || '暂无评价内容' }}</p>
                <small>订单：{{ review.orderId || '-' }} · {{ review.createdAt }}</small>
              </div>
            </div>
            <el-empty v-if="myReviews.length === 0" description="暂无订单评价" />

            <div class="profile-subsection-title">商品评论</div>
            <div v-for="comment in myComments" :key="comment.id" class="review-row">
              <div>
                <strong>{{ comment.itemTitle }}</strong>
                <p>{{ comment.content || '暂无评论内容' }}</p>
                <small>
                  {{ comment.relation === 'RECEIVED' ? '收到评论' : '我发表的评论' }}
                  · {{ comment.userName }} · {{ comment.createdAt }}
                </small>
              </div>
              <el-button text type="primary" @click="router.push(`/items/${comment.itemId}`)">查看商品</el-button>
            </div>
            <el-empty v-if="myComments.length === 0" description="暂无商品评论" />
          </div>

          <div v-else-if="activeMenu === 'wanted'" class="profile-record-list">
            <div v-for="post in myWantedPosts" :key="post.id" class="profile-record-row">
              <div>
                <strong>{{ post.title }}</strong>
                <p>{{ post.description || '暂无补充说明' }}</p>
                <small>{{ post.campus || '不限校区' }} · {{ post.budget }} · {{ post.createdAt }}</small>
              </div>
              <div class="profile-record-actions">
                <el-tag :type="post.status === 'OPEN' ? 'success' : 'info'">{{ wantedStatusText(post.status) }}</el-tag>
                <el-button v-if="post.status === 'OPEN'" plain @click="closeWanted(post)">关闭</el-button>
              </div>
            </div>
            <el-empty v-if="myWantedPosts.length === 0" description="暂未发布求购" />
          </div>

          <div v-else class="privacy-list">
            <div class="privacy-row">
              <div>
                <strong>手机号公开</strong>
                <p>开启后，交易双方可在订单详情查看手机号。</p>
              </div>
              <el-switch v-model="privacy.phone" @change="savePrivacy" />
            </div>
            <div class="privacy-row">
              <div>
                <strong>QQ / 微信公开</strong>
                <p>开启后，交易双方可在咨询页查看联系方式。</p>
              </div>
              <el-switch v-model="privacy.wechat" @change="savePrivacy" />
            </div>
          </div>
        </el-card>
      </section>
    </template>
  </main>
</template>
