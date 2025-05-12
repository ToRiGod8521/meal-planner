<template>
    <div class="max-w-md mx-auto p-6">
      <h2 class="text-2xl mb-4">我的個人資料</h2>
      <form @submit.prevent="onSubmit">
        <div class="mb-4">
          <label class="block mb-1">全名</label>
          <input v-model="form.fullName" required class="w-full border px-2 py-1" />
        </div>
        <div class="mb-4">
          <label class="block mb-1">年齡</label>
          <input v-model.number="form.age" type="number" min="0" required class="w-full border px-2 py-1" />
        </div>
        <div class="mb-4">
          <label class="block mb-1">身高 (cm)</label>
          <input v-model.number="form.height" type="number" min="0" step="0.1" required class="w-full border px-2 py-1" />
        </div>
        <div class="mb-4">
          <label class="block mb-1">體重 (kg)</label>
          <input v-model.number="form.weight" type="number" min="0" step="0.1" required class="w-full border px-2 py-1" />
        </div>
        <h3 class="text-lg mt-6 mb-2">飲食偏好（葷素擇一）</h3>
        <div class="mb-4 flex items-center space-x-4">
        <label class="inline-flex items-center">
          <input
            type="radio"
            value="vegetarian"
            v-model="form.dietType"
            class="mr-2"
          />
          素食者
        </label>
        <label class="inline-flex items-center">
          <input
            type="radio"
            value="meatEater"
            v-model="form.dietType"
            class="mr-2"
          />
          葷食者
        </label>
      </div>

      <!-- 熱量／蛋白質 -->
      <div class="mb-4">
        <label class="block mb-1">每日熱量目標 (kcal)</label>
        <input v-model.number="form.calorieGoal" type="number" min="0" required class="w-full border px-2 py-1" />
      </div>
      <div class="mb-4">
        <label class="block mb-1">每日蛋白質目標 (g)</label>
        <input v-model.number="form.proteinGoal" type="number" min="0" required class="w-full border px-2 py-1" />
      </div>

        <div v-if="error" class="mb-4 text-red-500">{{ error }}</div>
        <button
          type="submit"
          :disabled="loading"
          class="px-4 py-2 bg-green-600 text-white rounded disabled:opacity-50"
        >
          {{ loading ? '儲存中…' : '儲存資料' }}
        </button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  const loading = ref(false)
  const error = ref(null)

  const form = ref({
    fullName: '',
    age: null,
    height: null,
    weight: null,
    // 飲食偏好欄位
    calorieGoal: null,
    proteinGoal: null,
    dietType:    '',  // "vegetarian" 或 "meatEater"
  })
  
  
  async function loadProfile() {
    loading.value = true
    error.value = null
    try {
      const res = await axios.get('/api/profile')
      const data = res.data
      form.value.fullName = data.fullName
      form.value.age = data.age
      form.value.height = data.height
      form.value.weight = data.weight
      if (data.vegetarian) form.value.dietType = 'vegetarian'
      else if (data.meatEater) form.value.dietType = 'meatEater'
      else form.value.dietType = ''
      form.value.calorieGoal = data.calorieGoal
      form.value.proteinGoal = data.proteinGoal

      // // 解析 JSON 字串到欄位
      // let prefs = {}
      // try { prefs = JSON.parse(data.dietPreferences) } catch {}
      // form.value.calories   = prefs.calories   ?? null
      // form.value.protein    = prefs.protein    ?? null
      // form.value.vegetarian = prefs.vegetarian ?? false
      // form.value.meateater  = prefs.meateater      ?? false

    } catch (err) {
      if (err.response?.status === 401 || err.response?.status === 403) {
         // token 過期或未授權：導回登入
        localStorage.removeItem('jwt')
        router.push({ name: 'login' })
      } else {
        error.value = '讀取個人資料失敗'
      }
    } finally {
      loading.value = false
    }
  }
  
  async function onSubmit() {
    if (!form.value.dietType) {
    error.value = '請選擇「素食者」或「葷食者」'
    return
    }
    loading.value = true
    error.value = null
    try {
      const payload = {
      fullName:    form.value.fullName,
      age:         form.value.age,
      height:      form.value.height,
      weight:      form.value.weight,
      calorieGoal: form.value.calorieGoal,
      proteinGoal: form.value.proteinGoal,
      vegetarian:  form.value.dietType === 'vegetarian',
      meatEater:   form.value.dietType === 'meatEater'
      }
    await axios.put('/api/profile', payload)
    alert('資料已儲存！')
    } catch {
      error.value = '儲存失敗'
    } finally {
      loading.value = false
    }
  }
  
  onMounted(loadProfile)
  </script>
  
  <style scoped>
  /* 自行調整樣式 */
  </style>