<template>
    <div class="max-w-md mx-auto mt-10 p-6 bg-white shadow-md rounded">
      <h2 class="text-2xl font-semibold mb-4">登入</h2>
      <form @submit.prevent="onSubmit">
        <div class="mb-4">
          <label class="block text-sm font-medium mb-1" for="username">帳號</label>
          <input
            v-model="form.username"
            id="username"
            type="text"
            class="w-full border px-3 py-2 rounded"
            required
          />
        </div>
        <div class="mb-4">
          <label class="block text-sm font-medium mb-1" for="password">密碼</label>
          <input
            v-model="form.password"
            id="password"
            type="password"
            class="w-full border px-3 py-2 rounded"
            required
          />
        </div>
        <div v-if="error" class="mb-4 text-red-500 text-sm">
          {{ error }}
        </div>
        <button
          type="submit"
          class="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
        >
          登入
        </button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  
  const router = useRouter();
  const form = ref({ username: '', password: '' });
  const error = ref('');
  
  async function onSubmit() {
    error.value = '';
    try {
      const res = await axios.post('/api/auth/login', {
        username: form.value.username,
        password: form.value.password
      });
      const token = res.data.token;
      localStorage.setItem('jwt', token);
      // 導向菜單頁
      router.push({ name: 'meal-plan' });
    } catch (e) {
      error.value = e.response?.data?.error || '登入失敗，請檢查帳號密碼';
    }
  }
  </script>
  
  <style scoped>
  /* 如有需要可補充樣式 */
  </style>