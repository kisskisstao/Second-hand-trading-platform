<script setup>
import { computed, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminDisputes, adminOrders } from '../../data/adminMock'

const filters = reactive({
  status: '',
  mode: '',
  time: '',
})

const filteredOrders = computed(() =>
  adminOrders.filter((order) => {
    const statusMatched = !filters.status || order.status === filters.status
    const modeMatched = !filters.mode || order.mode === filters.mode
    const timeMatched =
      !Array.isArray(filters.time) ||
      filters.time.length !== 2 ||
      (new Date(order.createdAt) >= new Date(filters.time[0]) &&
        new Date(order.createdAt) <= new Date(filters.time[1]))
    return statusMatched && modeMatched && timeMatched
  }),
)

function resolveDispute(row, result) {
  ElMessageBox.confirm(`确认对纠纷 ${row.id} 执行「${result}」？`, '管理员仲裁', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: result === '同意退款' ? 'warning' : 'info',
  }).then(() => {
    ElMessage.success(`仲裁结果已提交：${result}`)
  }).catch(() => {})
}
</script>

<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-filter-card">
      <el-form label-position="top">
        <div class="admin-filter-grid">
          <el-form-item label="订单状态">
            <el-select v-model="filters.status" clearable placeholder="全部状态">
              <el-option label="待预订" value="待预订" />
              <el-option label="待面交" value="待面交" />
              <el-option label="待收货" value="待收货" />
              <el-option label="已完成" value="已完成" />
              <el-option label="纠纷中" value="纠纷中" />
            </el-select>
          </el-form-item>
          <el-form-item label="交易模式">
            <el-select v-model="filters.mode" clearable placeholder="全部模式">
              <el-option label="校内面交" value="校内面交" />
              <el-option label="线上担保" value="线上担保" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间">
            <el-date-picker v-model="filters.time" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" />
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-tabs>
        <el-tab-pane label="全平台订单">
          <el-table :data="filteredOrders" stripe>
            <el-table-column prop="id" label="订单号" min-width="160" />
            <el-table-column prop="product" label="商品" min-width="220" />
            <el-table-column prop="buyer" label="买家" min-width="100" />
            <el-table-column prop="seller" label="卖家" min-width="120" />
            <el-table-column prop="status" label="状态" min-width="110">
              <template #default="{ row }">
                <el-tag :type="row.status === '纠纷中' ? 'danger' : 'warning'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="mode" label="交易模式" min-width="120" />
            <el-table-column prop="amount" label="金额" min-width="100">
              <template #default="{ row }">￥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" min-width="170" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="纠纷订单">
          <el-table :data="adminDisputes" stripe>
            <el-table-column prop="id" label="纠纷编号" min-width="160" />
            <el-table-column prop="orderId" label="订单号" min-width="160" />
            <el-table-column prop="buyer" label="买家" min-width="100" />
            <el-table-column prop="seller" label="卖家" min-width="120" />
            <el-table-column prop="reason" label="纠纷原因" min-width="260" />
            <el-table-column prop="status" label="状态" min-width="110" />
            <el-table-column prop="amount" label="金额" min-width="100">
              <template #default="{ row }">￥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column label="管理员仲裁" fixed="right" width="220">
              <template #default="{ row }">
                <el-button link type="success" @click="resolveDispute(row, '同意退款')">同意退款</el-button>
                <el-button link type="danger" @click="resolveDispute(row, '驳回申诉')">驳回申诉</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>
