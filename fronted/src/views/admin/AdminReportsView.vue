<script setup>
import { computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminReports } from '../../data/adminMock'

const pendingCount = computed(() => adminReports.filter((report) => report.status === '待审核').length)

function handleReport(row, action) {
  ElMessageBox.confirm(`确认对举报 ${row.id} 执行「${action}」？`, '举报审核', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: action.includes('驳回') ? 'info' : 'warning',
  }).then(() => {
    ElMessage.success(`举报处理已提交：${action}`)
  }).catch(() => {})
}
</script>

<template>
  <div class="admin-page">
    <el-card shadow="never">
      <template #header>
        <div class="admin-card-header">
          <strong>举报审核管理</strong>
          <el-tag type="danger">待审核 {{ pendingCount }} 条</el-tag>
        </div>
      </template>

      <el-table :data="adminReports" stripe>
        <el-table-column prop="id" label="举报编号" min-width="160" />
        <el-table-column prop="reporter" label="举报人" min-width="110" />
        <el-table-column prop="target" label="被举报商品/用户" min-width="220" />
        <el-table-column prop="type" label="举报类型" min-width="130">
          <template #default="{ row }">
            <el-tag :type="row.type === '欺诈' ? 'danger' : 'warning'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="举报内容" min-width="300" />
        <el-table-column prop="status" label="状态" min-width="110" />
        <el-table-column label="操作" fixed="right" width="270">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleReport(row, '通过举报')">通过举报</el-button>
            <el-button link type="warning" @click="handleReport(row, '下架商品+扣分封号')">下架商品+扣分封号</el-button>
            <el-button link @click="handleReport(row, '驳回举报')">驳回举报</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
