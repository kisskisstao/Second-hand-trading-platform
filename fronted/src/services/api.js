import axios from 'axios'

export const api = axios.create({
  baseURL: '/api',
  timeout: 8000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || error.message || 'Request failed'
    return Promise.reject(new Error(message))
  },
)

export function checkHealth() {
  return api.get('/health')
}

export const authApi = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  adminLogin: (data) => api.post('/auth/admin/login', data),
}

export const userApi = {
  getMe: () => api.get('/users/me'),
  updateMe: (data) => api.put('/users/me', data),
  getMyItems: (params) => api.get('/users/me/items', { params }),
  getMyFavorites: (params) => api.get('/users/me/favorites', { params }),
  getReviews: (userId, params) => api.get(`/users/${userId}/reviews`, { params }),
}

export const fileApi = {
  uploadImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/files/images', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}

export const categoryApi = {
  list: () => api.get('/categories'),
}

export const itemApi = {
  create: (data) => api.post('/items', data),
  list: (params) => api.get('/items', { params }),
  detail: (itemId) => api.get(`/items/${itemId}`),
  update: (itemId, data) => api.put(`/items/${itemId}`, data),
  remove: (itemId) => api.patch(`/items/${itemId}/remove`),
  favorite: (itemId) => api.post(`/items/${itemId}/favorite`),
  unfavorite: (itemId) => api.delete(`/items/${itemId}/favorite`),
  comments: (itemId) => api.get(`/items/${itemId}/comments`),
  createComment: (itemId, data) => api.post(`/items/${itemId}/comments`, data),
}

export const orderApi = {
  create: (data) => api.post('/orders', data),
  list: (params) => api.get('/orders', { params }),
  detail: (orderId) => api.get(`/orders/${orderId}`),
  accept: (orderId) => api.patch(`/orders/${orderId}/accept`),
  cancel: (orderId, data) => api.patch(`/orders/${orderId}/cancel`, data),
  complete: (orderId) => api.patch(`/orders/${orderId}/complete`),
  review: (orderId, data) => api.post(`/orders/${orderId}/reviews`, data),
}

export const wantedApi = {
  create: (data) => api.post('/wanted-posts', data),
  list: (params) => api.get('/wanted-posts', { params }),
  close: (postId) => api.patch(`/wanted-posts/${postId}/close`),
}

export const swapApi = {
  create: (data) => api.post('/swap-requests', data),
  list: (params) => api.get('/swap-requests', { params }),
  accept: (requestId) => api.patch(`/swap-requests/${requestId}/accept`),
  reject: (requestId, data) => api.patch(`/swap-requests/${requestId}/reject`, data),
  cancel: (requestId) => api.patch(`/swap-requests/${requestId}/cancel`),
}

export const chatApi = {
  list: () => api.get('/chats'),
  messages: (chatId, params) => api.get(`/chats/${chatId}/messages`, { params }),
  sendMessage: (chatId, data) => api.post(`/chats/${chatId}/messages`, data),
}

export const adminApi = {
  dashboard: () => api.get('/admin/dashboard'),
  users: (params) => api.get('/admin/users', { params }),
  disableUser: (userId) => api.patch(`/admin/users/${userId}/disable`),
  enableUser: (userId) => api.patch(`/admin/users/${userId}/enable`),
  items: (params) => api.get('/admin/items', { params }),
  removeItem: (itemId, data) => api.patch(`/admin/items/${itemId}/remove`, data),
  deleteItem: (itemId) => api.delete(`/admin/items/${itemId}`),
  categories: () => api.get('/admin/categories'),
  createCategory: (data) => api.post('/admin/categories', data),
  updateCategory: (categoryId, data) => api.put(`/admin/categories/${categoryId}`, data),
  deleteCategory: (categoryId) => api.delete(`/admin/categories/${categoryId}`),
  orders: (params) => api.get('/admin/orders', { params }),
  disputes: (params) => api.get('/admin/disputes', { params }),
  resolveDispute: (disputeId, data) => api.patch(`/admin/disputes/${disputeId}/resolve`, data),
  reports: (params) => api.get('/admin/reports', { params }),
  approveReport: (reportId, data) => api.patch(`/admin/reports/${reportId}/approve`, data),
  rejectReport: (reportId, data) => api.patch(`/admin/reports/${reportId}/reject`, data),
  settings: () => api.get('/admin/settings'),
  updateSettings: (data) => api.put('/admin/settings', data),
  notices: (params) => api.get('/admin/notices', { params }),
  createNotice: (data) => api.post('/admin/notices', data),
  updateNotice: (noticeId, data) => api.put(`/admin/notices/${noticeId}`, data),
  deleteNotice: (noticeId) => api.delete(`/admin/notices/${noticeId}`),
}
