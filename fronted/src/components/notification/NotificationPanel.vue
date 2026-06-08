<template>
  <div class="notification-panel">
    <div class="notification-header">
      <h3>系统通知</h3>
      <button class="btn-mark-all" @click="markAllRead">全部已读</button>
    </div>

    <div class="notifications">
      <div v-if="notifications.length === 0" class="empty">
        暂无通知
      </div>
      <div 
        v-for="notification in notifications" 
        :key="notification.notificationId"
        class="notification-item"
        :class="{ unread: !notification.readAt }"
        @click="markAsRead(notification.notificationId)"
      >
        <div class="notification-icon">
          <span v-if="notification.type === 'ORDER'">📦</span>
          <span v-else-if="notification.type === 'SYSTEM'">📢</span>
          <span v-else>🔔</span>
        </div>
        <div class="notification-content">
          <h4>{{ notification.title }}</h4>
          <p>{{ notification.content }}</p>
          <span class="time">{{ formatTime(notification.createdAt) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, onMounted, onUnmounted } from 'vue';
import { userApi } from '../../services/api';
const notifications = ref([]);
const formatTime = (dateStr) => {
 if (!dateStr)
 return '';
 const date = new Date(dateStr);
 return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`;
};
const loadNotifications = async () => {
 try {
 const res = await userApi.getMyNotifications();
 notifications.value = res.data || [];
 }
 catch (error) {
 console.error('加载通知失败:', error);
 }
};
const markAsRead = async (notificationId) => {
 console.log('标记已读:', notificationId);
};
const markAllRead = () => {
 notifications.value.forEach(n => n.readAt = new Date().toISOString());
};
let refreshInterval;
onMounted(() => {
 loadNotifications();
 refreshInterval = setInterval(loadNotifications, 30000);
});
onUnmounted(() => {
 if (refreshInterval) {
 clearInterval(refreshInterval);
 }
});
</script>

<style scoped>
.notification-panel {
  width: 320px;
  max-height: 400px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
}

.btn-mark-all {
  font-size: 12px;
  color: #409eff;
  background: none;
  border: none;
  cursor: pointer;
}

.notifications {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.empty {
  text-align: center;
  color: #999;
  padding: 30px;
}

.notification-item {
  display: flex;
  gap: 10px;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f8f9fa;
}

.notification-item.unread {
  background: #f0f7ff;
}

.notification-icon {
  font-size: 20px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

h4 {
  margin: 0 0 4px 0;
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

p {
  margin: 0 0 4px 0;
  font-size: 12px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.time {
  font-size: 11px;
  color: #999;
}
</style>