import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const STORAGE_KEY = 'campusTradeAuth'

function readSession() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || null
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref(readSession())
  const isLoggedIn = computed(() => Boolean(user.value))
  const isAdmin = computed(() => user.value?.role === 'admin')

  function login(payload = {}) {
    user.value = {
      role: payload.role || 'user',
      nickname: payload.nickname || (payload.role === 'admin' ? '管理员' : '校园用户'),
      account: payload.account || '',
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user.value))
  }

  function logout() {
    user.value = null
    localStorage.removeItem(STORAGE_KEY)
  }

  return {
    user,
    isLoggedIn,
    isAdmin,
    login,
    logout,
  }
})
