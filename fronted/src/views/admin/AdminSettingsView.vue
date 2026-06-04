<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { sensitiveWords } from '../../data/adminMock'

const words = ref([...sensitiveWords])
const newWord = ref('')

const payment = reactive({
  wechatAppId: 'wx-campus-demo',
  alipayAppId: 'alipay-campus-demo',
  campusCardMerchant: 'CAMPUS-2026',
})

const rules = reactive({
  maxImages: 9,
  disputeDays: 3,
  creditDeduction: 10,
  tradeTip: '建议优先同校区面交，贵重数码商品需现场验机。',
})

function addWord() {
  if (!newWord.value.trim()) {
    ElMessage.warning('请输入敏感词')
    return
  }
  words.value.push(newWord.value.trim())
  newWord.value = ''
  ElMessage.success('敏感词已新增')
}

function removeWord(word) {
  words.value = words.value.filter((item) => item !== word)
  ElMessage.success(`敏感词「${word}」已删除`)
}

function savePayment() {
  ElMessage.success('支付参数已保存')
}

function saveRules() {
  ElMessage.success('交易规则已保存')
}
</script>

<template>
  <div class="admin-page settings-grid">
    <el-card shadow="never">
      <template #header>敏感词库配置</template>
      <div class="word-add-row">
        <el-input v-model="newWord" placeholder="新增 IM 敏感词" />
        <el-button type="primary" @click="addWord">新增</el-button>
      </div>
      <div class="tag-stack">
        <el-tag v-for="word in words" :key="word" closable type="warning" @close="removeWord(word)">
          {{ word }}
        </el-tag>
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
        <el-button type="primary" @click="savePayment">保存支付参数</el-button>
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
        <el-button type="primary" @click="saveRules">保存交易规则</el-button>
      </el-form>
    </el-card>
  </div>
</template>
