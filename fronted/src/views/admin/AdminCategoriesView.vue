<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../services/api'

const categories = ref([])
const loading = ref(false)
const saving = ref(false)
const categoryDialog = ref(false)
const editingCategory = ref(null)
const categoryForm = reactive({
  name: '',
  tags: '',
  sortOrder: 0,
})

function normalizeCategory(row = {}) {
  return {
    id: row.categoryId || row.id,
    name: row.name || '',
    sortOrder: row.sortOrder || 0,
    productCount: Number(row.productCount || 0),
    tags: Array.isArray(row.tags)
      ? row.tags
      : String(row.tags || '').split(',').map((tag) => tag.trim()).filter(Boolean),
  }
}

async function loadCategories() {
  loading.value = true
  try {
    const response = await adminApi.categories()
    categories.value = (response.data || []).map(normalizeCategory)
  } catch (error) {
    categories.value = []
    ElMessage.error(error.message || '分类加载失败')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingCategory.value = null
  categoryForm.name = ''
  categoryForm.tags = ''
  categoryForm.sortOrder = categories.value.length + 1
  categoryDialog.value = true
}

function editCategory(category) {
  editingCategory.value = category
  categoryForm.name = category.name
  categoryForm.tags = category.tags.join(',')
  categoryForm.sortOrder = category.sortOrder
  categoryDialog.value = true
}

function deleteCategory(category) {
  ElMessageBox.confirm(`确认删除分类“${category.name}”？已有商品的分类不能删除。`, '删除分类', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await adminApi.deleteCategory(category.id)
      ElMessage.success('分类已删除')
      loadCategories()
    } catch (error) {
      ElMessage.error(error.message || '分类删除失败')
    }
  }).catch(() => {})
}

async function saveCategory() {
  if (!categoryForm.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  saving.value = true
  const payload = {
    name: categoryForm.name.trim(),
    tags: categoryForm.tags.split(/[,，]/).map((tag) => tag.trim()).filter(Boolean),
    sortOrder: categoryForm.sortOrder,
  }
  try {
    if (editingCategory.value) {
      await adminApi.updateCategory(editingCategory.value.id, payload)
    } else {
      await adminApi.createCategory(payload)
    }
    categoryDialog.value = false
    ElMessage.success('分类已保存')
    loadCategories()
  } catch (error) {
    ElMessage.error(error.message || '分类保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadCategories)
</script>

<template>
  <div class="admin-page">
    <el-card shadow="never">
      <template #header>
        <div class="admin-card-header">
          <strong>分类管理</strong>
          <el-button type="primary" @click="openCreate">新增一级分类</el-button>
        </div>
      </template>

      <div v-loading="loading" class="category-admin-grid">
        <el-card v-for="category in categories" :key="category.id" shadow="hover" class="category-admin-card">
          <div class="category-title-line">
            <h3>{{ category.name }}</h3>
            <el-tag>{{ category.productCount }} 件商品</el-tag>
          </div>
          <div class="tag-stack">
            <el-tag v-for="tag in category.tags" :key="tag">{{ tag }}</el-tag>
            <el-tag v-if="!category.tags.length" type="info">暂无标签</el-tag>
          </div>
          <div class="category-actions">
            <el-button type="primary" plain @click="editCategory(category)">编辑</el-button>
            <el-button type="danger" plain @click="deleteCategory(category)">删除</el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <el-dialog v-model="categoryDialog" :title="editingCategory ? '编辑分类' : '新增一级分类'" width="420">
      <el-form label-position="top">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sortOrder" :min="1" />
        </el-form-item>
        <el-form-item label="子标签">
          <el-input v-model="categoryForm.tags" placeholder="用逗号分隔多个标签" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
