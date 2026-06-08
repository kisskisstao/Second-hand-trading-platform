<template>
  <div class="review-list">
    <div v-if="stats" class="stats-section">
      <div class="stat-item">
        <span class="stat-value">{{ stats.averageRating || 0 }}</span>
        <span class="stat-label">平均评分</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.reviewCount || 0 }}</span>
        <span class="stat-label">评价数量</span>
      </div>
    </div>

    <div class="reviews">
      <div v-if="reviews.length === 0" class="empty">
        暂无评价
      </div>
      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <div class="stars">
            <span 
              v-for="star in 5" 
              :key="star"
              class="star"
              :class="{ active: review.rating >= star }"
            >
              ★
            </span>
          </div>
          <span class="time">{{ formatTime(review.createdAt) }}</span>
        </div>
        <p class="review-content">{{ review.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted } from 'vue';
import { reviewApi } from '../../services/api';
const props = defineProps({
 userId: {
 type: Number,
 required: true
 }
});
const reviews = ref([]);
const stats = ref(null);
const formatTime = (dateStr) => {
 if (!dateStr)
 return '';
 const date = new Date(dateStr);
 return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};
const loadReviews = async () => {
 try {
 const [reviewsRes, statsRes] = await Promise.all([
 reviewApi.getUserReviews(props.userId),
 reviewApi.getUserRatingStats(props.userId)
 ]);
 reviews.value = reviewsRes.data || [];
 stats.value = statsRes.data || null;
 }
 catch (error) {
 console.error('加载评价失败:', error);
 }
};
onMounted(() => {
 loadReviews();
});
</script>

<style scoped>
.review-list {
  padding: 10px;
}

.stats-section {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.reviews {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40px;
}

.review-item {
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.stars {
  display: flex;
  gap: 4px;
}

.star {
  font-size: 16px;
  color: #ddd;
}

.star.active {
  color: #ffc107;
}

.time {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}
</style>