<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, Message, School, User } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loginTab = ref(route.query.tab === 'admin' ? 'admin' : 'user')
const userLoginMode = ref('password')

const loginForm = reactive({
  account: '',
  password: '',
  email: '',
  code: '',
})

const adminForm = reactive({
  account: 'admin',
  password: 'admin123456',
})

function loginByPassword() {
  if (!loginForm.account.trim() || !loginForm.password.trim()) {
    ElMessage.warning('请输入学号/工号和密码')
    return
  }

  authStore.login({
    role: 'user',
    account: loginForm.account,
    nickname: `用户${loginForm.account}`,
  })
  ElMessage.success('登录成功')
  router.push('/profile')
}

function loginByEmail() {
  if (!loginForm.email.trim() || !loginForm.code.trim()) {
    ElMessage.warning('请输入校园邮箱和验证码')
    return
  }

  authStore.login({
    role: 'user',
    account: loginForm.email,
    nickname: '邮箱用户',
  })
  ElMessage.success('邮箱登录成功')
  router.push('/profile')
}

function sendCode() {
  if (!loginForm.email.trim()) {
    ElMessage.warning('请先输入校园邮箱')
    return
  }
  ElMessage.success('验证码已发送，请查看校园邮箱')
}

function ssoLogin() {
  authStore.login({
    role: 'user',
    account: 'sso-user',
    nickname: '校园 SSO 用户',
  })
  ElMessage.success('SSO 登录成功')
  router.push('/profile')
}

function adminLogin() {
  if (adminForm.account !== 'admin' || adminForm.password !== 'admin123456') {
    ElMessage.error('管理员账号或密码错误')
    return
  }

  authStore.login({
    role: 'admin',
    account: 'admin',
    nickname: '管理员',
  })
  ElMessage.success('管理员登录成功')
  router.push('/admin')
}
</script>

<template>
  <main class="page-wrap auth-page">
    <section class="auth-hero">
      <p class="section-kicker">校园实名 · 安全交易</p>
      <h1>登录校园二手闲置网</h1>
      <p>登录后可以发布商品、收藏商品、咨询卖家和管理订单。</p>
    </section>

    <section class="auth-layout auth-layout-single">
      <el-card class="auth-card" shadow="never">
        <el-tabs v-model="loginTab" stretch>
          <el-tab-pane label="普通用户登录" name="user">
            <el-tabs v-model="userLoginMode" class="inner-tabs">
              <el-tab-pane label="密码登录" name="password">
                <el-form label-position="top">
                  <el-form-item label="学号/工号">
                    <el-input v-model="loginForm.account" :prefix-icon="User" placeholder="请输入学号或工号" />
                  </el-form-item>
                  <el-form-item label="密码">
                    <el-input
                      v-model="loginForm.password"
                      :prefix-icon="Lock"
                      placeholder="请输入密码"
                      show-password
                      @keyup.enter="loginByPassword"
                    />
                  </el-form-item>
                  <el-button type="primary" size="large" class="full-button" @click="loginByPassword">
                    登录
                  </el-button>
                </el-form>
              </el-tab-pane>

              <el-tab-pane label="邮箱验证码" name="email">
                <el-form label-position="top">
                  <el-form-item label="校园邮箱">
                    <el-input v-model="loginForm.email" :prefix-icon="Message" placeholder="name@school.edu.cn" />
                  </el-form-item>
                  <el-form-item label="验证码">
                    <div class="inline-control">
                      <el-input v-model="loginForm.code" placeholder="6 位验证码" @keyup.enter="loginByEmail" />
                      <el-button @click="sendCode">获取验证码</el-button>
                    </div>
                  </el-form-item>
                  <el-button type="primary" size="large" class="full-button" @click="loginByEmail">
                    邮箱登录
                  </el-button>
                </el-form>
              </el-tab-pane>
            </el-tabs>

            <el-divider>快捷入口</el-divider>
            <el-button size="large" class="full-button" :icon="School" @click="ssoLogin">
              SSO 校园统一身份登录
            </el-button>
          </el-tab-pane>

          <el-tab-pane label="管理员登录" name="admin">
            <el-alert
              title="演示账号：admin / admin123456"
              type="warning"
              show-icon
              :closable="false"
            />
            <el-form class="admin-form" label-position="top">
              <el-form-item label="管理员账号">
                <el-input v-model="adminForm.account" />
              </el-form-item>
              <el-form-item label="管理员密码">
                <el-input v-model="adminForm.password" show-password @keyup.enter="adminLogin" />
              </el-form-item>
              <el-button type="primary" size="large" class="full-button" @click="adminLogin">
                进入后台管理面板
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="auth-switch-line">
          <span>还没账号？</span>
          <RouterLink to="/register">前往注册</RouterLink>
        </div>
      </el-card>
    </section>
  </main>
</template>
