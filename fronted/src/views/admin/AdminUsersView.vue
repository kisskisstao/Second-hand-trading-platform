<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../services/api'

const users = ref([])
const loading = ref(false)
const detailDialog = ref(false)
const currentUser = ref({})

function normalizeUser(row = {}) {
  return {
    id: row.userId || row.id,
    studentNo: row.studentNo || '',
    name: row.realName || row.nickname || '',
    nickname: row.nickname || '',
    department: row.department || '',
    verified: row.verifiedStatus || row.verified_status || '-',
    credit: row.creditScore ?? row.credit_score ?? 100,
    contact: row.phone || row.email || '-',
    registeredAt: String(row.createdAt || '').replace('T', ' ').slice(0, 16),
    status: row.status || 'NORMAL',
  }
}

async function loadUsers() {
  loading.value = true
  try {
    const response = await adminApi.users({ page: 1, pageSize: 100 })
    users.value = (response.data?.list || []).map(normalizeUser)
  } catch (error) {
    users.value = []
    ElMessage.error(error.message || '用户列表加载失败')
  } finally {
    loading.value = false
  }
}

function openDetail(user) {
  currentUser.value = user
  detailDialog.value = true
}

function exportUsers() {
  ElMessage.success(`当前可导出 ${users.value.length} 个用户`)
}

function batchAudit() {
  ElMessage.info('实名审核接口暂未独立建模，当前用户注册后默认为已认证')
}

function auditUser() {
  ElMessage.info('实名审核接口暂未独立建模')
}

function toggleUserStatus(user) {
  const disabled = user.status === 'DISABLED'
  ElMessageBox.confirm(`确认${disabled ? '启用' : '禁用'}该用户？`, '账号状态调整', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      if (disabled) {
        await adminApi.enableUser(user.id)
      } else {
        await adminApi.disableUser(user.id)
      }
      ElMessage.success('账号状态已更新')
      loadUsers()
    } catch (error) {
      ElMessage.error(error.message || '账号状态更新失败')
    }
  }).catch(() => {})
}

function viewViolations(user) {
  ElMessageBox.alert(`${user.name || user.studentNo} 暂无独立违规记录列表，可在举报审核中查看处理记录。`, '违规记录', {
    confirmButtonText: '知道了',
  })
}

onMounted(loadUsers)
</script>

<template>
  <div class="admin-page">
    <el-card shadow="never">
      <template #header>
        <div class="admin-card-header">
          <strong>用户管理</strong>
          <div>
            <el-button @click="exportUsers">导出用户</el-button>
            <el-button type="primary" @click="batchAudit">批量审核实名</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="users" stripe>
        <el-table-column prop="studentNo" label="学号" min-width="120" />
        <el-table-column prop="name" label="姓名/昵称" min-width="120" />
        <el-table-column prop="department" label="院系" min-width="160" />
        <el-table-column prop="verified" label="实名状态" min-width="110" />
        <el-table-column prop="credit" label="信用分" min-width="90" />
        <el-table-column prop="contact" label="联系方式" min-width="160" />
        <el-table-column prop="registeredAt" label="注册时间" min-width="170" />
        <el-table-column prop="status" label="账号状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
              {{ row.status === 'NORMAL' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="310">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">查看详情</el-button>
            <el-button link type="warning" @click="auditUser(row)">审核实名</el-button>
            <el-button link :type="row.status === 'NORMAL' ? 'danger' : 'success'" @click="toggleUserStatus(row)">
              {{ row.status === 'NORMAL' ? '禁用' : '启用' }}
            </el-button>
            <el-button link @click="viewViolations(row)">违规记录</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialog" title="用户详情" width="520">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学号">{{ currentUser.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名/昵称">{{ currentUser.name }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ currentUser.department }}</el-descriptions-item>
        <el-descriptions-item label="实名状态">{{ currentUser.verified }}</el-descriptions-item>
        <el-descriptions-item label="信用分">{{ currentUser.credit }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentUser.contact }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>
