<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orders } from '../../data/mock'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const activeStatus = ref('待面交')
const reviewDialog = ref(false)
const rating = ref(5)
const reviewText = ref('')
const currentOrder = ref(null)

const statuses = ['待预订', '待面交', '待发货', '待收货', '已完成', '已取消']

const visibleOrders = computed(() => {
  const list = orders.filter((order) => order.status === activeStatus.value)
  return list.length ? list : orders.slice(0, 1).map((order) => ({ ...order, status: activeStatus.value }))
})

function openRefund(order) {
  ElMessageBox.prompt(`请填写订单 ${order.id} 的退款原因。`, '退款申请', {
    confirmButtonText: '提交申请',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：卖家无法按约定时间面交',
  }).then(({ value }) => {
    if (!value?.trim()) {
      ElMessage.warning('退款原因不能为空')
      return
    }
    ElMessage.success('退款申请已提交')
  }).catch(() => {})
}

function openReview(order) {
  currentOrder.value = order
  reviewDialog.value = true
}

function submitReview() {
  if (!reviewText.value.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }
  ElMessage.success('评价已提交')
  reviewDialog.value = false
  reviewText.value = ''
}

function viewDetail(order) {
  ElMessageBox.alert(
    `交易模式：${order.mode}\n交易口令：${order.code}\n商品：${order.product.title}`,
    `订单 ${order.id}`,
    {
      confirmButtonText: '知道了',
    },
  )
}
</script>

<template>
  <main class="page-wrap orders-page">
    <el-card v-if="!authStore.isLoggedIn" class="auth-required-card" shadow="never">
      <h1>还没登录</h1>
      <p>登录或注册后可以查看订单、申请退款和完成互评。</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="router.push('/login')">前往登录</el-button>
        <el-button size="large" @click="router.push('/register')">前往注册</el-button>
      </div>
    </el-card>

    <template v-else>
    <div class="page-title">
      <p class="section-kicker">订单管理</p>
      <h1>我的交易订单</h1>
    </div>

    <el-tabs v-model="activeStatus" class="order-tabs">
      <el-tab-pane v-for="status in statuses" :key="status" :label="status" :name="status">
        <div class="order-list">
          <el-card v-for="order in visibleOrders" :key="`${order.id}-${status}`" class="order-card" shadow="hover">
            <div class="order-head">
              <span>订单号：{{ order.id }}</span>
              <el-tag type="warning">{{ status }}</el-tag>
            </div>
            <div class="order-body">
              <el-image :src="order.product.image" fit="cover" />
              <div class="order-info">
                <h3>{{ order.product.title }}</h3>
                <p>{{ order.mode }} · {{ order.product.campus }} · {{ order.product.dorm }}</p>
                <strong>￥{{ order.amount }}</strong>
              </div>
              <div class="trade-code">
                <span>交易口令</span>
                <strong>{{ order.code }}</strong>
                <div class="qr-box">QR</div>
              </div>
              <div class="order-actions">
                <el-button @click="openRefund(order)">退款申请</el-button>
                <el-button v-if="status === '已完成'" type="primary" @click="openReview(order)">
                  去互评
                </el-button>
                <el-button v-else type="primary" @click="viewDetail(order)">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="reviewDialog" :title="currentOrder ? `交易互评 - ${currentOrder.id}` : '交易互评'" width="480">
      <el-form label-position="top">
        <el-form-item label="星级评价">
          <el-rate v-model="rating" />
        </el-form-item>
        <el-form-item label="文字评价">
          <el-input v-model="reviewText" type="textarea" :rows="4" placeholder="写下你的交易体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
    </template>
  </main>
</template>
