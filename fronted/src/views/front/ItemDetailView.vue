<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, Flag, StarFilled } from '@element-plus/icons-vue'
import ProductGridCard from '../../components/product/ProductGridCard.vue'
import { conditionTagMap, products } from '../../data/mock'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const comment = ref('')
const favorited = ref(false)

const product = computed(
  () => products.find((item) => String(item.id) === String(route.params.itemId)) || products[0],
)

const relatedProducts = computed(() =>
  products.filter((item) => item.id !== product.value.id && item.campus === product.value.campus).slice(0, 3),
)

const comments = ref([
  { user: '李同学', text: '请问今晚可以在图书馆门口交易吗？', reply: '可以，19:00 后都方便。' },
  { user: '王同学', text: '可以小刀吗？', reply: '同校区自取可以优惠一点。' },
])

function requireLogin(actionText) {
  if (authStore.isLoggedIn) return true

  ElMessageBox.confirm(`还没登录，${actionText}需要先登录或注册。`, '需要登录', {
    confirmButtonText: '前往登录',
    cancelButtonText: '前往注册',
    distinguishCancelAndClose: true,
    type: 'warning',
  })
    .then(() => router.push('/login'))
    .catch((action) => {
      if (action === 'cancel') {
        router.push('/register')
      }
    })
  return false
}

function toggleFavorite() {
  if (!requireLogin('收藏商品')) return
  favorited.value = !favorited.value
  ElMessage.success(favorited.value ? '已加入我的收藏' : '已取消收藏')
}

function consultSeller() {
  if (!requireLogin('咨询卖家')) return
  router.push('/chats')
}

function reserveItem() {
  if (!requireLogin('预订商品')) return
  ElMessageBox.confirm(`确认预订「${product.value.title}」？`, '预订商品', {
    confirmButtonText: '确认预订',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('预订成功，请在订单页查看交易口令')
    router.push('/orders')
  }).catch(() => {})
}

function reportItem() {
  if (!requireLogin('举报商品')) return
  ElMessageBox.prompt('请填写举报原因，管理员会在后台审核。', '举报商品', {
    confirmButtonText: '提交举报',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：虚假商品、描述不符、欺诈风险',
  }).then(({ value }) => {
    if (!value?.trim()) {
      ElMessage.warning('举报原因不能为空')
      return
    }
    ElMessage.success('举报已提交，等待管理员审核')
  }).catch(() => {})
}

function submitComment() {
  if (!requireLogin('发表留言')) return
  if (!comment.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  comments.value.push({
    user: authStore.user.nickname,
    text: comment.value.trim(),
    reply: '等待卖家回复',
  })
  comment.value = ''
  ElMessage.success('留言已发表')
}
</script>

<template>
  <main class="page-wrap detail-page">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.category }}</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.title }}</el-breadcrumb-item>
    </el-breadcrumb>

    <section class="detail-main">
      <div class="detail-gallery">
        <el-carousel height="430px" indicator-position="outside">
          <el-carousel-item v-for="index in 3" :key="index">
            <el-image :src="product.image" fit="cover" />
          </el-carousel-item>
        </el-carousel>
      </div>

      <el-card class="detail-info" shadow="never">
        <h1>{{ product.title }}</h1>
        <p class="detail-desc">{{ product.desc }}</p>
        <div class="price-row">
          <strong>￥{{ product.price }}</strong>
          <span>原价 ￥{{ product.originalPrice }}</span>
        </div>
        <div class="tag-row">
          <el-tag :type="conditionTagMap[product.condition]" size="large">{{ product.condition }}</el-tag>
          <el-tag effect="plain">{{ product.category }}</el-tag>
          <el-tag effect="plain">{{ product.campus }} · {{ product.dorm }}</el-tag>
        </div>

        <div class="seller-card">
          <el-avatar :size="52">{{ product.seller.slice(0, 1) }}</el-avatar>
          <div>
            <h3>{{ product.seller }}</h3>
            <p>信用分 {{ product.credit }}</p>
          </div>
          <el-tag type="success" effect="dark">高信用</el-tag>
        </div>

        <div class="detail-actions">
          <el-button size="large" :type="favorited ? 'warning' : 'default'" :icon="StarFilled" @click="toggleFavorite">
            {{ favorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button size="large" type="primary" :icon="ChatDotRound" @click="consultSeller">
            立即咨询
          </el-button>
          <el-button size="large" type="warning" @click="reserveItem">预订商品</el-button>
          <el-button size="large" :icon="Flag" @click="reportItem">举报</el-button>
        </div>
      </el-card>
    </section>

    <section class="detail-sections">
      <el-card shadow="never">
        <template #header>商品详细描述</template>
        <p class="long-text">
          {{ product.desc }} 支持同校区线下面交，看货满意后再确认交易。商品来源清晰，适合课程学习、宿舍使用或毕业季临时补齐装备。
        </p>
      </el-card>

      <el-card shadow="never">
        <template #header>商品留言</template>
        <div class="comment-list">
          <div v-for="item in comments" :key="item.text" class="comment-item">
            <strong>{{ item.user }}</strong>
            <p>{{ item.text }}</p>
            <div class="seller-reply">卖家回复：{{ item.reply }}</div>
          </div>
        </div>
        <div class="comment-input">
          <el-input v-model="comment" placeholder="向卖家留言咨询" />
          <el-button type="primary" @click="submitComment">发表留言</el-button>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>同校区相关推荐</template>
        <div class="product-grid three">
          <ProductGridCard
            v-for="item in relatedProducts.length ? relatedProducts : products.slice(1, 4)"
            :key="item.id"
            :product="item"
          />
        </div>
      </el-card>
    </section>
  </main>
</template>
