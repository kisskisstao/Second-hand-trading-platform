<template>
  <div class="order-review">
    <h3>评价订单</h3>
    
    <div class="rating-section">
      <span class="label">评分：</span>
      <div class="stars">
        <span 
          v-for="star in 5" 
          :key="star"
          class="star"
          :class="{ active: rating >= star }"
          @click="rating = star"
        >
          ★
        </span>
      </div>
    </div>

    <div class="comment-section">
      <label class="label">评价内容：</label>
      <textarea 
        v-model="content"
        placeholder="请输入您的评价..."
        maxlength="500"
      ></textarea>
      <span class="char-count">{{ content.length }}/500</span>
    </div>

    <div class="actions">
      <button class="btn-cancel" @click="$emit('cancel')">取消</button>
      <button class="btn-submit" @click="submitReview" :disabled="rating === 0">提交评价</button>
    </div>
  </div>
</template>

<script setup>import { ref } from 'vue';
import { reviewApi } from '../../services/api';
const props = defineProps({
 orderId: {
 type: Number,
 required: true
 },
 targetUserId: {
 type: Number,
 required: true
 }
});
const emit = defineEmits(['cancel', 'success']);
const rating = ref(0);
const content = ref('');
const submitReview = async () => {
 if (rating.value === 0)
 return;
 try {
 await reviewApi.create({
 orderId: props.orderId,
 targetUserId: props.targetUserId,
 rating: rating.value,
 content: content.value
 });
 emit('success');
 }
 catch (error) {
 console.error('评价失败:', error);
 alert('评价失败，请稍后重试');
 }
};
</script>

<style scoped>
.order-review {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

h3 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
}

.rating-section {
  margin-bottom: 20px;
}

.label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: #666;
}

.stars {
  display: flex;
  gap: 10px;
}

.star {
  font-size: 32px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.star.active {
  color: #ffc107;
}

.comment-section {
  margin-bottom: 20px;
}

textarea {
  width: 100%;
  height: 100px;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: vertical;
  font-size: 14px;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-cancel, .btn-submit {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-submit {
  background: #409eff;
  color: #fff;
}

.btn-submit:disabled {
  background: #b3d7ff;
  cursor: not-allowed;
}
</style>