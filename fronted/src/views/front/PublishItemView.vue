<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { campuses, categories, conditions } from '../../data/mock'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const fileList = ref([])

const form = reactive({
  title: '',
  desc: '',
  price: '',
  originalPrice: '',
  condition: '',
  category: '',
  campus: '',
  dorm: '',
  tradeModes: ['面交'],
  status: '上架',
})

function formatText(command) {
  ElMessage.info(`${command}功能已记录，后续接入富文本编辑器`)
}

function validateForm() {
  if (!form.title.trim()) return '请填写商品标题'
  if (!form.desc.trim()) return '请填写详细描述'
  if (!form.price && form.price !== 0) return '请填写售价'
  if (!form.condition) return '请选择成色'
  if (!form.category) return '请选择商品分类'
  if (!form.campus) return '请选择校区'
  if (!form.tradeModes.length) return '至少选择一种交易模式'
  return ''
}

function saveDraft() {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  form.status = '存草稿'
  ElMessage.success('草稿已保存')
}

function submitItem() {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }

  const error = validateForm()
  if (error) {
    ElMessage.warning(error)
    return
  }

  ElMessage.success('商品已提交发布')
  router.push('/')
}
</script>

<template>
  <main class="page-wrap publish-page">
    <el-card v-if="!authStore.isLoggedIn" class="auth-required-card" shadow="never">
      <h1>还没登录</h1>
      <p>登录或注册后才能发布校园闲置商品。</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="router.push('/login')">前往登录</el-button>
        <el-button size="large" @click="router.push('/register')">前往注册</el-button>
      </div>
    </el-card>

    <template v-else>
    <div class="page-title">
      <p class="section-kicker">免费发布</p>
      <h1>发布校园闲置商品</h1>
    </div>

    <el-form label-position="top" class="publish-form">
      <el-card shadow="never" class="form-card">
        <template #header>多图上传</template>
        <el-upload
          v-model:file-list="fileList"
          drag
          multiple
          list-type="picture-card"
          :auto-upload="false"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖拽图片到此处，或点击上传</div>
        </el-upload>
      </el-card>

      <el-card shadow="never" class="form-card">
        <template #header>基础信息</template>
        <el-form-item label="商品标题">
          <el-input v-model="form.title" maxlength="40" show-word-limit placeholder="例如：高等数学教材九成新" />
        </el-form-item>
        <el-form-item label="详细描述">
          <div class="rich-editor">
            <div class="rich-toolbar">
              <el-button size="small" @click="formatText('加粗')">加粗</el-button>
              <el-button size="small" @click="formatText('清单')">清单</el-button>
              <el-button size="small" @click="formatText('插图')">插图</el-button>
            </div>
            <el-input
              v-model="form.desc"
              type="textarea"
              :rows="7"
              placeholder="描述购买时间、使用情况、瑕疵、配件、可交易时间"
            />
          </div>
        </el-form-item>

        <div class="two-column-form">
          <el-form-item label="售价">
            <el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" />
          </el-form-item>
          <el-form-item label="原价">
            <el-input-number v-model="form.originalPrice" :min="0" :precision="2" controls-position="right" />
          </el-form-item>
          <el-form-item label="成色">
            <el-select v-model="form.condition" placeholder="选择成色">
              <el-option v-for="condition in conditions" :key="condition" :label="condition" :value="condition" />
            </el-select>
          </el-form-item>
        </div>
      </el-card>

      <el-card shadow="never" class="form-card">
        <template #header>属性选择</template>
        <div class="two-column-form">
          <el-form-item label="商品分类">
            <el-select v-model="form.category" placeholder="选择分类">
              <el-option v-for="category in categories" :key="category" :label="category" :value="category" />
            </el-select>
          </el-form-item>
          <el-form-item label="校区">
            <el-select v-model="form.campus" placeholder="选择校区">
              <el-option v-for="campus in campuses" :key="campus" :label="campus" :value="campus" />
            </el-select>
          </el-form-item>
          <el-form-item label="宿舍楼/教学楼">
            <el-cascader
              v-model="form.dorm"
              :options="[
                { label: '桃李园', value: '桃李园', children: [{ label: '3 栋', value: '3 栋' }] },
                { label: '东苑', value: '东苑', children: [{ label: '6 栋', value: '6 栋' }] },
                { label: '西苑', value: '西苑', children: [{ label: '2 栋', value: '2 栋' }] },
              ]"
              placeholder="选择精确位置"
            />
          </el-form-item>
        </div>
      </el-card>

      <el-card shadow="never" class="form-card">
        <template #header>交易设置</template>
        <el-form-item label="交易模式">
          <el-checkbox-group v-model="form.tradeModes">
            <el-checkbox label="面交" />
            <el-checkbox label="线上担保" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="form.status">
            <el-radio-button label="上架" />
            <el-radio-button label="存草稿" />
          </el-radio-group>
        </el-form-item>
      </el-card>

      <div class="sticky-submit">
        <el-button size="large" @click="saveDraft">保存草稿</el-button>
        <el-button type="primary" size="large" @click="submitItem">提交发布</el-button>
      </div>
    </el-form>
    </template>
  </main>
</template>
