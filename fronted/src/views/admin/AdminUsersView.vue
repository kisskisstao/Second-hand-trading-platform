<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminUsers } from '../../data/adminMock'

const detailDialog = ref(false)
const currentUser = ref(adminUsers[0] || {})

function openDetail(user) {
  currentUser.value = user
  detailDialog.value = true
}

function exportUsers() {
  if (!adminUsers.length) {
    ElMessage.info('当前没有普通用户账号可导出')
    return
  }
  ElMessage.success('用户数据导出任务已创建')
}

function batchAudit() {
  if (!adminUsers.length) {
    ElMessage.info('当前没有待审核实名用户')
    return
  }
  ElMessage.success('批量审核已完成')
}

function auditUser(user) {
  ElMessage.success(`${user.name || user.studentNo} 的实名信息已审核`)
}

function toggleUserStatus(user) {
  ElMessageBox.confirm(`确认${user.status === '正常' ? '封禁' : '解封'}该用户？`, '账号状态调整', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('账号状态已更新')
  }).catch(() => {})
}

function viewViolations(user) {
  ElMessageBox.alert(`${user.name || user.studentNo} 暂无违规记录。`, '违规记录', {
    confirmButtonText: '知道了',
  })
}
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

      <el-table :data="adminUsers" stripe>
        <el-table-column prop="studentNo" label="学号" min-width="120" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="department" label="院系" min-width="160" />
        <el-table-column prop="verified" label="实名状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="row.verified === '已实名' ? 'success' : row.verified === '待审核' ? 'warning' : 'info'">
              {{ row.verified }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="信用分" min-width="90" />
        <el-table-column prop="contact" label="联系方式" min-width="150" />
        <el-table-column prop="registeredAt" label="注册时间" min-width="170" />
        <el-table-column prop="status" label="账号状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="310">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">查看详情</el-button>
            <el-button link type="warning" @click="auditUser(row)">审核实名</el-button>
            <el-button link :type="row.status === '正常' ? 'danger' : 'success'" @click="toggleUserStatus(row)">
              {{ row.status === '正常' ? '封禁' : '解封' }}
            </el-button>
            <el-button link @click="viewViolations(row)">违规记录</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialog" title="用户详情" width="520">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学号">{{ currentUser.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentUser.name }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ currentUser.department }}</el-descriptions-item>
        <el-descriptions-item label="实名状态">{{ currentUser.verified }}</el-descriptions-item>
        <el-descriptions-item label="信用分">{{ currentUser.credit }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentUser.contact }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>
