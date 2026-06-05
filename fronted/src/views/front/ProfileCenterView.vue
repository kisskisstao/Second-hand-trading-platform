<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductListItem from '../../components/product/ProductListItem.vue'
import { userApi } from '../../services/api'
import { normalizeItemPage } from '../../services/normalizers'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const activeMenu = ref('selling')
const myProducts = ref([])
const favoriteProducts = ref([])
const loadingItems = ref(false)

const menuItems = [
  { key: 'selling', label: '我的在售商品' },
  { key: 'orders', label: '我的订单' },
  { key: 'favorites', label: '我的收藏' },
  { key: 'reviews', label: '我的评价' },
  { key: 'wanted', label: '我的求购' },
  { key: 'swap', label: '以物换物' },
  { key: 'privacy', label: '隐私设置' },
]

const privacy = ref({
  phone: false,
  wechat: true,
})

const title = computed(() => menuItems.find((item) => item.key === activeMenu.value)?.label)
const currentUser = computed(() => authStore.user || {})
const displayName = computed(() => currentUser.value.nickname || currentUser.value.realName || currentUser.value.account)
const avatarText = computed(() => displayName.value?.slice(0, 1) || '用')
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
    const [itemsResponse, favoritesResponse] = await Promise.all([
      userApi.getMyItems({ page: 1, pageSize: 100 }),
      userApi.getMyFavorites({ page: 1, pageSize: 100 }),
    ])
    myProducts.value = normalizeItemPage(itemsResponse).list
    favoriteProducts.value = normalizeItemPage(favoritesResponse).list
  } catch (error) {
    myProducts.value = []
    favoriteProducts.value = []
    console.error(error)
  } finally {
    loadingItems.value = false
  }
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
          <div><strong>{{ myProducts.length }}</strong><span>在售商品</span></div>
          <div><strong>0</strong><span>已售商品</span></div>
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
            <el-tabs>
              <el-tab-pane label="上架中">
                <div v-if="myProducts.length > 0" class="product-list">
                  <ProductListItem v-for="product in myProducts" :key="product.id" :product="product" />
                </div>
                <el-empty v-else description="暂无在售商品" />
              </el-tab-pane>
              <el-tab-pane label="已下架">
                <el-empty description="暂无已下架商品" />
              </el-tab-pane>
              <el-tab-pane label="已出">
                <el-empty description="暂无已出商品" />
              </el-tab-pane>
              <el-tab-pane label="草稿">
                <el-empty description="暂无草稿" />
              </el-tab-pane>
            </el-tabs>
          </div>

          <div v-else-if="activeMenu === 'orders'" class="order-mini-list">
            <el-empty description="暂无订单" />
          </div>

          <div v-else-if="activeMenu === 'favorites'" class="product-list">
            <ProductListItem v-for="product in favoriteProducts" :key="product.id" :product="product" />
            <el-empty v-if="favoriteProducts.length === 0" description="暂无收藏商品" />
          </div>

          <div v-else-if="activeMenu === 'reviews'" class="review-list">
            <el-empty description="暂无评价" />
          </div>

          <div v-else-if="activeMenu === 'wanted'">
            <el-empty description="暂未发布求购" />
          </div>

          <div v-else-if="activeMenu === 'swap'" class="product-list">
            <el-empty description="暂无置换商品" />
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
