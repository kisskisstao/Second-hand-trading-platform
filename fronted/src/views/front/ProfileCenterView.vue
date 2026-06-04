<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductListItem from '../../components/product/ProductListItem.vue'
import { orders, products } from '../../data/mock'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const activeMenu = ref('selling')

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

function goRegister() {
  router.push('/register')
}

function savePrivacy() {
  ElMessage.success('隐私设置已保存')
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
        <el-avatar :size="82">{{ authStore.user.nickname.slice(0, 1) }}</el-avatar>
        <div>
          <h1>{{ authStore.user.nickname }}</h1>
          <p>实名：林同学 · 数学与应用数学 · 2024 级</p>
          <el-rate :model-value="4.8" disabled show-score score-template="信用分 96" />
        </div>
      </div>
      <div class="profile-stats">
        <div><strong>8</strong><span>在售商品</span></div>
        <div><strong>21</strong><span>已售商品</span></div>
        <div><strong>36</strong><span>收藏商品</span></div>
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

      <el-card shadow="never" class="profile-content">
        <template #header>
          <div class="card-header">
            <span>{{ title }}</span>
            <el-tag v-if="activeMenu === 'selling'" type="warning">上架 / 下架 / 已出 / 草稿</el-tag>
          </div>
        </template>

        <div v-if="activeMenu === 'selling'" class="status-board">
          <el-tabs>
            <el-tab-pane label="上架中">
              <ProductListItem v-for="product in products.slice(0, 3)" :key="product.id" :product="product" />
            </el-tab-pane>
            <el-tab-pane label="已下架">
              <el-empty description="暂无已下架商品" />
            </el-tab-pane>
            <el-tab-pane label="已出">
              <ProductListItem :product="products[5]" />
            </el-tab-pane>
            <el-tab-pane label="草稿">
              <el-empty description="暂无草稿" />
            </el-tab-pane>
          </el-tabs>
        </div>

        <div v-else-if="activeMenu === 'orders'" class="order-mini-list">
          <div v-for="order in orders" :key="order.id" class="mini-order">
            <el-image :src="order.product.image" fit="cover" />
            <div>
              <strong>{{ order.product.title }}</strong>
              <p>{{ order.id }} · {{ order.status }} · {{ order.mode }}</p>
            </div>
            <span>￥{{ order.amount }}</span>
          </div>
        </div>

        <div v-else-if="activeMenu === 'favorites'" class="product-list">
          <ProductListItem v-for="product in products.slice(2, 6)" :key="product.id" :product="product" />
        </div>

        <div v-else-if="activeMenu === 'reviews'" class="review-list">
          <el-rate :model-value="5" disabled />
          <p>交易准时，商品和描述一致，沟通很顺畅。</p>
          <el-rate :model-value="4" disabled />
          <p>线下面交地点清楚，整体体验不错。</p>
        </div>

        <div v-else-if="activeMenu === 'wanted'">
          <el-empty description="暂未发布求购" />
        </div>

        <div v-else-if="activeMenu === 'swap'" class="product-list">
          <ProductListItem v-for="product in products.filter((item) => item.swap)" :key="product.id" :product="product" />
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
