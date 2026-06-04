<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCategories } from '../../data/adminMock'

const categoryDialog = ref(false)
const tagInput = ref('')
const categoryForm = reactive({
  name: '',
  tags: '',
})

function addTag(category) {
  if (!tagInput.value.trim()) {
    ElMessage.warning('请输入子标签名称')
    return
  }
  ElMessage.success(`已为「${category.name}」添加子标签：${tagInput.value.trim()}`)
  tagInput.value = ''
}

function closeTag(tag) {
  ElMessage.success(`子标签「${tag}」已删除`)
}

function editCategory(category) {
  ElMessage.info(`正在编辑分类：${category.name}`)
}

function deleteCategory(category) {
  ElMessageBox.confirm(`确认删除分类「${category.name}」？`, '删除分类', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('分类已删除')
  }).catch(() => {})
}

function saveCategory() {
  if (!categoryForm.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  categoryDialog.value = false
  ElMessage.success('分类已保存')
  categoryForm.name = ''
  categoryForm.tags = ''
}
</script>

<template>
  <div class="admin-page">
    <el-card shadow="never">
      <template #header>
        <div class="admin-card-header">
          <strong>分类管理</strong>
          <el-button type="primary" @click="categoryDialog = true">新增一级分类</el-button>
        </div>
      </template>

      <div class="category-admin-grid">
        <el-card v-for="category in adminCategories" :key="category.id" shadow="hover" class="category-admin-card">
          <div class="category-title-line">
            <h3>{{ category.name }}</h3>
            <el-tag>{{ category.productCount }} 件商品</el-tag>
          </div>
          <div class="tag-stack">
            <el-tag v-for="tag in category.tags" :key="tag" closable @close="closeTag(tag)">{{ tag }}</el-tag>
          </div>
          <div class="tag-add-row">
            <el-input v-model="tagInput" placeholder="新增子标签" />
            <el-button @click="addTag(category)">添加</el-button>
          </div>
          <div class="category-actions">
            <el-button type="primary" plain @click="editCategory(category)">编辑</el-button>
            <el-button type="danger" plain @click="deleteCategory(category)">删除</el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <el-dialog v-model="categoryDialog" title="新增一级分类" width="420">
      <el-form label-position="top">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="子标签">
          <el-input v-model="categoryForm.tags" placeholder="用逗号分隔多个标签" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
