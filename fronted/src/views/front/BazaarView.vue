<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProductGridCard from '../../components/product/ProductGridCard.vue'
import { itemApi, wantedApi } from '../../services/api'
import { normalizeItemPage } from '../../services/normalizers'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const activeTab = ref(route.meta.tab || 'wanted')
const wantedItems = ref([])
const seasonProducts = ref([])
const loading = ref(false)

watch(
  () => route.meta.tab,
  (tab) => {
    activeTab.value = tab || 'wanted'
  },
)

function changeTab(tab) {
  const paths = {
    wanted: '/wanted',
    season: '/season',
  }
  router.push(paths[tab] || '/wanted')
}

function requireLogin(actionText) {
  if (authStore.isLoggedIn) return true

  ElMessageBox.confirm(`${actionText}需要先登录或注册。`, '需要登录', {
    confirmButtonText: '前往登录',
    cancelButtonText: '前往注册',
    distinguishCancelAndClose: true,
    type: 'warning',
  })
    .then(() => router.push('/login'))
    .catch((action) => {
      if (action === 'cancel') router.push('/register')
    })
  return false
}

function goPublish() {
  if (!requireLogin('发布闲置')) return
  router.push('/items/publish')
}

function publishWanted() {
  if (!requireLogin('发布求购')) return
  ElMessageBox.prompt('请输入要求购的物品名称。', '发布求购', {
    confirmButtonText: '发布',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：求购二手自行车',
  }).then(async ({ value }) => {
    if (!value?.trim()) {
      ElMessage.warning('求购物品不能为空')
      return
    }
    await wantedApi.create({
      title: value.trim(),
      description: '',
      campus: authStore.user?.campus || '校本部',
    })
    ElMessage.success('求购已发布')
    loadBazaar()
  }).catch(() => {})
}

function contactUser(item) {
  if (!requireLogin('联系发布人')) return
  ElMessage.success(`已打开与 ${item.user} 的咨询会话`)
  router.push('/chats')
}

async function loadBazaar() {
  loading.value = true
  try {
    const [wantedResponse, itemResponse] = await Promise.all([
      wantedApi.list({ page: 1, pageSize: 20 }),
      itemApi.list({ page: 1, pageSize: 6, sort: 'latest' }),
    ])
    wantedItems.value = normalizePurchases(wantedResponse)
    seasonProducts.value = normalizeItemPage(itemResponse).list
  } catch (error) {
    ElMessage.error(error.message || '广场数据加载失败')
  } finally {
    loading.value = false
  }
}

function normalizePurchases(response = {}) {
  const data = response.data || response
  return Array.isArray(data.list)
    ? data.list.map((item) => ({
      id: item.purchaseId,
      title: item.title,
      campus: item.campus,
      budget: budgetText(item),
      user: item.user?.nickname || `用户${item.userId || ''}`,
      createdAt: item.createdAt,
    }))
    : []
}

function budgetText(item) {
  if (item.budgetMin && item.budgetMax) return `￥${item.budgetMin} - ￥${item.budgetMax}`
  if (item.budgetMax) return `￥${item.budgetMax} 以内`
  if (item.budgetMin) return `￥${item.budgetMin} 以上`
  return '面议'
}

onMounted(loadBazaar)
</script>

<template>
  <main class="page-wrap bazaar-page">
    <section class="season-banner">
      <div>
        <p class="section-kicker">开学 / 毕业季专题</p>
        <h1>急售甩卖、求购补齐集中看</h1>
      </div>
      <el-button type="primary" size="large" @click="goPublish">发布闲置</el-button>
    </section>

    <el-tabs v-model="activeTab" v-loading="loading" class="bazaar-tabs" @tab-change="changeTab">
      <el-tab-pane label="求购广场" name="wanted">
        <div class="section-head">
          <div>
            <p class="section-kicker">同学正在找</p>
            <h2>求购列表</h2>
          </div>
          <el-button type="primary" @click="publishWanted">发布求购</el-button>
        </div>
        <div class="wanted-grid">
          <el-card v-for="item in wantedItems" :key="item.id" shadow="hover">
            <h3>{{ item.title }}</h3>
            <p>需求校区：{{ item.campus }}</p>
            <p>预算：{{ item.budget }}</p>
            <div class="card-footer-line">
              <span>{{ item.user }}</span>
              <el-button text type="primary" @click="contactUser(item)">联系 TA</el-button>
            </div>
          </el-card>
          <el-empty v-if="wantedItems.length === 0" description="暂无求购" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="毕业季专题" name="season">
        <div class="section-head">
          <div>
            <p class="section-kicker">毕业清仓</p>
            <h2>急售 / 甩卖商品</h2>
          </div>
        </div>
        <div class="product-grid">
          <ProductGridCard v-for="product in seasonProducts" :key="product.id" :product="product" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </main>
</template>
