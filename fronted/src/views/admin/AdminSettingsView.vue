<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../services/api'

const loading = ref(false)
const saving = ref(false)
const words = ref([])
const newWord = ref('')

const payment = reactive({
  wechatAppId: '',
  alipayAppId: '',
  campusCardMerchant: '',
})

const rules = reactive({
  maxImages: 9,
  disputeDays: 3,
  creditDeduction: 10,
  tradeTip: '',
})

function parseJson(value, fallback = {}) {
  if (!value) return fallback
  if (typeof value === 'object') return value
  try {
    return JSON.parse(value)
  } catch {
    return fallback
  }
}

async function loadSettings() {
  loading.value = true
  try {
    const response = await adminApi.settings()
    const data = response.data || {}
    const tradeRules = parseJson(data.trade_rules, {})
    const wechat = parseJson(data.payment_wechat, {})
    const alipay = parseJson(data.payment_alipay, {})
    const campusCard = parseJson(data.payment_campus_card, {})

    words.value = Array.isArray(data.sensitiveWords) ? [...data.sensitiveWords] : []
    rules.maxImages = Number(tradeRules.maxImages || 9)
    rules.disputeDays = Number(tradeRules.disputeDays || 3)
    rules.creditDeduction = Number(tradeRules.creditDeduction || 10)
    rules.tradeTip = tradeRules.tradeTip || '建议优先同校区面交，贵重商品请现场验货。'
    payment.wechatAppId = wechat.appId || ''
    payment.alipayAppId = alipay.appId || ''
    payment.campusCardMerchant = campusCard.merchant || ''
  } catch (error) {
    ElMessage.error(error.message || '系统设置加载失败')
  } finally {
    loading.value = false
  }
}

function addWord() {
  const word = newWord.value.trim()
  if (!word) {
    ElMessage.warning('请输入敏感词')
    return
  }
  if (!words.value.includes(word)) {
    words.value.push(word)
  }
  newWord.value = ''
}

function removeWord(word) {
  words.value = words.value.filter((item) => item !== word)
}

async function saveSettings() {
  saving.value = true
  try {
    await adminApi.updateSettings({
      sensitiveWords: words.value,
      wechatAppId: payment.wechatAppId,
      alipayAppId: payment.alipayAppId,
      campusCardMerchant: payment.campusCardMerchant,
      maxImages: rules.maxImages,
      disputeDays: rules.disputeDays,
      creditDeduction: rules.creditDeduction,
      tradeTip: rules.tradeTip,
    })
    ElMessage.success('系统设置已保存')
    loadSettings()
  } catch (error) {
    ElMessage.error(error.message || '系统设置保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadSettings)
</script>

<template>
  <div v-loading="loading" class="admin-page settings-grid">
    <el-card shadow="never">
      <template #header>敏感词库配置</template>
      <div class="word-add-row">
        <el-input v-model="newWord" placeholder="新增 IM 敏感词" @keyup.enter="addWord" />
        <el-button type="primary" @click="addWord">新增</el-button>
      </div>
      <div class="tag-stack">
        <el-tag v-for="word in words" :key="word" closable type="warning" @close="removeWord(word)">
          {{ word }}
        </el-tag>
        <el-tag v-if="!words.length" type="info">暂无敏感词</el-tag>
      </div>
    </el-card>

    <el-card shadow="never">
      <template #header>支付配置</template>
      <el-form label-position="top">
        <el-form-item label="微信支付 AppID">
          <el-input v-model="payment.wechatAppId" />
        </el-form-item>
        <el-form-item label="支付宝 AppID">
          <el-input v-model="payment.alipayAppId" />
        </el-form-item>
        <el-form-item label="校园卡商户号">
          <el-input v-model="payment.campusCardMerchant" />
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="saveSettings">保存支付参数</el-button>
      </el-form>
    </el-card>

    <el-card shadow="never" class="settings-wide">
      <template #header>平台交易规则配置</template>
      <el-form label-position="top">
        <div class="admin-filter-grid">
          <el-form-item label="商品最多图片数">
            <el-input-number v-model="rules.maxImages" :min="1" :max="12" />
          </el-form-item>
          <el-form-item label="纠纷可申请天数">
            <el-input-number v-model="rules.disputeDays" :min="1" :max="15" />
          </el-form-item>
          <el-form-item label="违规默认扣分">
            <el-input-number v-model="rules.creditDeduction" :min="1" :max="100" />
          </el-form-item>
        </div>
        <el-form-item label="交易安全提示">
          <el-input v-model="rules.tradeTip" type="textarea" :rows="4" />
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="saveSettings">保存交易规则</el-button>
      </el-form>
    </el-card>
  </div>
</template>
