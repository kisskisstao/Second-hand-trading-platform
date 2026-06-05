<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import AdminChart from '../../components/admin/AdminChart.vue'
import { adminStats, campusDistribution } from '../../data/adminMock'
import { categories, products } from '../../data/mock'

const router = useRouter()

const lineOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 36, right: 20, top: 40, bottom: 32 },
  xAxis: {
    type: 'category',
    data: ['05-29', '05-30', '05-31', '06-01', '06-02', '06-03', '06-04'],
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: '交易额',
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.12 },
      itemStyle: { color: '#ff7800' },
      data: [0, 0, 0, 0, 0, 0, 0],
    },
  ],
}

const pieOption = {
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [
    {
      name: '商品占比',
      type: 'pie',
      radius: ['42%', '68%'],
      center: ['50%', '46%'],
      data: categories.map((category) => ({
        name: category,
        value: products.filter((product) => product.category === category).length,
      })),
    },
  ],
}

const barOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 38, right: 20, top: 40, bottom: 32 },
  xAxis: { type: 'category', data: campusDistribution.map((item) => item.campus) },
  yAxis: { type: 'value' },
  series: [
    {
      name: '商品数',
      type: 'bar',
      itemStyle: { color: '#ff7800', borderRadius: [6, 6, 0, 0] },
      data: campusDistribution.map((item) => item.count),
    },
  ],
}))
</script>

<template>
  <div class="admin-page">
    <section class="admin-stat-grid">
      <el-card v-for="stat in adminStats" :key="stat.label" shadow="never" class="admin-stat-card">
        <span>{{ stat.label }}</span>
        <strong>{{ stat.value }}</strong>
        <small>{{ stat.trend }} 较昨日</small>
      </el-card>
    </section>

    <section class="admin-chart-grid">
      <el-card shadow="never" class="chart-card wide">
        <template #header>近 7 日交易额走势</template>
        <AdminChart :option="lineOption" />
      </el-card>
      <el-card shadow="never" class="chart-card">
        <template #header>各分类商品占比</template>
        <AdminChart :option="pieOption" />
      </el-card>
      <el-card shadow="never" class="chart-card wide">
        <template #header>各校区商品分布</template>
        <AdminChart :option="barOption" />
      </el-card>
    </section>

    <section class="admin-quick-grid">
      <el-card shadow="hover" @click="router.push('/admin/users')">
        <strong>待审核实名用户</strong>
        <span>0 人</span>
      </el-card>
      <el-card shadow="hover" @click="router.push('/admin/reports')">
        <strong>待处理举报</strong>
        <span>0 条</span>
      </el-card>
      <el-card shadow="hover" @click="router.push('/admin/orders')">
        <strong>待仲裁纠纷</strong>
        <span>0 单</span>
      </el-card>
    </section>
  </div>
</template>
