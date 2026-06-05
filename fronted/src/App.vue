<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, DataAnalysis, House, Plus, QuestionFilled, Search, User } from '@element-plus/icons-vue'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const keyword = ref('')
const notifications = []

function goSearch() {
  router.push({
    path: '/items',
    query: {
      keyword: keyword.value,
    },
  })
}

function goPublish() {
  if (authStore.isLoggedIn) {
    router.push('/items/publish')
    return
  }

  ElMessageBox.confirm('还没登录，登录或注册后才能免费发布商品。', '需要登录', {
    confirmButtonText: '前往登录',
    cancelButtonText: '前往注册',
    distinguishCancelAndClose: true,
    type: 'warning',
  })
    .then(() => router.push('/login'))
    .catch((action) => {
      if (action === 'cancel') {
        router.push('/register')
      }
    })
}

function logout() {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<template>
  <el-config-provider>
    <div class="site-shell">
      <header class="site-header">
        <div class="topbar">
          <RouterLink class="logo" to="/">
            <span class="logo-mark">闲</span>
            <span>校园二手闲置网</span>
          </RouterLink>

          <nav class="top-links" aria-label="顶部导航">
            <RouterLink to="/">
              <el-icon><House /></el-icon>
              首页
            </RouterLink>
            <RouterLink v-if="!authStore.isLoggedIn" to="/login">登录</RouterLink>
            <RouterLink v-if="!authStore.isLoggedIn" to="/register">注册</RouterLink>
            <RouterLink v-if="authStore.isLoggedIn" to="/profile">
              <el-icon><User /></el-icon>
              个人中心
            </RouterLink>
            <RouterLink v-if="authStore.isAdmin" to="/admin">
              <el-icon><DataAnalysis /></el-icon>
              数据后台
            </RouterLink>
            <el-button v-if="authStore.isLoggedIn" link type="primary" @click="logout">
              退出
            </el-button>
            <RouterLink to="/help">
              <el-icon><QuestionFilled /></el-icon>
              帮助
            </RouterLink>
          </nav>
        </div>

        <div class="searchbar">
          <el-input
            v-model="keyword"
            class="global-search"
            size="large"
            clearable
            placeholder="搜索教材、电脑、宿舍好物"
            @keyup.enter="goSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <el-button class="search-button" size="large" type="primary" @click="goSearch">
            搜商品
          </el-button>

          <el-button class="publish-button" size="large" type="primary" @click="goPublish">
            <el-icon><Plus /></el-icon>
            免费发布商品
          </el-button>

          <el-popover placement="bottom-end" width="320" trigger="click">
            <template #reference>
              <el-badge :value="notifications.length" :hidden="notifications.length === 0" class="notice-badge">
                <el-button :icon="Bell" circle size="large" aria-label="消息通知" />
              </el-badge>
            </template>
            <div class="notice-panel">
              <h3>消息通知</h3>
              <ul v-if="notifications.length > 0">
                <li v-for="notice in notifications" :key="notice">{{ notice }}</li>
              </ul>
              <el-empty v-else description="暂无消息" :image-size="72" />
              <el-button text type="primary" @click="router.push('/chats')">查看聊天消息</el-button>
            </div>
          </el-popover>
        </div>
      </header>

      <RouterView />

      <footer class="site-footer">
        <span>校园二手闲置网，为同校区学生提供轻量、安全、可追溯的闲置交易服务。</span>
        <nav>
          <RouterLink to="/help">用户协议</RouterLink>
          <RouterLink to="/help">帮助中心</RouterLink>
          <RouterLink to="/help">校园客服</RouterLink>
        </nav>
      </footer>
    </div>
  </el-config-provider>
</template>
