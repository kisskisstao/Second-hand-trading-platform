<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../services/api'

const reports = ref([])
const loading = ref(false)
const pendingCount = computed(() => reports.value.filter((report) => report.status === 'PENDING').length)

function normalizeReport(row = {}) {
  return {
    id: row.reportId || row.id,
    reporter: row.reporterName || `用户${row.reporterId || ''}`,
    target: row.targetName || `${row.targetType || ''} ${row.targetId || ''}`,
    type: row.reportType || '',
    content: row.content || '',
    status: row.status || 'PENDING',
    createdAt: String(row.createdAt || '').replace('T', ' ').slice(0, 16),
  }
}

async function loadReports() {
  loading.value = true
  try {
    const response = await adminApi.reports({ page: 1, pageSize: 100 })
    reports.value = (response.data?.list || []).map(normalizeReport)
  } catch (error) {
    reports.value = []
    ElMessage.error(error.message || '举报列表加载失败')
  } finally {
    loading.value = false
  }
}

function handleReport(row, action) {
  ElMessageBox.confirm(`确认对举报 ${row.id} 执行“${action}”？`, '举报审核', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: action === '驳回举报' ? 'info' : 'warning',
  }).then(async () => {
    try {
      if (action === '驳回举报') {
        await adminApi.rejectReport(row.id, { remark: action })
      } else {
        await adminApi.approveReport(row.id, { remark: action })
      }
      ElMessage.success('举报处理已提交')
      loadReports()
    } catch (error) {
      ElMessage.error(error.message || '举报处理失败')
    }
  }).catch(() => {})
}

function statusType(status) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'info'
  return 'warning'
}

onMounted(loadReports)
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

      <el-table v-loading="loading" :data="reports" stripe>
        <el-table-column prop="id" label="举报编号" min-width="120" />
        <el-table-column prop="reporter" label="举报人" min-width="120" />
        <el-table-column prop="target" label="被举报对象" min-width="220" />
        <el-table-column prop="type" label="举报类型" min-width="130" />
        <el-table-column prop="content" label="举报内容" min-width="300" />
        <el-table-column prop="status" label="状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="260">
          <template #default="{ row }">
            <el-button link type="danger" :disabled="row.status !== 'PENDING'" @click="handleReport(row, '通过举报')">通过</el-button>
            <el-button link type="warning" :disabled="row.status !== 'PENDING'" @click="handleReport(row, '下架商品并扣分')">下架/扣分</el-button>
            <el-button link :disabled="row.status !== 'PENDING'" @click="handleReport(row, '驳回举报')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
