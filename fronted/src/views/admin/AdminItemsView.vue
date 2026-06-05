<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { campuses, categories } from '../../data/mock'
import { adminApi } from '../../services/api'
import { normalizeItemPage } from '../../services/normalizers'

const selectedRows = ref([])
const products = ref([])
const loading = ref(false)
const filters = reactive({
  category: '',
  campus: '',
  date: '',
})

const filteredProducts = computed(() =>
  products.value.filter((product) => {
    const categoryMatched = !filters.category || product.category === filters.category
    const campusMatched = !filters.campus || product.campus === filters.campus
    const dateMatched =
      !Array.isArray(filters.date) ||
      filters.date.length !== 2 ||
      (new Date(product.publishedAt || product.date) >= new Date(filters.date[0]) &&
        new Date(product.publishedAt || product.date) <= new Date(filters.date[1]))
    return categoryMatched && campusMatched && dateMatched
  }),
)

async function loadItems() {
  loading.value = true
  try {
    const response = await adminApi.items()
    products.value = normalizeItemPage(response).list.map((item) => ({
      ...item,
      publishedAt: item.date,
    }))
  } catch (error) {
    ElMessage.error(error.message || '商品列表加载失败')
  } finally {
    loading.value = false
  }
}

function batchAudit() {
  ElMessage.success(`已审核 ${selectedRows.value.length || filteredProducts.value.length} 件商品`)
}

function batchRemove() {
  ElMessageBox.confirm('确认批量下架违规商品？', '批量下架', {
    confirmButtonText: '确认下架',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const rows = selectedRows.value.length ? selectedRows.value : filteredProducts.value
    await Promise.all(rows.map((row) => adminApi.offShelfItem(row.id)))
    ElMessage.success(`已下架 ${rows.length} 件商品`)
    loadItems()
  }).catch(() => {})
}

function removeItem(row) {
  ElMessageBox.confirm(`确认下架「${row.title}」？`, '下架违规商品', {
    confirmButtonText: '确认下架',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await adminApi.offShelfItem(row.id)
    ElMessage.success('商品已下架')
    loadItems()
  }).catch(() => {})
}

function deleteItem(row) {
  ElMessageBox.confirm(`确认删除「${row.title}」？删除后不可恢复。`, '删除商品', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'error',
  }).then(async () => {
    await adminApi.deleteItem(row.id)
    ElMessage.success('商品已删除')
    loadItems()
  }).catch(() => {})
}

function auditItem(row) {
  ElMessage.success(`商品「${row.title}」已通过审核`)
}

function statusType(status) {
  if (status === 'ON_SALE' || status === '上架') return 'success'
  if (status === 'REMOVED' || status === '违规') return 'danger'
  if (status === 'SOLD') return 'info'
  return 'warning'
}

function statusText(status) {
  const map = {
    ON_SALE: '上架',
    REMOVED: '已下架',
    SOLD: '已售出',
    RESERVED: '已预约',
    DRAFT: '草稿',
  }
  return map[status] || status || '未知'
}

onMounted(loadItems)
</script>

<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-filter-card">
      <el-form label-position="top">
        <div class="admin-filter-grid">
          <el-form-item label="分类">
            <el-select v-model="filters.category" clearable placeholder="全部分类">
              <el-option v-for="category in categories" :key="category" :label="category" :value="category" />
            </el-select>
          </el-form-item>
          <el-form-item label="校区">
            <el-select v-model="filters.campus" clearable placeholder="全部校区">
              <el-option v-for="campus in campuses" :key="campus" :label="campus" :value="campus" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布时间">
            <el-date-picker v-model="filters.date" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" />
          </el-form-item>
          <el-form-item label="批量操作">
            <el-button type="primary" @click="batchAudit">批量审核</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="admin-card-header">
          <strong>商品列表</strong>
          <el-button type="danger" @click="batchRemove">批量下架违规商品</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="filteredProducts" stripe @selection-change="selectedRows = $event">
        <el-table-column type="selection" width="48" />
        <el-table-column prop="title" label="商品标题" min-width="220" />
        <el-table-column prop="category" label="分类" min-width="110" />
        <el-table-column prop="seller" label="卖家" min-width="120" />
        <el-table-column prop="campus" label="发布校区" min-width="120" />
        <el-table-column prop="price" label="价格" min-width="100">
          <template #default="{ row }">￥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="status" label="商品状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" min-width="170" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button link type="warning" @click="removeItem(row)">下架违规商品</el-button>
            <el-button link type="danger" @click="deleteItem(row)">删除商品</el-button>
            <el-button v-if="row.status === '违规'" link type="primary" @click="auditItem(row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
