<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import ProductGridCard from '../../components/product/ProductGridCard.vue'
import ProductListItem from '../../components/product/ProductListItem.vue'
import { campuses, categories, conditions, products } from '../../data/mock'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const activeCampus = ref('全部校区')
const activeCategory = ref('全部分类')
const activeTab = ref('latest')
const noAutoJump = ref(false)

const filters = reactive({
  minPrice: '',
  maxPrice: '',
  campus: '',
  conditions: [],
  categories: [],
})

const campusTags = ['全部校区', ...campuses]
const categoryTags = ['全部分类', ...categories]

const recommendedProducts = computed(() => {
  if (activeTab.value === 'hot') {
    return [...products].sort((a, b) => b.hot - a.hot).slice(0, 4)
  }

  if (activeTab.value === 'near') {
    return products.filter((product) => product.campus === '校本部').slice(0, 4)
  }

  return products.slice(0, 4)
})

const filteredProducts = computed(() =>
  products.filter((product) => {
    const campusMatched =
      activeCampus.value === '全部校区' || product.campus === activeCampus.value
    const categoryMatched =
      activeCategory.value === '全部分类' || product.category === activeCategory.value
    const filterCampusMatched = !filters.campus || product.campus === filters.campus
    const conditionMatched =
      filters.conditions.length === 0 || filters.conditions.includes(product.condition)
    const categoryFilterMatched =
      filters.categories.length === 0 || filters.categories.includes(product.category)
    const minMatched = !filters.minPrice || product.price >= Number(filters.minPrice)
    const maxMatched = !filters.maxPrice || product.price <= Number(filters.maxPrice)

    return (
      campusMatched &&
      categoryMatched &&
      filterCampusMatched &&
      conditionMatched &&
      categoryFilterMatched &&
      minMatched &&
      maxMatched
    )
  }),
)

function goPublish() {
  if (authStore.isLoggedIn) {
    router.push('/items/publish')
    return
  }

  ElMessageBox.confirm('还没登录，登录或注册后才能免费发布商品。', '需要登录', {
    confirmButtonText: '前往登录',
    cancelButtonText: '前往注册',
    distinguishCancelAndClose: true,
    type: 'warning',
  })
    .then(() => router.push('/login'))
    .catch((action) => {
      if (action === 'cancel') router.push('/register')
    })
}
</script>

<template>
  <main class="home-page page-wrap with-floating-filter">
    <aside class="floating-filter">
      <h3>多维筛选</h3>
      <el-form label-position="top">
        <el-form-item label="价格区间">
          <div class="price-range">
            <el-input v-model="filters.minPrice" placeholder="最低价" />
            <span>-</span>
            <el-input v-model="filters.maxPrice" placeholder="最高价" />
          </div>
        </el-form-item>

        <el-form-item label="成色">
          <el-checkbox-group v-model="filters.conditions">
            <el-checkbox v-for="condition in conditions" :key="condition" :label="condition" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="校区">
          <el-select v-model="filters.campus" clearable placeholder="全部校区">
            <el-option v-for="campus in campuses" :key="campus" :label="campus" :value="campus" />
          </el-select>
        </el-form-item>

        <el-form-item label="分类">
          <el-checkbox-group v-model="filters.categories">
            <el-checkbox v-for="category in categories" :key="category" :label="category" />
          </el-checkbox-group>
        </el-form-item>
      </el-form>
    </aside>

    <section class="notice-strip">
      <span>已根据你的校区推荐附近二手商品，优先展示同校区可面交闲置。</span>
      <el-checkbox v-model="noAutoJump">不自动跳转</el-checkbox>
    </section>

    <section class="campus-shortcuts">
      <button
        v-for="campus in campusTags"
        :key="campus"
        :class="{ active: campus === activeCampus }"
        type="button"
        @click="activeCampus = campus"
      >
        {{ campus }}
      </button>
    </section>

    <section class="category-strip">
      <button
        v-for="category in categoryTags"
        :key="category"
        :class="{ active: category === activeCategory }"
        type="button"
        @click="activeCategory = category"
      >
        {{ category }}
      </button>
    </section>

    <section class="hero-panel">
      <div>
        <p class="section-kicker">同校区面交 · 轻量可信</p>
        <h1>校园闲置，先看身边同学在出什么</h1>
        <p>教材、数码、宿舍好物、毕业季清仓，一站式按校区筛选。</p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="goPublish">免费发布商品</el-button>
        <el-button size="large" @click="router.push('/wanted')">发布求购</el-button>
      </div>
    </section>

    <section class="content-section">
      <div class="section-head">
        <div>
          <p class="section-kicker">首页推荐</p>
          <h2>猜你想看</h2>
        </div>
        <el-tabs v-model="activeTab" class="compact-tabs">
          <el-tab-pane label="最新发布" name="latest" />
          <el-tab-pane label="热门商品" name="hot" />
          <el-tab-pane label="附近商品" name="near" />
        </el-tabs>
      </div>

      <div class="product-grid">
        <ProductGridCard v-for="product in recommendedProducts" :key="product.id" :product="product" />
      </div>
    </section>

    <section class="content-section">
      <div class="section-head">
        <div>
          <p class="section-kicker">全部分类</p>
          <h2>商品流列表</h2>
        </div>
        <span class="result-count">共 {{ filteredProducts.length }} 件</span>
      </div>

      <div class="product-list">
        <ProductListItem v-for="product in filteredProducts" :key="product.id" :product="product" />
      </div>
    </section>
  </main>
</template>
