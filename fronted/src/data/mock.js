// Static option lists used by forms and filters. Product data is loaded from the backend.

export const campuses = ['校本部', '东校区', '西校区', '南校区', '大学城校区']

export const categories = [
  '教材教辅',
  '数码3C',
  '生活日用',
  '服饰鞋包',
  '运动户外',
  '其他',
]

export const categoryTags = [
  { id: 1, category: '教材教辅', name: '公共课教材' },
  { id: 2, category: '教材教辅', name: '考研资料' },
  { id: 3, category: '教材教辅', name: '四六级资料' },
  { id: 4, category: '数码3C', name: '手机平板' },
  { id: 5, category: '数码3C', name: '电脑配件' },
  { id: 6, category: '数码3C', name: '耳机音箱' },
  { id: 7, category: '生活日用', name: '宿舍电器' },
  { id: 8, category: '生活日用', name: '收纳清洁' },
  { id: 9, category: '生活日用', name: '床上用品' },
  { id: 10, category: '服饰鞋包', name: '箱包' },
  { id: 11, category: '服饰鞋包', name: '鞋服' },
  { id: 12, category: '服饰鞋包', name: '配饰' },
  { id: 13, category: '运动户外', name: '球类' },
  { id: 14, category: '运动户外', name: '健身器材' },
  { id: 15, category: '运动户外', name: '骑行装备' },
  { id: 16, category: '其他', name: '票券' },
  { id: 17, category: '其他', name: '乐器' },
  { id: 18, category: '其他', name: '杂物' },
]

export const conditions = ['全新', '9成新', '8成新', '轻微使用', '明显使用']

export const conditionTagMap = {
  全新: 'success',
  '9成新': 'warning',
  '8成新': 'primary',
  轻微使用: 'info',
  明显使用: 'danger',
  NEW: 'success',
  LIKE_NEW: 'warning',
  GOOD: 'primary',
  FAIR: 'info',
}

export const products = []
