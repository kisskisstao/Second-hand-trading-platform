<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminProducts } from '../../data/adminMock'
import { campuses, categories } from '../../data/mock'

const selectedRows = ref([])
const filters = reactive({
  category: '',
  campus: '',
  date: '',
})

const filteredProducts = computed(() =>
  adminProducts.filter((product) => {
    const categoryMatched = !filters.category || product.category === filters.category
    const campusMatched = !filters.campus || product.campus === filters.campus
    const dateMatched =
      !Array.isArray(filters.date) ||
      filters.date.length !== 2 ||
      (new Date(product.publishedAt) >= new Date(filters.date[0]) &&
        new Date(product.publishedAt) <= new Date(filters.date[1]))
    return categoryMatched && campusMatched && dateMatched
  }),
)

function batchAudit() {
  ElMessage.success(`已审核 ${selectedRows.value.length || filteredProducts.value.length} 件商品`)
}

function batchRemove() {
  ElMessageBox.confirm('确认批量下架违规商品？', '批量下架', {
    confirmButtonText: '确认下架',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('批量下架操作已提交')
  }).catch(() => {})
}

function removeItem(row) {
  ElMessageBox.confirm(`确认下架「${row.title}」？`, '下架违规商品', {
    confirmButtonText: '确认下架',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('商品已下架')
  }).catch(() => {})
}

function deleteItem(row) {
  ElMessageBox.confirm(`确认删除「${row.title}」？删除后不可恢复。`, '删除商品', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'error',
  }).then(() => {
    ElMessage.success('商品已删除')
  }).catch(() => {})
}

function auditItem(row) {
  ElMessage.success(`商品「${row.title}」已通过审核`)
}
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

      <el-table :data="filteredProducts" stripe @selection-change="selectedRows = $event">
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
            <el-tag :type="row.status === '上架' ? 'success' : row.status === '违规' ? 'danger' : 'info'">
              {{ row.status }}
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
