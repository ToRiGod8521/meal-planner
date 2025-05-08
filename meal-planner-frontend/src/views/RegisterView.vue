<template>
    <div class="max-w-md mx-auto p-6">
        <h2 class="text-2x1 mb-4">註冊帳號</h2>
        <form @submit.prevent="onSubmit">
            <div class="mb-4">
                <label class="block mb-1">使用者名稱</label>
                <input v-model="form.username" required class="w-full border px-2 py-1"/>
            </div>
            <div class="mb-4">
                <label class="block mb-1">電子信箱</label>
                <input v-model="form.email" type="email" required class="w-full border px-2 py-1" />
            </div>
            <div class="mb-4">
                <label class="block mb-1">密碼</label>
                <input v-model="form.password" type="password" required class="w-full border px-2 py-1" />
            </div>
            <div class="mb-4">
                <label class="block mb-1">再次輸入密碼</label>
                <input v-model="form.confirm" type="password" required class="w-full border px-2 py-1" />
            </div>
            <div v-if="error" class="mb-4 text-red-500">{{ error }}</div>
            <button
                type="submit"
                :disabled="loading"
                class="px-4 py-2 bg-green-600 text-white rounded disabled:opacity-50"
            >
            {{ loading ? '註冊中…' : '註冊' }}
            </button>
      <p class="mt-4 text-sm">
        已有帳號？<router-link to="/login" class="text-blue-600">立即登入</router-link>
      </p>
        </form>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import axios from 'axios'
    import { useRouter } from 'vue-router'

    const router = useRouter()
    const form = ref({ username: '', email: '', password: '', confirm: '' })
    const loading = ref(false)
    const error = ref(null)

    async function onSubmit() {
  error.value = null
  if (form.value.password !== form.value.confirm) {
    error.value = '兩次密碼輸入不一致'
    return
  }
  loading.value = true

  try {
    //註冊
    await axios.post('/api/auth/register', {
      username: form.value.username,
      email: form.value.email,
      password: form.value.password
    })

    //自動取得token
    const loginRes = await axios.post('/api/auth/login',{
        username: form.value.username,
        password: form.value.password
    })
    const token = loginRes.data.token
    localStorage.setItem('jwt',token)

    //導向到 profile 新增/編輯頁
    router.push({ name: 'profile' })


  } catch (err) {
    error.value = err.response?.data?.error || '註冊或登入失敗'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>

</style>
