<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../services/api'
import { normalizeOrder } from '../../services/normalizers'

const orders = ref([])
const disputes = ref([])
const loading = ref(false)
const filters = reactive({
  status: '',
  mode: '',
  time: '',
})

const filteredOrders = computed(() =>
  orders.value.filter((order) => {
    const statusMatched = !filters.status || order.status === filters.status
    const modeMatched = !filters.mode || order.tradeMode === filters.mode
    const timeMatched =
      !Array.isArray(filters.time) ||
      filters.time.length !== 2 ||
      (new Date(order.createdAt) >= new Date(filters.time[0]) &&
        new Date(order.createdAt) <= new Date(filters.time[1]))
    return statusMatched && modeMatched && timeMatched
  }),
)

function normalizeDispute(row = {}) {
  return {
    id: row.disputeId || row.id,
    orderId: row.orderId,
    buyer: row.buyerName || `用户${row.buyerId || ''}`,
    seller: row.sellerName || `用户${row.sellerId || ''}`,
    product: row.itemTitle || '',
    reason: row.reason || '',
    status: row.status || 'PENDING',
    amount: Number(row.amount || 0),
    createdAt: String(row.createdAt || '').replace('T', ' ').slice(0, 16),
  }
}

async function loadData() {
  loading.value = true
  try {
    const [orderResponse, disputeResponse] = await Promise.all([
      adminApi.orders({ page: 1, pageSize: 100 }),
      adminApi.disputes({ page: 1, pageSize: 100 }),
    ])
    orders.value = (orderResponse.data?.list || []).map(normalizeOrder)
    disputes.value = (disputeResponse.data?.list || []).map(normalizeDispute)
  } catch (error) {
    orders.value = []
    disputes.value = []
    ElMessage.error(error.message || '订单数据加载失败')
  } finally {
    loading.value = false
  }
}

function resolveDispute(row, result) {
  ElMessageBox.confirm(`确认对纠纷 ${row.id} 执行“${result}”？`, '管理员仲裁', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: result === '同意退款' ? 'warning' : 'info',
  }).then(async () => {
    try {
      await adminApi.resolveDispute(row.id, { result, remark: result })
      ElMessage.success('仲裁结果已提交')
      loadData()
    } catch (error) {
      ElMessage.error(error.message || '纠纷处理失败')
    }
  }).catch(() => {})
}

onMounted(loadData)
</script>

<template>
  <div class="admin-page">
    <el-card shadow="never" class="admin-filter-card">
      <el-form label-position="top">
        <div class="admin-filter-grid">
          <el-form-item label="订单状态">
            <el-select v-model="filters.status" clearable placeholder="全部状态">
              <el-option label="待预约" value="PENDING" />
              <el-option label="已接单" value="ACCEPTED" />
              <el-option label="支付中" value="PAYING" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="交易模式">
            <el-select v-model="filters.mode" clearable placeholder="全部模式">
              <el-option label="校内面交" value="OFFLINE" />
              <el-option label="线上担保" value="ESCROW" />
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
          <el-table v-loading="loading" :data="filteredOrders" stripe>
            <el-table-column prop="orderNo" label="订单号" min-width="170" />
            <el-table-column prop="product.title" label="商品" min-width="220" />
            <el-table-column prop="buyer.nickname" label="买家" min-width="120" />
            <el-table-column prop="seller.nickname" label="卖家" min-width="120" />
            <el-table-column prop="status" label="状态" min-width="110" />
            <el-table-column prop="tradeMode" label="交易模式" min-width="120" />
            <el-table-column prop="amount" label="金额" min-width="100">
              <template #default="{ row }">￥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" min-width="170" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="纠纷订单">
          <el-table v-loading="loading" :data="disputes" stripe>
            <el-table-column prop="id" label="纠纷编号" min-width="120" />
            <el-table-column prop="orderId" label="订单号" min-width="120" />
            <el-table-column prop="product" label="商品" min-width="180" />
            <el-table-column prop="buyer" label="买家" min-width="120" />
            <el-table-column prop="seller" label="卖家" min-width="120" />
            <el-table-column prop="reason" label="纠纷原因" min-width="260" />
            <el-table-column prop="status" label="状态" min-width="110" />
            <el-table-column prop="amount" label="金额" min-width="100">
              <template #default="{ row }">￥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column label="管理员仲裁" fixed="right" width="220">
              <template #default="{ row }">
                <el-button link type="success" :disabled="row.status !== 'PENDING'" @click="resolveDispute(row, '同意退款')">同意退款</el-button>
                <el-button link type="danger" :disabled="row.status !== 'PENDING'" @click="resolveDispute(row, '驳回申诉')">驳回申诉</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>
