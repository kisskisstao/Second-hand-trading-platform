<script setup>
import { computed, reactive } from 'vue'
import { useRoute } from 'vue-router'
import ProductListItem from '../../components/product/ProductListItem.vue'
import { campuses, categories, conditions, products } from '../../data/mock'

const route = useRoute()

const filters = reactive({
  price: '',
  condition: '',
  campus: route.query.campus || '',
  category: '',
  sort: 'latest',
})

const keyword = computed(() => route.query.keyword || '校园闲置')

const filteredProducts = computed(() => {
  let list = products.filter((product) => {
    const keywordMatched =
      !keyword.value ||
      product.title.includes(keyword.value) ||
      product.desc.includes(keyword.value) ||
      product.category.includes(keyword.value)
    const campusMatched = !filters.campus || product.campus === filters.campus
    const conditionMatched = !filters.condition || product.condition === filters.condition
    const categoryMatched = !filters.category || product.category === filters.category
    const priceMatched =
      !filters.price ||
      (filters.price === '0-100' && product.price <= 100) ||
      (filters.price === '100-500' && product.price > 100 && product.price <= 500) ||
      (filters.price === '500+' && product.price > 500)

    return keywordMatched && campusMatched && conditionMatched && categoryMatched && priceMatched
  })

  if (list.length === 0) {
    list = products
  }

  if (filters.sort === 'price_asc') {
    return [...list].sort((a, b) => a.price - b.price)
  }

  if (filters.sort === 'price_desc') {
    return [...list].sort((a, b) => b.price - a.price)
  }

  if (filters.sort === 'distance') {
    return [...list].sort((a, b) => Number.parseFloat(a.distance) - Number.parseFloat(b.distance))
  }

  return list
})
</script>

<template>
  <main class="page-wrap search-page">
    <div class="page-title">
      <p class="section-kicker">搜索结果</p>
      <h1>“{{ keyword }}”相关商品</h1>
    </div>

    <el-card class="search-filter-card" shadow="never">
      <el-form label-position="top">
        <div class="search-filter-grid">
          <el-form-item label="价格">
            <el-select v-model="filters.price" clearable placeholder="不限">
              <el-option label="100 元以内" value="0-100" />
              <el-option label="100-500 元" value="100-500" />
              <el-option label="500 元以上" value="500+" />
            </el-select>
          </el-form-item>
          <el-form-item label="成色">
            <el-select v-model="filters.condition" clearable placeholder="不限">
              <el-option v-for="condition in conditions" :key="condition" :label="condition" :value="condition" />
            </el-select>
          </el-form-item>
          <el-form-item label="校区">
            <el-select v-model="filters.campus" clearable placeholder="不限">
              <el-option v-for="campus in campuses" :key="campus" :label="campus" :value="campus" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="filters.category" clearable placeholder="不限">
              <el-option v-for="category in categories" :key="category" :label="category" :value="category" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序">
            <el-select v-model="filters.sort">
              <el-option label="发布时间" value="latest" />
              <el-option label="价格从低到高" value="price_asc" />
              <el-option label="价格从高到低" value="price_desc" />
              <el-option label="距离优先" value="distance" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <section class="content-section">
      <div class="section-head">
        <div>
          <p class="section-kicker">筛选后商品</p>
          <h2>共 {{ filteredProducts.length }} 件结果</h2>
        </div>
      </div>
      <div class="product-list">
        <ProductListItem v-for="product in filteredProducts" :key="product.id" :product="product" />
      </div>
    </section>
  </main>
</template>
