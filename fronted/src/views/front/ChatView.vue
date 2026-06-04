<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Goods, Picture, Promotion } from '@element-plus/icons-vue'
import { chatMessages, contacts } from '../../data/mock'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const activeId = ref(contacts[0].id)
const input = ref('')
const localMessages = ref([...chatMessages])

const activeContact = computed(() => contacts.find((contact) => contact.id === activeId.value) || contacts[0])
const sensitiveHint = computed(() => {
  const words = ['私下转账', '押金', '先付款', '脱离平台']
  return words.find((word) => input.value.includes(word))
})

function sendText() {
  if (!input.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  if (sensitiveHint.value) {
    ElMessage.warning('消息包含敏感词，请修改后再发送')
    return
  }

  localMessages.value.push({
    id: Date.now(),
    from: 'me',
    text: input.value.trim(),
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
  })
  input.value = ''
  ElMessage.success('消息已发送')
}

function sendImage() {
  ElMessage.success('图片已加入待发送队列')
}

function sendProductCard() {
  localMessages.value.push({
    id: Date.now(),
    from: 'me',
    text: `商品卡片：${activeContact.value.product.title}`,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
  })
  ElMessage.success('商品卡片已发送')
}
</script>

<template>
  <main class="page-wrap chat-page">
    <el-card v-if="!authStore.isLoggedIn" class="auth-required-card" shadow="never">
      <h1>还没登录</h1>
      <p>登录或注册后才能进入 IM 聊天，联系买家或卖家。</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="router.push('/login')">前往登录</el-button>
        <el-button size="large" @click="router.push('/register')">前往注册</el-button>
      </div>
    </el-card>

    <section v-else class="chat-shell">
      <aside class="chat-contacts">
        <h2>消息</h2>
        <button
          v-for="contact in contacts"
          :key="contact.id"
          :class="{ active: contact.id === activeId }"
          type="button"
          @click="activeId = contact.id"
        >
          <el-badge :value="contact.unread" :hidden="!contact.unread">
            <el-avatar>{{ contact.name.slice(0, 1) }}</el-avatar>
          </el-badge>
          <span>
            <strong>{{ contact.name }}</strong>
            <small>{{ contact.role }} · {{ contact.last }}</small>
          </span>
        </button>
      </aside>

      <section class="chat-window">
        <header class="chat-header">
          <div>
            <h1>{{ activeContact.name }}</h1>
            <p>{{ activeContact.product.title }}</p>
          </div>
          <el-tag type="warning">{{ activeContact.role }}</el-tag>
        </header>

        <div class="chat-product-card">
          <el-image :src="activeContact.product.image" fit="cover" />
          <div>
            <strong>{{ activeContact.product.title }}</strong>
            <p>￥{{ activeContact.product.price }} · {{ activeContact.product.campus }}</p>
          </div>
          <el-button type="primary" plain :icon="Goods" @click="sendProductCard">发送商品卡</el-button>
        </div>

        <div class="message-list">
          <div
            v-for="message in localMessages"
            :key="message.id"
            class="message-bubble"
            :class="message.from"
          >
            <p>{{ message.text }}</p>
            <small>{{ message.time }}</small>
          </div>
        </div>

        <footer class="chat-input">
          <el-alert
            v-if="sensitiveHint"
            :title="`检测到敏感词“${sensitiveHint}”，建议在平台内完成沟通与交易。`"
            type="warning"
            :closable="false"
            show-icon
          />
          <div class="chat-toolbar">
            <el-button :icon="Picture" @click="sendImage">图片</el-button>
            <el-button :icon="Goods" @click="sendProductCard">商品卡</el-button>
          </div>
          <div class="chat-send-row">
            <el-input
              v-model="input"
              type="textarea"
              :rows="3"
              placeholder="输入消息，避免发送联系方式、押金、私下转账等风险内容"
              @keyup.enter.exact="sendText"
            />
            <el-button type="primary" size="large" :icon="Promotion" @click="sendText">发送</el-button>
          </div>
        </footer>
      </section>
    </section>
  </main>
</template>
